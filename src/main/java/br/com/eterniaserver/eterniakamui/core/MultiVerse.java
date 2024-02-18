package br.com.eterniaserver.eterniakamui.core;

import br.com.eterniaserver.acf.BaseCommand;
import br.com.eterniaserver.acf.CommandHelp;
import br.com.eterniaserver.acf.annotation.CommandAlias;
import br.com.eterniaserver.acf.annotation.CommandCompletion;
import br.com.eterniaserver.acf.annotation.CommandPermission;
import br.com.eterniaserver.acf.annotation.Default;
import br.com.eterniaserver.acf.annotation.Description;
import br.com.eterniaserver.acf.annotation.HelpCommand;
import br.com.eterniaserver.acf.annotation.Subcommand;
import br.com.eterniaserver.acf.annotation.Syntax;

import br.com.eterniaserver.eterniakamui.EterniaKamui;
import br.com.eterniaserver.eterniakamui.enums.Messages;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@CommandAlias("mv|multiverse")
@CommandPermission("eternia.world")
public class MultiVerse extends BaseCommand {

    private final EterniaKamui plugin;
    private final CustomWorldService customWorldService;

    public MultiVerse(EterniaKamui plugin, CustomWorldService customWorldService) {
        this.plugin = plugin;
        this.customWorldService = customWorldService;
    }

    @Default
    @HelpCommand
    @Syntax("<página>")
    @Description(" Mostra ajudas para os comandos de multiworld")
    public void help(CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("create")
    @Syntax("<nome> <enviroment> <type> <invclear>(0 ou 1)")
    @CommandCompletion("world_teste @worldenv @worldtyp 0")
    @Description(" Crie e/ou carregue um mundo")
    public void onCreateWorld(Player player, String worldName, String worldEnviroment, String worldType, Integer invClear) {
        worldName = worldName.toLowerCase();
        worldEnviroment = worldEnviroment.toUpperCase();
        worldType = worldType.toUpperCase();

        if (customWorldService.containsWorld(worldName)) {
            plugin.sendMiniMessages(player, Messages.WORLD_EXISTS, worldName);
        }

        customWorldService.createWorld(worldName, worldEnviroment, worldType);

        plugin.sendMiniMessages(player, Messages.WORLD_CREATED, worldName);
    }

    @Subcommand("remove")
    @Syntax("<nome>")
    @CommandCompletion("@worlds_custom")
    @Description(" Remova um mundo")
    public void onRemoveWorld(Player player, String worldName) {
        worldName = worldName.toLowerCase();

        if (customWorldService.containsBaseWorld(worldName)) {
            plugin.sendMiniMessages(player, Messages.WORLD_BASE);
            return;
        }

        if (!customWorldService.containsCustomWorld(worldName)) {
            plugin.sendMiniMessages(player, Messages.WORLD_NOT_FOUND, worldName);
            return;
        }

        customWorldService.removeWorld(worldName);

        plugin.sendMiniMessages(player, Messages.WORLD_DELETED, worldName);
    }

    @Subcommand("teleport|tp")
    @Syntax("<x> <y> <z> <nome>")
    @CommandCompletion("0 0 0 @worlds")
    @Description(" Teleporta-se até um mundo")
    public void onTp(Player player, Double x, Double y, Double z, String worldName) {
        worldName = worldName.toLowerCase();

        if (!customWorldService.containsWorld(worldName)) {
            plugin.sendMiniMessages(player, Messages.WORLD_NOT_FOUND, worldName);
            return;
        }

        player.teleportAsync(new Location(Bukkit.getWorld(worldName), x, y, z, 0, 0));
    }
}