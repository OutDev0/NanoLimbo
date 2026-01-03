package ua.nanit.limbo.world;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ua.nanit.limbo.protocol.registry.Version;
import ua.nanit.limbo.server.data.NamespacedKey;

import java.util.EnumMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public enum DimensionType {

    OVERWORLD(NamespacedKey.minecraft("overworld"), 0),
    THE_END(NamespacedKey.minecraft("the_end"), 1),
    THE_NETHER(NamespacedKey.minecraft("the_nether"), -1);

    private final NamespacedKey key;
    private final int legacyDimensionId;

    @NonNull
    public VersionedDimension createVersionedDimension(@NonNull DimensionRegistry dimensionRegistry) {
        Map<Version, Dimension> perVersionDimension = new EnumMap<>(Version.class);
        for (Version version : Version.values()) {
            Dimension dimension = dimensionRegistry.findDimension(version, this.key);
            if (dimension == null) {
                continue;
            }
            perVersionDimension.put(version, dimension);
        }
        return new VersionedDimension(this.key, this.legacyDimensionId, perVersionDimension);
    }

}
