package br.com.eterniaserver.eterniakamui;

import br.com.eterniaserver.eterniakamui.core.ClaimFlagService;
import br.com.eterniaserver.eterniakamui.core.ConfigurationCfg;
import br.com.eterniaserver.eterniakamui.core.CustomWorldService;
import br.com.eterniaserver.eterniakamui.core.Flags;
import br.com.eterniaserver.eterniakamui.core.MultiVerse;
import br.com.eterniaserver.eterniakamui.handlers.ClaimHandler;
import br.com.eterniaserver.eterniakamui.handlers.Pl3xMapHandler;
import br.com.eterniaserver.eternialib.EterniaLib;
import net.pl3x.map.core.Pl3xMap;

public class Manager {

    private final EterniaKamui plugin;
    private final Pl3xMapHandler pl3xMapHandler;

    public Manager(EterniaKamui plugin) {
        this.plugin = plugin;
        this.pl3xMapHandler = new Pl3xMapHandler(plugin);
    }
    public void onEnable() {
        ClaimFlagService claimFlagService = new ClaimFlagService(plugin);
        CustomWorldService customWorldService = new CustomWorldService(plugin);

        ConfigurationCfg configuration = new ConfigurationCfg(plugin, claimFlagService, customWorldService);

        EterniaLib.registerConfiguration("eterniakamui", "core", configuration);

        configuration.executeConfig();
        configuration.executeCritical();

        configuration.saveConfiguration(true);

        EterniaLib.getCmdManager().getCommandCompletions().registerStaticCompletion("worldenv", customWorldService.environments());
        EterniaLib.getCmdManager().getCommandCompletions().registerStaticCompletion("worldtyp", customWorldService.types());
        EterniaLib.getCmdManager().getCommandCompletions().registerStaticCompletion("worlds_custom", customWorldService.worldNames());

        EterniaLib.getCmdManager().registerCommand(new MultiVerse(plugin, customWorldService));
        EterniaLib.getCmdManager().registerCommand(new Flags(plugin, claimFlagService));

        plugin.getServer().getPluginManager().registerEvents(new ClaimHandler(plugin, claimFlagService), plugin);

        if (plugin.getServer().getPluginManager().isPluginEnabled("Pl3xMap")) {
            plugin.getServer().getPluginManager().registerEvents(pl3xMapHandler, plugin);
            Pl3xMap.api().getWorldRegistry().forEach(pl3xMapHandler::registerWorld);
        }
    }

    public void onDisable() {
        if (plugin.getServer().getPluginManager().isPluginEnabled("Pl3xMap")) {
            Pl3xMap.api().getWorldRegistry().forEach(pl3xMapHandler::unloadWorld);
        }
        plugin.getServer().getScheduler().cancelTasks(plugin);
    }

}
