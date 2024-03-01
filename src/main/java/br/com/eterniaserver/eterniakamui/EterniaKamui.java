package br.com.eterniaserver.eterniakamui;

import br.com.eterniaserver.eterniakamui.enums.Strings;

import org.bukkit.plugin.java.JavaPlugin;

public class EterniaKamui extends JavaPlugin {

    private final String[] strings = new String[Strings.values().length];

    private final Manager manager = new Manager(this);

    public String[] strings() {
        return strings;
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

}