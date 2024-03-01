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

import br.com.eterniaserver.eterniakamui.enums.Messages;

import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.chat.MessageOptions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@CommandAlias("%WORLDS")
@CommandPermission("%WORLDS_PERM")
public class MultiVerse extends BaseCommand {

    private final CustomWorldService customWorldService;

    public MultiVerse(CustomWorldService customWorldService) {
        this.customWorldService = customWorldService;
    }

    @Default
    @HelpCommand
    @Syntax("%WORLDS_SYNTAX")
    @Description("%WORLDS_DESCRIPTION")
    public void help(CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("%WORLDS_CREATE")
    @Syntax("%WORLDS_CREATE_SYNTAX")
    @CommandCompletion("world_teste @worldenv @worldtyp")
    @Description("%WORLDS_CREATE_DESCRIPTION")
    public void onCreateWorld(Player player, String worldName, String worldEnviroment, String worldType, Integer invClear) {
        worldName = worldName.toLowerCase();
        worldEnviroment = worldEnviroment.toUpperCase();
        worldType = worldType.toUpperCase();

        if (customWorldService.containsWorld(worldName)) {
            MessageOptions options = new MessageOptions(worldName);
            EterniaLib.getChatCommons().sendMessage(player, Messages.WORLD_EXISTS, options);
        }

        customWorldService.createWorld(worldName, worldEnviroment, worldType);

        MessageOptions options = new MessageOptions(worldName);
        EterniaLib.getChatCommons().sendMessage(player, Messages.WORLD_CREATED, options);
    }

    @Subcommand("%WORLDS_REMOVE")
    @Syntax("%WORLDS_REMOVE_SYNTAX")
    @CommandCompletion("@worlds_custom")
    @Description("%WORLDS_REMOVE_DESCRIPTION")
    public void onRemoveWorld(Player player, String worldName) {
        worldName = worldName.toLowerCase();

        if (customWorldService.containsBaseWorld(worldName)) {
            EterniaLib.getChatCommons().sendMessage(player, Messages.WORLD_BASE);
            return;
        }

        if (!customWorldService.containsCustomWorld(worldName)) {
            MessageOptions options = new MessageOptions(worldName);
            EterniaLib.getChatCommons().sendMessage(player, Messages.WORLD_NOT_FOUND, options);
            return;
        }

        customWorldService.removeWorld(worldName);

        MessageOptions options = new MessageOptions(worldName);
        EterniaLib.getChatCommons().sendMessage(player, Messages.WORLD_DELETED, options);
    }

    @Subcommand("%WORLDS_TP")
    @Syntax("%WORLDS_TP_SYNTAX")
    @CommandCompletion("0 0 0 @worlds")
    @Description("%WORLDS_TP_DESCRIPTION")
    public void onTp(Player player, Double x, Double y, Double z, String worldName) {
        worldName = worldName.toLowerCase();

        if (!customWorldService.containsWorld(worldName)) {
            MessageOptions options = new MessageOptions(worldName);
            EterniaLib.getChatCommons().sendMessage(player, Messages.WORLD_NOT_FOUND, options);
            return;
        }

        player.teleportAsync(new Location(Bukkit.getWorld(worldName), x, y, z, 0, 0));
    }
}
