package ua.nanit.limbo.litebans;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import ua.nanit.limbo.server.LimboServer;
import ua.nanit.limbo.util.DurationFormatter;
import ua.nanit.limbo.util.NbtMessageUtil;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public record Ban(
    String bannedByName,
    String reason,
    long start,
    long end,
    boolean isIpBan,
    boolean isActive
) {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss");

    public Duration getDuration() {
        return Duration.between(
            Instant.ofEpochMilli(start),
            Instant.ofEpochMilli(end)
        );
    }

    public Instant getExpiry() {
        return Instant.ofEpochMilli(end);
    }

    public boolean isExpired() {
        return !isActive || Instant.now().isAfter(getExpiry());
    }

    public ZonedDateTime getEndDateTime() {
        return getExpiry().atZone(ZoneId.systemDefault());
    }

    public @NotNull Component constructKickMessage() {
        String durationString = end == 0
            ? "Never (Permanent)"
            : DurationFormatter.formatDuration(getDuration())
            + " ("
            + DATE_TIME_FORMATTER.format(getEndDateTime())
            + ")";

        String banMessage = LimboServer.getInstance()
            .getConfig()
            .getLiteBansKickMessageFormat();

        return NbtMessageUtil.MINI_MESSAGE.deserialize(
            banMessage
                .replace("$reason", reason())
                .replace("$duration", durationString)
        );
    }
}
