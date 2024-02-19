package br.com.eterniaserver.eterniakamui.handlers;

import br.com.eterniaserver.eterniakamui.EterniaKamui;
import br.com.eterniaserver.eterniakamui.core.ClaimFlagService;
import br.com.eterniaserver.eterniakamui.enums.Strings;
import br.com.eterniaserver.eterniakamui.core.ClaimFlag;

import br.com.eterniaserver.eternialib.EterniaLib;

import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.events.ClaimCreatedEvent;
import me.ryanhamshire.GriefPrevention.events.ClaimDeletedEvent;
import me.ryanhamshire.GriefPrevention.events.PreventPvPEvent;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public class ClaimHandler implements Listener {

    private final EterniaKamui plugin;
    private final ClaimFlagService claimFlagService;

    private final Collection<Integer> protectedWorlds;

    public ClaimHandler(EterniaKamui plugin, ClaimFlagService claimFlagService) {
        this.plugin = plugin;
        this.claimFlagService = claimFlagService;

        this.protectedWorlds = Arrays
                .stream(plugin.getString(Strings.PROTECTED_WORLDS).split(","))
                .map(String::hashCode)
                .toList();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClaimCreated(ClaimCreatedEvent event) {
        Claim claim = event.getClaim();
        int claimId = claim.getID().intValue();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            ClaimFlag claimFlag = new ClaimFlag(claimId);
            EterniaLib.getDatabase().insert(ClaimFlag.class, claimFlag);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClaimDeleted(ClaimDeletedEvent event) {
        Claim claim = event.getClaim();
        int claimId = claim.getID().intValue();

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            EterniaLib.getDatabase().delete(ClaimFlag.class, claimId);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Optional<Claim> claim = claimFlagService.getClaimAt(event.getEntity().getLocation());
        if (claim.isEmpty()) {
            return;
        }

        Optional<ClaimFlag> claimFlag = claimFlagService.getClaimFlag(claim.get().getID().intValue());
        if (claimFlag.isPresent() && claimFlag.get().isKeepLevel()) {
            event.setDroppedExp(0);
            event.setKeepLevel(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPvP(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player defender) {
            World world = defender.getWorld();

            if (protectedWorlds.contains(world.hashCode())) {
                long time = world.getTime();
                if (time < 12300 || time > 23850) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreventPvP(PreventPvPEvent event) {
        Claim claim = event.getClaim();

        Optional<ClaimFlag> claimFlag = claimFlagService.getClaimFlag(claim.getID().intValue());
        if (claimFlag.isPresent() && claimFlag.get().isAllowPvP()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEntityExplode(EntityExplodeEvent event) {
        event.setCancelled(handleExplosion(event.getLocation()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockExplode(BlockExplodeEvent event) {
        event.setCancelled(handleExplosion(event.getBlock().getLocation()));
    }

    private boolean handleExplosion(Location location) {
        Optional<Claim> claim = claimFlagService.getClaimAt(location);
        if (claim.isEmpty()) {
            return false;
        }

        Optional<ClaimFlag> claimFlag = claimFlagService.getClaimFlag(claim.get().getID().intValue());
        return claimFlag.filter(flag -> !flag.isExplosions()).isPresent();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeavesDecay(LeavesDecayEvent event) {
        Optional<Claim> claim = claimFlagService.getClaimAt(event.getBlock().getLocation());
        if (claim.isEmpty()) {
            return;
        }

        Optional<ClaimFlag> claimFlag = claimFlagService.getClaimFlag(claim.get().getID().intValue());
        if (claimFlag.isPresent() && !claimFlag.get().isLeaveDecay()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCreaturePreSpawnEvent(PreCreatureSpawnEvent event) {
        if (event.getType().getEntityClass() == null || !Monster.class.isAssignableFrom(event.getType().getEntityClass())) {
            return;
        }

        Optional<Claim> claim = claimFlagService.getClaimAt(event.getSpawnLocation());
        if (claim.isEmpty()) {
            return;
        }

        Optional<ClaimFlag> claimFlag = claimFlagService.getClaimFlag(claim.get().getID().intValue());
        if (claimFlag.isPresent() && !claimFlag.get().isMonsterSpawn()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEntityInventoryClick(InventoryClickEvent e) {
        if (e.getView().getOriginalTitle().equals(ClaimFlag.GUI_NAME)) {
            menuGui((Player) e.getWhoClicked(), e.getSlot(), e);
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        Optional<Claim> claim = claimFlagService.getClaimAt(event.getToBlock().getLocation());
        if (claim.isEmpty()) {
            return;
        }

        Optional<ClaimFlag> claimFlag = claimFlagService.getClaimFlag(claim.get().getID().intValue());
        if (claimFlag.isPresent() && !claimFlag.get().isLiquidFluid()) {
            event.setCancelled(true);
        }
    }

    private void menuGui(Player player, int slotInt, InventoryClickEvent event) {
        if (slotInt < 0 || slotInt >= ClaimFlag.FLAGS_LENGTH) {
            return;
        }

        Optional<Claim> claim = claimFlagService.getClaimCachedAt(player);
        if (claim.isEmpty()) {
            return;
        }

        Optional<ClaimFlag> claimFlagOptional = claimFlagService.getClaimFlag(claim.get().getID().intValue());
        if (claimFlagOptional.isEmpty()) {
            return;
        }

        ClaimFlag claimFlag = claimFlagOptional.get();

        boolean flagValue = claimFlag.getFlag(slotInt);

        event.getView().setItem(
                slotInt,
                claimFlagService.getFlagItem(slotInt, !flagValue)
        );

        claimFlag.setFlag(slotInt, !flagValue);
        claimFlagService.updateClaimFlag(claimFlag);
    }


}
