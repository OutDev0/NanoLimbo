package ua.nanit.limbo.litebans;

import java.time.Instant;

public record Ban(
    String bannedByName,
    long start,
    long end,
    boolean isIpBan,
    boolean isActive
) {
    public Instant getExpiry() {
        return Instant.ofEpochMilli(end);
    }

    public boolean isExpired() {
        return Instant.now().isAfter(getExpiry());
    }
}
