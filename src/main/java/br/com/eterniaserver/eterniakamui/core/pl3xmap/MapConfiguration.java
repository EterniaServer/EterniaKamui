package br.com.eterniaserver.eterniakamui.core.pl3xmap;

import br.com.eterniaserver.eterniakamui.EterniaKamui;
import net.pl3x.map.core.configuration.AbstractConfig;

import java.nio.file.Path;

public final class MapConfiguration extends AbstractConfig {
    @Key("settings.layer.label")
    @Comment("Label para o Pl3xMap")
    public static String LAYER_LABEL = "GriefPrevention";
    @Key("settings.layer.show-controls")
    @Comment("Show controls for map layer")
    public static boolean LAYER_SHOW_CONTROLS = true;
    @Key("settings.layer.default-hidden")
    @Comment("Whether map layer is hidden by default")
    public static boolean LAYER_DEFAULT_HIDDEN = false;
    @Key("settings.layer.update-interval")
    @Comment("Update interval for map layer")
    public static int LAYER_UPDATE_INTERVAL = 30;
    @Key("settings.layer.priority")
    @Comment("Priority for map layer")
    public static int LAYER_PRIORITY = 10;
    @Key("settings.layer.z-index")
    @Comment("zIndex for map layer")
    public static int LAYER_ZINDEX = 10;

    @Key("settings.claim.basic.stroke.color")
    @Comment("Stroke color (#AARRGGBB)")
    public static String MARKER_BASIC_STROKE_COLOR = "#FF00FF00";
    @Key("settings.claim.basic.stroke.weight")
    @Comment("Stroke weight")
    public static int MARKER_BASIC_STROKE_WEIGHT = 3;
    @Key("settings.claim.basic.fill.color")
    @Comment("Fill color (#AARRGGBB)")
    public static String MARKER_BASIC_FILL_COLOR = "#3300FF00";
    @Key("settings.claim.basic.popup")
    @Comment("Popup for basic claims")
    public static String MARKER_BASIC_POPUP = """
            Claim Owner: <span style="font-weight:bold;"><owner></span><trusts>""";

    @Key("settings.claim.admin.stroke.color")
    @Comment("Stroke color (#AARRGGBB)")
    public static String MARKER_ADMIN_STROKE_COLOR = "#FFED7117";
    @Key("settings.claim.admin.stroke.weight")
    @Comment("Stroke weight")
    public static int MARKER_ADMIN_STROKE_WEIGHT = 3;
    @Key("settings.claim.admin.fill.color")
    @Comment("Fill color (#AARRGGBB)")
    public static String MARKER_ADMIN_FILL_COLOR = "#33ED7117";
    @Key("settings.claim.admin.popup")
    @Comment("Popup for admin claims")
    public static String MARKER_ADMIN_POPUP = """
            <span style="font-weight:bold;">Proteção Administrativa</span><trusts>""";

    @Key("settings.claim.popup.trust")
    @Comment("Popup text for trusts if present")
    public static String MARKER_POPUP_TRUST = """
            Construção: <span style="font-weight:bold;"><builders></span><br/>""";
    @Key("settings.claim.popup.container")
    @Comment("Popup text for container trusts if present")
    public static String MARKER_POPUP_CONTAINER = """
            Container: <span style="font-weight:bold;"><containers></span><br/>""";
    @Key("settings.claim.popup.access")
    @Comment("Popup text for trusts if present")
    public static String MARKER_POPUP_ACCESS = """
            Acesso: <span style="font-weight:bold;"><accessors></span><br/>""";
    @Key("settings.claim.popup.permission")
    @Comment("Popup text for trusts if present")
    public static String MARKER_POPUP_PERMISSION = """
            Administrativa: <span style="font-weight:bold;"><managers></span><br/>""";

    private static final MapConfiguration CONFIG = new MapConfiguration();

    public static void reload(EterniaKamui plugin) {
        Path mainDir = plugin.getDataFolder().toPath();
        CONFIG.reload(mainDir.resolve("griefprevention.yml"), MapConfiguration.class);
    }

}