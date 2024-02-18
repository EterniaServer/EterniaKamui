package br.com.eterniaserver.eterniakamui.core;

import br.com.eterniaserver.eternialib.database.annotations.DataField;
import br.com.eterniaserver.eternialib.database.annotations.PrimaryKeyField;
import br.com.eterniaserver.eternialib.database.annotations.Table;
import br.com.eterniaserver.eternialib.database.enums.FieldType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(tableName = "%eternia_kamui_worlds%")
public class CustomWorld {

    @PrimaryKeyField(columnName = "worldName", type = FieldType.STRING, autoIncrement = false)
    private String worldName;

    @DataField(columnName = "worldEnvironment", type = FieldType.STRING, notNull = true)
    private String worldEnvironment;

    @DataField(columnName = "worldType", type = FieldType.STRING, notNull = true)
    private String worldType;

}
