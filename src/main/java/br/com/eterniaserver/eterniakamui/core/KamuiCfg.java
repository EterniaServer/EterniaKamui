package br.com.eterniaserver.eterniakamui.core;

import br.com.eterniaserver.eterniakamui.Constants;
import br.com.eterniaserver.eterniakamui.EterniaKamui;
import br.com.eterniaserver.eterniakamui.enums.Messages;
import br.com.eterniaserver.eterniakamui.enums.Strings;
import br.com.eterniaserver.eterniakamui.enums.Commands;

import br.com.eterniaserver.eternialib.chat.MessageMap;
import br.com.eterniaserver.eternialib.configuration.CommandLocale;
import br.com.eterniaserver.eternialib.configuration.enums.ConfigurationCategory;
import br.com.eterniaserver.eternialib.configuration.interfaces.CmdConfiguration;
import br.com.eterniaserver.eternialib.configuration.interfaces.MsgConfiguration;
import br.com.eterniaserver.eternialib.configuration.interfaces.ReloadableConfiguration;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class KamuiCfg implements ReloadableConfiguration, CmdConfiguration<Commands>, MsgConfiguration<Messages> {

    private final EterniaKamui plugin;
    private final ClaimFlagService claimFlagService;
    private final CustomWorldService customWorldService;

    private final FileConfiguration inFile;
    private final FileConfiguration outFile;

    private final MessageMap<Messages, String> messages = new MessageMap<>(Messages.class, Messages.CONS_SERVER_PREFIX);

    public KamuiCfg(EterniaKamui plugin, ClaimFlagService claimFlagService, CustomWorldService customWorldService) {
        this.plugin = plugin;
        this.claimFlagService = claimFlagService;
        this.customWorldService = customWorldService;

        this.inFile = YamlConfiguration.loadConfiguration(new File(getFilePath()));
        this.outFile = new YamlConfiguration();
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
                Messages.CONS_SERVER_PREFIX,
                "#555555[#34eb40E#3471ebK#555555]#AAAAAA "
        );
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
        strings[Strings.CONS_FLAG_DISABLED.ordinal()] = inFile.getString("flags.lore.disabled", "<color:#FF5555>Desativado");
        strings[Strings.CONS_FLAG_ENABLED.ordinal()] = inFile.getString("flags.lore.enabled", "<color:#55FF55>Ativado");
        strings[Strings.CONS_FLAG_MONSTER_SPAWN.ordinal()] = inFile.getString("flags.monster-spawn", "<color:#AAAAAA>Spawn de Monstros");
        strings[Strings.CONS_FLAG_PVP.ordinal()] = inFile.getString("flags.pvp", "<color:#AAAAAA>PvP");
        strings[Strings.CONS_EXPLOSIONS.ordinal()] = inFile.getString("flags.explosions", "<color:#AAAAAA>Explosões");
        strings[Strings.CONS_LIQUID_FLUID.ordinal()] = inFile.getString("flags.liquid-fluid", "<color:#AAAAAA>Fluir de líquidos");
        strings[Strings.CONS_KEEP_LEVEL.ordinal()] = inFile.getString("flags.keep-level", "<color:#AAAAAA>Manter nível");
        strings[Strings.CONS_LEAVE_DECAY.ordinal()] = inFile.getString("flags.leave-decay", "<color:#AAAAAA>Queda de folhas");
        strings[Strings.PERM_BYPASS.ordinal()] = inFile.getString("server.perm-bypass", "eterniakamui.bypass");

        outFile.set("sql.table-worlds", strings[Strings.TABLE_WORLDS.ordinal()]);
        outFile.set("sql.table-flags", strings[Strings.TABLE_FLAGS.ordinal()]);
        outFile.set("server.protected-worlds", strings[Strings.PROTECTED_WORLDS.ordinal()]);
        outFile.set("flags.lore.disabled", strings[Strings.CONS_FLAG_DISABLED.ordinal()]);
        outFile.set("flags.lore.enabled", strings[Strings.CONS_FLAG_ENABLED.ordinal()]);
        outFile.set("flags.monster-spawn", strings[Strings.CONS_FLAG_MONSTER_SPAWN.ordinal()]);
        outFile.set("flags.pvp", strings[Strings.CONS_FLAG_PVP.ordinal()]);
        outFile.set("flags.explosions", strings[Strings.CONS_EXPLOSIONS.ordinal()]);
        outFile.set("flags.liquid-fluid", strings[Strings.CONS_LIQUID_FLUID.ordinal()]);
        outFile.set("flags.keep-level", strings[Strings.CONS_KEEP_LEVEL.ordinal()]);
        outFile.set("flags.leave-decay", strings[Strings.CONS_LEAVE_DECAY.ordinal()]);
        outFile.set("server.perm-bypass", strings[Strings.PERM_BYPASS.ordinal()]);
    }

    @Override
    public void executeCritical() {
        addCommandLocale(
                Commands.FLAGS,
                new CommandLocale(
                        "flags",
                        null,
                        "Altere as flags de um terreno",
                        "eternia.claim.user",
                        null
                )
        );
        addCommandLocale(
                Commands.WORLDS,
                new CommandLocale(
                        "multiverse|mv",
                        "<página>",
                        "Comandos de mundos",
                        "eternia.world",
                        null
                )
        );
        addCommandLocale(
                Commands.WORLDS_CREATE,
                new CommandLocale(
                        "create",
                        "<nome> <enviroment> <type>",
                        " Crie e/ou carregue um mundo",
                        null,
                        null
                )
        );
        addCommandLocale(
                Commands.WORLDS_REMOVE,
                new CommandLocale(
                        "remove",
                        "<nome>",
                        " Remova um mundo",
                        null,
                        null
                )
        );
        addCommandLocale(
                Commands.WORLDS_TP,
                new CommandLocale(
                        "teleport|tp",
                        "<x> <y> <z> <nome>",
                        " Teleporta-se até um mundo",
                        null,
                        null
                )
        );

        claimFlagService.loadAllFlags();
        customWorldService.loadAllCustomWorlds();
    }

    @Override
    public MessageMap<Messages, String> messages() {
        return messages;
    }
}
