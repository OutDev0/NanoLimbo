package ua.nanit.limbo.world;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import ua.nanit.limbo.protocol.registry.Version;
import ua.nanit.limbo.server.data.NamespacedKey;

import java.util.Map;

@RequiredArgsConstructor
public class VersionedDimension {

    @Getter
    private final NamespacedKey key;
    @Getter
    private final int legacyDimensionId;
    private final Map<Version, Dimension> perVersionDimensions;

    public int getId(@NonNull Version version) {
        return getDimensionByProtocol(version).id();
    }

    public int getHeight(@NonNull Version version) {
        return getDimensionByProtocol(version).height();
    }

    public int getChunkSections(@NonNull Version version) {
        return getHeight(version) / 16;
    }

    @NonNull
    public CompoundBinaryTag getCodec(@NonNull Version version) {
        return getDimensionByProtocol(version).codec();
    }

    @NonNull
    public CompoundBinaryTag getDefaultCodec(@NonNull Version version) {
        return getDimensionByProtocol(version).defaultCodec();
    }

    @NonNull
    private Dimension getDimensionByProtocol(@NonNull Version version) {
        Dimension dimension = this.perVersionDimensions.get(version);
        if (dimension == null) {
            throw new IllegalStateException("No dimension found for version " + version);
        }

        return dimension;
    }

}
