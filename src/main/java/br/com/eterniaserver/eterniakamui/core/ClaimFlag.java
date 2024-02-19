package br.com.eterniaserver.eterniakamui.core;

import br.com.eterniaserver.eternialib.database.annotations.DataField;
import br.com.eterniaserver.eternialib.database.annotations.PrimaryKeyField;
import br.com.eterniaserver.eternialib.database.annotations.Table;
import br.com.eterniaserver.eternialib.database.enums.FieldType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(tableName = "%eternia_kamui_flags%")
public class ClaimFlag {

    public static final String GUI_NAME = "EterniaFlags<color:%s>";

    public static final int FLAGS_LENGTH = 6;

    public static final int MONSTER_SPAWN_INDEX = 0;
    public static final int ALLOW_PVP_INDEX = 1;
    public static final int EXPLOSIONS_INDEX = 2;
    public static final int LIQUID_FLUID_INDEX = 3;
    public static final int KEEP_LEVEL_INDEX = 4;
    public static final int LEAVE_DECAY_INDEX = 5;

    @Setter
    @PrimaryKeyField(columnName = "id", type = FieldType.INTEGER, autoIncrement = false)
    private Integer id;

    @DataField(columnName = "flags", type = FieldType.STRING, notNull = true)
    private String flags;

    public ClaimFlag(String flags) {
        setFlags(flags);
    }

    public ClaimFlag(Integer id) {
        this("001111");
        this.id = id;
    }

    private boolean monsterSpawn;
    private boolean allowPvP;
    private boolean explosions;
    private boolean liquidFluid;
    private boolean keepLevel;
    private boolean leaveDecay;

    public void setFlags(String flags) {
        if (flags.length() != FLAGS_LENGTH) {
            flags = StringUtils.rightPad(flags, FLAGS_LENGTH, "1");
        }

        this.flags = flags;

        setMonsterSpawn(flags.charAt(MONSTER_SPAWN_INDEX) == '1');
        setAllowPvP(flags.charAt(ALLOW_PVP_INDEX) == '1');
        setExplosions(flags.charAt(EXPLOSIONS_INDEX) == '1');
        setLiquidFluid(flags.charAt(LIQUID_FLUID_INDEX) == '1');
        setKeepLevel(flags.charAt(KEEP_LEVEL_INDEX) == '1');
    }

    public boolean getFlag(int index) {
        return flags.charAt(index) == '1';
    }

    private void internalSetFlag(int index, boolean value) {
        flags = flags.substring(0, index) + (value ? "1" : "0") + flags.substring(index + 1);
    }

    public void setFlag(int index, boolean value) {
        switch (index) {
            case MONSTER_SPAWN_INDEX -> setMonsterSpawn(value);
            case ALLOW_PVP_INDEX -> setAllowPvP(value);
            case EXPLOSIONS_INDEX -> setExplosions(value);
            case LIQUID_FLUID_INDEX -> setLiquidFluid(value);
            case KEEP_LEVEL_INDEX -> setKeepLevel(value);
        }
    }

    private void setMonsterSpawn(boolean creatureSpawn) {
        this.monsterSpawn = creatureSpawn;
        internalSetFlag(MONSTER_SPAWN_INDEX, creatureSpawn);
    }

    private void setAllowPvP(boolean allowPvP) {
        this.allowPvP = allowPvP;
        internalSetFlag(ALLOW_PVP_INDEX, allowPvP);
    }

    private void setExplosions(boolean explosions) {
        this.explosions = explosions;
        internalSetFlag(EXPLOSIONS_INDEX, explosions);
    }

    private void setLiquidFluid(boolean liquidFluid) {
        this.liquidFluid = liquidFluid;
        internalSetFlag(LIQUID_FLUID_INDEX, liquidFluid);
    }

    private void setKeepLevel(boolean keepLevel) {
        this.keepLevel = keepLevel;
        internalSetFlag(KEEP_LEVEL_INDEX, keepLevel);
    }

}
