package br.com.eterniaserver.eterniakamui.core.pl3xmap;

import br.com.eterniaserver.eterniakamui.handlers.Pl3xMapHandler;
import net.pl3x.map.core.markers.layer.WorldLayer;
import net.pl3x.map.core.markers.marker.Marker;
import net.pl3x.map.core.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class MapLayer extends WorldLayer {

    public static final String KEY = "griefprevention";

    private final Pl3xMapHandler griefPreventionHook;

    public MapLayer(@NotNull Pl3xMapHandler griefPreventionHook, @NotNull World world) {
        super(KEY, world, () -> MapConfiguration.LAYER_LABEL);
        this.griefPreventionHook = griefPreventionHook;

        setShowControls(MapConfiguration.LAYER_SHOW_CONTROLS);
        setDefaultHidden(MapConfiguration.LAYER_DEFAULT_HIDDEN);
        setUpdateInterval(MapConfiguration.LAYER_UPDATE_INTERVAL);
        setPriority(MapConfiguration.LAYER_PRIORITY);
        setZIndex(MapConfiguration.LAYER_ZINDEX);
    }

    @Override
    public @NotNull Collection<Marker<?>> getMarkers() {
        return this.griefPreventionHook.getClaims(getWorld());
    }
}