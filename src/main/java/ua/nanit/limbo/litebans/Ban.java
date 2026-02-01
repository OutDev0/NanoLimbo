package ua.nanit.limbo.litebans;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
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

    public Instant getExpiry() {
        return Instant.ofEpochMilli(end);
    }

    public boolean isExpired() {
        if (end == 0) {
            return !isActive;
        }

        return !isActive && Instant.now().isAfter(getExpiry());
    }

    public @NotNull Component constructKickMessage() {
        Instant now = Instant.now();

        String durationString;
        if (end == 0) {
            durationString = "Never (Permanent)";
        } else {
            Instant expiry = getExpiry();

            Duration remaining = Duration.between(now, expiry);
            if (remaining.isNegative()) {
                remaining = Duration.ZERO;
            }

            durationString = DurationFormatter.formatDuration(remaining)
                    + " ("
                    + DATE_TIME_FORMATTER.format(expiry.atZone(ZoneId.systemDefault()))
                    + ")";
        }

        String banMessage = LimboServer.getInstance()
                .getConfig()
                .getLiteBansKickMessageFormat();

        return MINI_MESSAGE.deserialize(
                banMessage
                        .replace("$reason", reason())
                        .replace("$duration", durationString)
        );
    }

}
