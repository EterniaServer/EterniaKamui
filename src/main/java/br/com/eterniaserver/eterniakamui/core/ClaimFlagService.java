package br.com.eterniaserver.eterniakamui.core;

import br.com.eterniaserver.eterniakamui.EterniaKamui;
import br.com.eterniaserver.eterniakamui.enums.Strings;
import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.database.Entity;
import br.com.eterniaserver.eterniaserver.EterniaServer;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

import net.kyori.adventure.text.Component;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

public class ClaimFlagService {

    private final EterniaKamui plugin;

    private final Map<Integer, BukkitTask> GUI_TASKS = new HashMap<>();

    private final ItemStack[] GUI_ITEMS_ENABLE = new ItemStack[ClaimFlag.FLAGS_LENGTH];
    private final ItemStack[] GUI_ITEMS_DISABLE = new ItemStack[ClaimFlag.FLAGS_LENGTH];
    private final ItemStack[] BASE_GUI = new ItemStack[9];

    private List<Component> loreEnable;
    private List<Component> loreDisable;

    public ClaimFlagService(EterniaKamui plugin) {
        this.plugin = plugin;
    }

    public void updateClaimFlag(ClaimFlag claimFlag) {
        BukkitTask task = GUI_TASKS.get(claimFlag.getId());

        if (task != null && !plugin.getServer().getScheduler().isCurrentlyRunning(task.getTaskId())) {
            task.cancel();
        }

        task = plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            EterniaLib.getDatabase().update(ClaimFlag.class, claimFlag);
        }, 200L);
        GUI_TASKS.put(claimFlag.getId(), task);
    }

    public Optional<Claim> getClaimAt(Location location) {
        return getClaimAt(location, null);
    }

    public Optional<Claim> getClaimCachedAt(Player player) {
        Claim lastClaim = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId()).lastClaim;
        return getClaimAt(player.getLocation(), lastClaim);
    }

    public Optional<ClaimFlag> getClaimFlag(int claimID) {
        ClaimFlag claimFlag = EterniaLib.getDatabase().get(ClaimFlag.class, claimID);
        if (claimFlag == null || claimFlag.getId() == null) {
            return Optional.empty();
        }

        return Optional.of(claimFlag);
    }

    public Inventory createPlayerFlagGui(ClaimFlag claimFlag, Player player) {
        Inventory inventory = EterniaServer.getGuiAPI().getGUI(ClaimFlag.GUI_NAME, player);

        for (int i = 0; i < ClaimFlag.FLAGS_LENGTH; i++) {
            boolean flagValue = claimFlag.getFlag(i);
            inventory.setItem(i, getFlagItem(i, flagValue));
        }

        return inventory;
    }

    public ItemStack getFlagItem(int flag, boolean enable) {
        if (enable) {
            return GUI_ITEMS_ENABLE[flag];
        }

        return GUI_ITEMS_DISABLE[flag];
    }

    protected void loadAllFlags() {
        createCashGui();

        try {
            Entity<ClaimFlag> claimFlagEntity = new Entity<>(ClaimFlag.class);

            EterniaLib.getDatabase().addTableName("%eternia_kamui_flags%", plugin.getString(Strings.TABLE_FLAGS));

            EterniaLib.getDatabase().register(ClaimFlag.class, claimFlagEntity);
        } catch (Exception exception) {
            plugin.getLogger().severe("EterniaKamui: Error loading ClaimFlags" + exception.getMessage());
            return;
        }

        List<ClaimFlag> claimFlags = EterniaLib.getDatabase().listAll(ClaimFlag.class);
        this.plugin.getLogger().log(Level.INFO, "EterniaKamui: {0} flags loaded", claimFlags.size());
    }

    private Optional<Claim> getClaimAt(Location location, Claim lastClaim) {
        return Optional.ofNullable(GriefPrevention.instance.dataStore.getClaimAt(
                location,
                false,
                false,
                lastClaim
        ));
    }

    private void createCashGui() {
        this.loreEnable = List.of(EterniaLib.getChatCommons().parseColor(plugin.getString(Strings.CONS_FLAG_ENABLED)));
        this.loreDisable = List.of(EterniaLib.getChatCommons().parseColor(plugin.getString(Strings.CONS_FLAG_DISABLED)));

        // Load the default Monster Spawn Tags
        loadDefaultItens(Material.CARVED_PUMPKIN, ClaimFlag.MONSTER_SPAWN_INDEX, plugin.getString(Strings.CONS_FLAG_MONSTER_SPAWN), false);
        loadDefaultItens(Material.CARVED_PUMPKIN, ClaimFlag.MONSTER_SPAWN_INDEX, plugin.getString(Strings.CONS_FLAG_MONSTER_SPAWN), true);

        // Load the default PvP Tags
        loadDefaultItens(Material.DIAMOND_SWORD, ClaimFlag.ALLOW_PVP_INDEX, plugin.getString(Strings.CONS_FLAG_PVP), false);
        loadDefaultItens(Material.DIAMOND_SWORD, ClaimFlag.ALLOW_PVP_INDEX, plugin.getString(Strings.CONS_FLAG_PVP), true);

        // Load the default Explosions Tags
        loadDefaultItens(Material.TNT, ClaimFlag.EXPLOSIONS_INDEX, plugin.getString(Strings.CONS_EXPLOSIONS), false);
        loadDefaultItens(Material.TNT, ClaimFlag.EXPLOSIONS_INDEX, plugin.getString(Strings.CONS_EXPLOSIONS), true);

        // Load the default Liquid Flow Tags
        loadDefaultItens(Material.WATER_BUCKET, ClaimFlag.LIQUID_FLUID_INDEX, plugin.getString(Strings.CONS_LIQUID_FLUID), false);
        loadDefaultItens(Material.WATER_BUCKET, ClaimFlag.LIQUID_FLUID_INDEX, plugin.getString(Strings.CONS_LIQUID_FLUID), true);

        // Load the default Keep Level Tags
        loadDefaultItens(Material.EXPERIENCE_BOTTLE, ClaimFlag.KEEP_LEVEL_INDEX, plugin.getString(Strings.CONS_KEEP_LEVEL), false);
        loadDefaultItens(Material.EXPERIENCE_BOTTLE, ClaimFlag.KEEP_LEVEL_INDEX, plugin.getString(Strings.CONS_KEEP_LEVEL), true);

        // Load the default Keep Level Tags
        loadDefaultItens(Material.AZALEA_LEAVES, ClaimFlag.LEAVE_DECAY_INDEX, plugin.getString(Strings.CONS_LEAVE_DECAY), false);
        loadDefaultItens(Material.AZALEA_LEAVES, ClaimFlag.LEAVE_DECAY_INDEX, plugin.getString(Strings.CONS_LEAVE_DECAY), true);

        loadBaseGui();

        EterniaServer.getGuiAPI().createGUI(ClaimFlag.GUI_NAME, BASE_GUI);
    }

    private void loadBaseGui() {
        for (int i = 0; i < 9; i++) {
            BASE_GUI[i] = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta meta = BASE_GUI[i].getItemMeta();
            meta.displayName(EterniaLib.getChatCommons().parseColor("<color:#aaaaaa>EK Flags"));
            BASE_GUI[i].setItemMeta(meta);
        }
    }

    private void loadDefaultItens(Material material, int position, String name, boolean enabled) {
        ItemStack itemStack = new ItemStack(material);

        itemStack.addUnsafeEnchantment(Enchantment.LUCK, 1);

        ItemMeta meta = itemStack.getItemMeta();

        meta.displayName(EterniaLib.getChatCommons().parseColor(name));
        meta.lore(enabled ? loreEnable : loreDisable);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        itemStack.setItemMeta(meta);

        if (enabled) {
            GUI_ITEMS_ENABLE[position] = itemStack;
            return;
        }

        GUI_ITEMS_DISABLE[position] = itemStack;
    }


}
