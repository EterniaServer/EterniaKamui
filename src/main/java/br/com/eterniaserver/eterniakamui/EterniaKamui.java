package br.com.eterniaserver.eterniakamui;

import br.com.eterniaserver.eterniakamui.enums.Messages;
import br.com.eterniaserver.eterniakamui.enums.Strings;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class EterniaKamui extends JavaPlugin {

    private final MiniMessage miniMessage =  MiniMessage.miniMessage();

    private final String[] strings = new String[Strings.values().length];
    private final String[] messages = new String[Messages.values().length];

    private final Manager manager = new Manager(this);

    public String[] strings() {
        return strings;
    }

    public String[] messages() {
        return messages;
    }

    public String getString(Strings entry) {
        return strings[entry.ordinal()];
    }

    @Override
    public void onEnable() {
        manager.onEnable();
    }

    @Override
    public void onDisable() {
        manager.onDisable();
    }

    public void sendMiniMessages(CommandSender sender, Messages messagesId, String... args) {
        String message = messages[messagesId.ordinal()];

        for (int i = 0; i < args.length; i++) {
            message = message.replace("{" + i + "}", args[i]);
        }

        sender.sendMessage(parseColor(strings[Strings.CONS_SERVER_PREFIX.ordinal()] + message));
    }

    public Component parseColor(String string) {
        return miniMessage.deserialize(string);
    }

}