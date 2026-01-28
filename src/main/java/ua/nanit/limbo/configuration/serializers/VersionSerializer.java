package ua.nanit.limbo.configuration.serializers;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import ua.nanit.limbo.protocol.registry.Version;

import java.lang.reflect.Type;

public class VersionSerializer implements TypeSerializer<Version> {

    @Override
    public Version deserialize(@NonNull Type type, @NonNull ConfigurationNode node) throws SerializationException {
        try {
            return Version.of(node.getInt(-1));
        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public void serialize(@NonNull Type type, @Nullable Version obj, @NonNull ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.raw(null);
            return;
        }

        node.set(Integer.class, obj.name());
    }
}
