package br.com.eterniaserver.eterniakamui.core.pl3xmap;

import java.util.ArrayList;
import java.util.UUID;

import lombok.NonNull;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.Messages;

import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.world.World;

import org.bukkit.Location;


public class MapClaim {
    private final World world;
    private final Claim claim;
    private final Point min;
    private final Point max;

    private UUID ownerId;
    private String ownerName;

    public MapClaim(@NonNull World world, @NonNull Claim claim) {
        this.world = world;
        this.claim = claim;

        Location min = this.claim.getLesserBoundaryCorner();
        Location max = this.claim.getGreaterBoundaryCorner();
        this.min = Point.of(min.getX(), min.getZ());
        this.max = Point.of(max.getX(), max.getZ());
    }

    public @NonNull World getWorld() {
        return this.world;
    }

    public boolean isAdminClaim() {
        return this.claim.isAdminClaim();
    }

    public @NonNull Long getID() {
        return this.claim.getID();
    }

    public @NonNull String getOwnerName() {
        if (isAdminClaim()) {
            return GriefPrevention.instance.dataStore.getMessage(Messages.OwnerNameForAdminClaims);
        }
        if (this.claim.getOwnerID() != this.ownerId) {
            this.ownerId = this.claim.getOwnerID();
            this.ownerName = this.claim.getOwnerName();
        }
        return this.ownerName;
    }

    public @NonNull Point getMin() {
        return this.min;
    }

    public @NonNull Point getMax() {
        return this.max;
    }

    public int getArea() {
        return this.claim.getArea();
    }

    public int getWidth() {
        return this.claim.getWidth();
    }

    public int getHeight() {
        return this.claim.getHeight();
    }

    public void getPermissions(
            @NonNull ArrayList<String> builders,
            @NonNull ArrayList<String> containers,
            @NonNull ArrayList<String> accessors,
            @NonNull ArrayList<String> managers
    ) {
        this.claim.getPermissions(builders, containers, accessors, managers);
    }
}