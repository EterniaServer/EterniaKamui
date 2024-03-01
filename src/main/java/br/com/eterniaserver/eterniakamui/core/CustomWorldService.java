package br.com.eterniaserver.eterniakamui.core;

import br.com.eterniaserver.eterniakamui.EterniaKamui;
import br.com.eterniaserver.eterniakamui.enums.Strings;
import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.database.Entity;

import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class CustomWorldService {

    private final List<String> environments = List.of("nether", "end", "normal");
    private final List<String> types = Arrays
            .stream(WorldType.values())
            .map(WorldType::name)
            .map(String::toLowerCase)
            .toList();

    private final EterniaKamui plugin;

    private final List<String> worldNames = new ArrayList<>();
    private final List<String> customWorlds = new ArrayList<>();
    private final List<String> baseWorlds = new ArrayList<>();

    public CustomWorldService(EterniaKamui plugin) {
        this.plugin = plugin;
    }

    public List<String> worldNames() {
        return worldNames;
    }

    public List<String> environments() {
        return environments;
    }

    public List<String> types() {
        return types;
    }

    public boolean containsWorld(String worldName) {
        return this.worldNames.contains(worldName);
    }

    public boolean containsBaseWorld(String worldName) {
        return this.baseWorlds.contains(worldName);
    }

    public boolean containsCustomWorld(String worldName) {
        return this.customWorlds.contains(worldName);
    }

    public void createWorld(String worldName, String environment, String type) {
        CustomWorld customWorld = new CustomWorld();
        customWorld.setWorldName(worldName);
        customWorld.setWorldEnvironment(environment);
        customWorld.setWorldType(type);

        createWorld(customWorld, true);
    }

    public void createWorld(CustomWorld customWorld, boolean save) {
        WorldCreator worldCreator = new WorldCreator(customWorld.getWorldName());
        worldCreator.environment(World.Environment.valueOf(customWorld.getWorldEnvironment().toUpperCase()));
        worldCreator.type(WorldType.valueOf(customWorld.getWorldType().toUpperCase()));

        plugin.getServer().createWorld(worldCreator);

        this.worldNames.add(customWorld.getWorldName());
        this.customWorlds.add(customWorld.getWorldName());

        if (save) {
            EterniaLib.getDatabase().insert(CustomWorld.class, customWorld);
        }
    }

    public void removeWorld(String worldName) {
        World world = plugin.getServer().getWorld(worldName);
        if (world == null) {
            return;
        }

        plugin.getServer().unloadWorld(worldName, true);

        this.worldNames.remove(worldName);
        this.customWorlds.remove(worldName);

        plugin.getServer().getScheduler().runTaskAsynchronously(
                plugin, () -> EterniaLib.getDatabase().delete(CustomWorld.class, worldName)
        );
    }

    protected void loadAllCustomWorlds() {
        for (final World world : plugin.getServer().getWorlds()) {
            this.worldNames.add(world.getName());
            this.baseWorlds.add(world.getName());
        }

        try {
            Entity<CustomWorld> customWorldEntity = new Entity<>(CustomWorld.class);

            EterniaLib.getDatabase().addTableName("%eternia_kamui_worlds%", plugin.getString(Strings.TABLE_WORLDS));

            EterniaLib.getDatabase().register(CustomWorld.class, customWorldEntity);
        } catch (Exception exception) {
            plugin.getLogger().severe("EterniaKamui: Error loading CustomWorlds" + exception.getMessage());
            return;
        }

        List<CustomWorld> customWorlds = EterniaLib.getDatabase().listAll(CustomWorld.class);
        for (CustomWorld customWorld : customWorlds) {
            createWorld(customWorld, false);
        }
        this.plugin.getLogger().log(Level.INFO, "EterniaKamui: {0} worlds loaded", customWorlds.size());
    }
}
