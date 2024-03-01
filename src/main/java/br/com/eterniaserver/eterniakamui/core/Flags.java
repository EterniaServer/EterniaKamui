package br.com.eterniaserver.eterniakamui.core;

import br.com.eterniaserver.acf.BaseCommand;
import br.com.eterniaserver.acf.annotation.CommandAlias;
import br.com.eterniaserver.acf.annotation.CommandPermission;
import br.com.eterniaserver.acf.annotation.Description;
import br.com.eterniaserver.eterniakamui.EterniaKamui;
import br.com.eterniaserver.eterniakamui.enums.Messages;
import br.com.eterniaserver.eterniakamui.enums.Strings;
import br.com.eterniaserver.eternialib.EterniaLib;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Optional;

public class Flags extends BaseCommand {

    private final EterniaKamui plugin;
    private final ClaimFlagService claimFlagService;

    public Flags(EterniaKamui plugin, ClaimFlagService claimFlagService) {
        this.plugin = plugin;
        this.claimFlagService = claimFlagService;
    }

    @CommandAlias("%FLAGS")
    @CommandPermission("%FLAGS_PERM")
    @Description("%FLAGS_DESCRIPTION")
    public void onFlag(Player player) {
        Optional<Claim> claimOptional = claimFlagService.getClaimCachedAt(player);
        if (claimOptional.isEmpty()) {
            EterniaLib.getChatCommons().sendMessage(player, Messages.CLAIM_NOT_FOUND);
            return;
        }

        Claim claim = claimOptional.get();
        if (!player.hasPermission(plugin.getString(Strings.PERM_BYPASS)) && !claim.ownerID.equals(player.getUniqueId()) && claim.hasExplicitPermission(player, ClaimPermission.Manage)) {
            EterniaLib.getChatCommons().sendMessage(player, Messages.WITHOUT_PERM);
            return;
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            int claimId = claim.getID().intValue();
            Optional<ClaimFlag> claimFlagOptional = claimFlagService.getClaimFlag(claimId);

            ClaimFlag claimFlag;
            if (claimFlagOptional.isEmpty()) {
                claimFlag = new ClaimFlag(claimId);
                EterniaLib.getDatabase().insert(ClaimFlag.class, claimFlag);
            } else {
                claimFlag = claimFlagOptional.get();
            }

            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                player.closeInventory();
                Inventory flagGui = claimFlagService.createPlayerFlagGui(claimFlag, player);
                player.openInventory(flagGui);
            });
        });
    }

}