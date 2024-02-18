package br.com.eterniaserver.eterniakamui.core;

import br.com.eterniaserver.eterniakamui.Constants;
import br.com.eterniaserver.eterniakamui.EterniaKamui;

import br.com.eterniaserver.eterniakamui.enums.Messages;
import br.com.eterniaserver.eterniakamui.enums.Strings;

import br.com.eterniaserver.eternialib.configuration.CommandLocale;
import br.com.eterniaserver.eternialib.configuration.ReloadableConfiguration;
import br.com.eterniaserver.eternialib.configuration.enums.ConfigurationCategory;
import br.com.eterniaserver.eternialib.core.enums.Commands;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigurationCfg implements ReloadableConfiguration {

    private final EterniaKamui plugin;
    private final ClaimFlagService claimFlagService;
    private final CustomWorldService customWorldService;

    private final FileConfiguration inFile;
    private final FileConfiguration outFile;

    private final CommandLocale[] commandsLocalesArray;

    public ConfigurationCfg(EterniaKamui plugin, ClaimFlagService claimFlagService, CustomWorldService customWorldService) {
        this.plugin = plugin;
        this.claimFlagService = claimFlagService;
        this.customWorldService = customWorldService;

        this.inFile = YamlConfiguration.loadConfiguration(new File(getFilePath()));
        this.outFile = new YamlConfiguration();

        this.commandsLocalesArray = new CommandLocale[Commands.values().length];
    }

    @Override
    public FileConfiguration inFileConfiguration() {
        return inFile;
    }

    @Override
    public FileConfiguration outFileConfiguration() {
        return outFile;
    }

    @Override
    public String[] messages() {
        return plugin.messages();
    }

    @Override
    public CommandLocale[] commandsLocale() {
        return commandsLocalesArray;
    }

    @Override
    public ConfigurationCategory category() {
        return ConfigurationCategory.GENERIC;
    }

    @Override
    public String getFolderPath() {
        return Constants.DATA_LAYER_FOLDER_PATH;
    }

    @Override
    public String getFilePath() {
        return Constants.CONFIG_FILE_PATH;
    }

    @Override
    public void executeConfig() {
        addMessage(
                Messages.CLAIM_NOT_FOUND,
                "Não foi possível encontrar nenhuma proteção nessa localizaçao<color:#555555>."
        );
        addMessage(
                Messages.WITHOUT_PERM,
                "Você não pode editar as flags de um terreno que não possui permissão administrativa<color:#555555>."
        );
        addMessage(
                Messages.WORLD_EXISTS,
                "Já existe um mundo com o nome de <color:#00AAAA>{0}<color:#555555>."
        );
        addMessage(
                Messages.WORLD_CREATED,
                "O mundo <color:#00AAAA>{0}<color:#AAAAAA> foi criado<color:#555555>."
        );
        addMessage(
                Messages.WORLD_REMOVED,
                "O mundo <color:#00AAAA>{0}<color:#AAAAAA> foi removido<color:#555555>."
        );
        addMessage(
                Messages.WORLD_DELETED,
                "O mundo <color:#00AAAA>{0}<color:#AAAAAA> foi deletado<color:#555555>."
        );
        addMessage(
                Messages.WORLD_NOT_FOUND,
                "O mundo <color:#00AAAA>{0}<color:#AAAAAA> não foi encontrado<color:#555555>."
        );
        addMessage(
                Messages.WORLD_BASE,
                "O mundo é um mundo base<color:#555555>."
        );

        String[] strings = plugin.strings();

        strings[Strings.TABLE_WORLDS.ordinal()] = inFile.getString("sql.table-worlds", "e_kamui_worlds");
        strings[Strings.TABLE_FLAGS.ordinal()] = inFile.getString("sql.table-flags", "e_kamui_claim_flags");
        strings[Strings.PROTECTED_WORLDS.ordinal()] = inFile.getString("server.protected-worlds", "world,world_builder");
        strings[Strings.CONS_SERVER_PREFIX.ordinal()] = inFile.getString("server.prefix", "<color:#555555>[<color:#34eb40>E<color:#3471eb>K<color:#555555>]<color:#AAAAAA> ");
        strings[Strings.CONS_FLAG_DISABLED.ordinal()] = inFile.getString("flags.lore.disabled", "<color:#FF5555>Desativado");
        strings[Strings.CONS_FLAG_ENABLED.ordinal()] = inFile.getString("flags.lore.enabled", "<color:#55FF55>Ativado");
        strings[Strings.CONS_FLAG_MONSTER_SPAWN.ordinal()] = inFile.getString("flags.monster-spawn", "<color:#AAAAAA>Monster Spawn");
        strings[Strings.CONS_FLAG_PVP.ordinal()] = inFile.getString("flags.pvp", "<color:#AAAAAA>PvP");
        strings[Strings.CONS_EXPLOSIONS.ordinal()] = inFile.getString("flags.explosions", "<color:#AAAAAA>Explosões");
        strings[Strings.CONS_LIQUID_FLUID.ordinal()] = inFile.getString("flags.liquid-fluid", "<color:#AAAAAA>Fluir Líquidos");
        strings[Strings.CONS_KEEP_LEVEL.ordinal()] = inFile.getString("flags.keep-level", "<color:#AAAAAA>Manter Nível");
        strings[Strings.PERM_BYPASS.ordinal()] = inFile.getString("server.perm-bypass", "eterniakamui.bypass");

        outFile.set("sql.table-worlds", strings[Strings.TABLE_WORLDS.ordinal()]);
        outFile.set("sql.table-flags", strings[Strings.TABLE_FLAGS.ordinal()]);
        outFile.set("server.prefix", strings[Strings.CONS_SERVER_PREFIX.ordinal()]);
        outFile.set("server.protected-worlds", strings[Strings.PROTECTED_WORLDS.ordinal()]);
        outFile.set("flags.lore.disabled", strings[Strings.CONS_FLAG_DISABLED.ordinal()]);
        outFile.set("flags.lore.enabled", strings[Strings.CONS_FLAG_ENABLED.ordinal()]);
        outFile.set("flags.monster-spawn", strings[Strings.CONS_FLAG_MONSTER_SPAWN.ordinal()]);
        outFile.set("flags.pvp", strings[Strings.CONS_FLAG_PVP.ordinal()]);
        outFile.set("flags.explosions", strings[Strings.CONS_EXPLOSIONS.ordinal()]);
        outFile.set("flags.liquid-fluid", strings[Strings.CONS_LIQUID_FLUID.ordinal()]);
        outFile.set("flags.keep-level", strings[Strings.CONS_KEEP_LEVEL.ordinal()]);
        outFile.set("server.perm-bypass", strings[Strings.PERM_BYPASS.ordinal()]);
    }

    @Override
    public void executeCritical() {
        claimFlagService.loadAllFlags();
        customWorldService.loadAllCustomWorlds();
    }
}