package br.com.eterniaserver.eterniakamui.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import br.com.eterniaserver.eterniakamui.EterniaKamui;
import br.com.eterniaserver.eterniakamui.core.pl3xmap.MapClaim;
import br.com.eterniaserver.eterniakamui.core.pl3xmap.MapConfiguration;
import br.com.eterniaserver.eterniakamui.core.pl3xmap.MapLayer;

import me.ryanhamshire.GriefPrevention.GriefPrevention;

import net.pl3x.map.core.markers.marker.Marker;
import net.pl3x.map.core.markers.option.Options;
import net.pl3x.map.core.util.Colors;
import net.pl3x.map.core.world.World;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;

import org.jetbrains.annotations.NotNull;

public class Pl3xMapHandler implements Listener {

    public Pl3xMapHandler(EterniaKamui plugin) {
        MapConfiguration.reload(plugin);
    }

    Collection<Marker<?>> EMPTY_LIST = new ArrayList<>();

    private boolean isWorldEnabled(@NotNull String name) {
        return GriefPrevention.instance.claimsEnabledForWorld(Bukkit.getWorld(name));
    }

    public void registerWorld(@NotNull World world) {
        if (isWorldEnabled(world.getName())) {
            world.getLayerRegistry().register(new MapLayer(this, world));
        }
    }

    public void unloadWorld(@NotNull World world) {
        world.getLayerRegistry().unregister(MapLayer.KEY);
    }

    public @NotNull Collection<Marker<?>> getClaims(@NotNull World world) {
        if (!isWorldEnabled(world.getName())) {
            return EMPTY_LIST;
        }
        return GriefPrevention.instance.dataStore.getClaims().stream()
                .filter(claim -> claim.getLesserBoundaryCorner().getWorld().getName().equals(world.getName()))
                .map(claim -> new MapClaim(world, claim))
                .map(claim -> {
                    String key = "gp-claim-" + claim.getID();
                    return Marker.rectangle(key, claim.getMin(), claim.getMax())
                            .setOptions(getOptions(claim));
                })
                .collect(Collectors.toSet());
    }

    private @NotNull Options getOptions(@NotNull MapClaim claim) {
        Options.Builder builder;
        if (claim.isAdminClaim()) {
            builder = Options.builder()
                    .strokeWeight(MapConfiguration.MARKER_ADMIN_STROKE_WEIGHT)
                    .strokeColor(Colors.fromHex(MapConfiguration.MARKER_ADMIN_STROKE_COLOR))
                    .fillColor(Colors.fromHex(MapConfiguration.MARKER_ADMIN_FILL_COLOR))
                    .popupContent(processPopup(MapConfiguration.MARKER_ADMIN_POPUP, claim));
        } else {
            builder = Options.builder()
                    .strokeWeight(MapConfiguration.MARKER_BASIC_STROKE_WEIGHT)
                    .strokeColor(Colors.fromHex(MapConfiguration.MARKER_BASIC_STROKE_COLOR))
                    .fillColor(Colors.fromHex(MapConfiguration.MARKER_BASIC_FILL_COLOR))
                    .popupContent(processPopup(MapConfiguration.MARKER_BASIC_POPUP, claim));
        }
        return builder.build();
    }

    private @NotNull String processPopup(@NotNull String popup, @NotNull MapClaim claim) {
        return popup.replace("<world>", claim.getWorld().getName())
                .replace("<id>", Long.toString(claim.getID()))
                .replace("<owner>", claim.getOwnerName())
                .replace("<trusts>", getTrusts(claim))
                .replace("<area>", Integer.toString(claim.getArea()))
                .replace("<width>", Integer.toString(claim.getWidth()))
                .replace("<height>", Integer.toString(claim.getHeight()));
    }

    private @NotNull String getTrusts(@NotNull MapClaim claim) {
        ArrayList<String> builders = new ArrayList<>();
        ArrayList<String> containers = new ArrayList<>();
        ArrayList<String> accessors = new ArrayList<>();
        ArrayList<String> managers = new ArrayList<>();
        claim.getPermissions(builders, containers, accessors, managers);
        StringBuilder sb = new StringBuilder();
        if (!builders.isEmpty()) {
            if (sb.isEmpty()) sb.append("<hr/>");
            sb.append(MapConfiguration.MARKER_POPUP_TRUST.replace("<builders>", getNames(builders)));
        }
        if (!containers.isEmpty()) {
            if (sb.isEmpty()) sb.append("<hr/>");
            sb.append(MapConfiguration.MARKER_POPUP_CONTAINER.replace("<containers>", getNames(containers)));
        }
        if (!accessors.isEmpty()) {
            if (sb.isEmpty()) sb.append("<hr/>");
            sb.append(MapConfiguration.MARKER_POPUP_ACCESS.replace("<accessors>", getNames(accessors)));
        }
        if (!managers.isEmpty()) {
            if (sb.isEmpty()) sb.append("<hr/>");
            sb.append(MapConfiguration.MARKER_POPUP_PERMISSION.replace("<managers>", getNames(managers)));
        }
        return sb.toString();
    }

    private @NotNull String getNames(@NotNull List<String> list) {
        List<String> names = new ArrayList<>();
        for (String str : list) {
            try {
                UUID uuid = UUID.fromString(str);
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                names.add(offlinePlayer.getName());
            } catch (Exception e) {
                names.add(str);
            }
        }
        return String.join(", ", names);
    }

}
