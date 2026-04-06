package ua.nanit.limbo.litebans;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import ua.nanit.limbo.server.LimboServer;
import ua.nanit.limbo.util.DurationFormatter;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public record Ban(
        String bannedByName,
        String reason,
        long start,
        long end,
        boolean isIpBan,
        boolean isActive
) {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();

    public boolean isPermanent() {
        return end == 0;
    }

    public Instant getExpiry() {
        return Instant.ofEpochMilli(end);
    }

    public @NotNull Duration getRemainingDuration() {
        if (isPermanent()) {
            return Duration.ZERO;
        }

        Duration remaining = Duration.between(Instant.now(), getExpiry());
        if (remaining.isNegative()) {
            return Duration.ZERO;
        }

        return remaining;
    }

    public boolean isExpired() {
        if (isPermanent()) {
            return !isActive;
        }

        return !isActive || getRemainingDuration().isZero();
    }

    public @NotNull Component constructKickMessage() {
        String durationString;
        if (isPermanent()) {
            durationString = "Never (Permanent)";
        } else {
            Instant expiry = getExpiry();
            durationString = DurationFormatter.formatDuration(getRemainingDuration())
                    + " ("
                    + DATE_TIME_FORMATTER.format(expiry.atZone(ZoneId.systemDefault()))
                    + ")";
        }

        String normalizedReason = reason()
                .replace("\\r\\n", "\n")
                .replace("\\n", "\n")
                .replace("\r\n", "\n")
                .replace("\r", "\n");

        Component reasonComponent = LEGACY.deserialize(normalizedReason);
        String template = LimboServer.getInstance()
                .getConfig()
                .getLiteBansKickMessageFormat();

        return MINI_MESSAGE.deserialize(
                template,
                TagResolver.builder()
                        .resolver(Placeholder.component("reason", reasonComponent))
                        .resolver(Placeholder.unparsed("duration", durationString))
                        .build()
        );
    }
}
