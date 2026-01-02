package ua.nanit.limbo.litebans;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

public class LiteBansIntegration {

    @Getter
    private final Connection connection;
    private final LoadingCache<@NotNull UUID, Ban> cache = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofSeconds(30))
        .build(this::findCurrentBan);

    public LiteBansIntegration(String connectionString) throws Exception {
        this.connection = DriverManager.getConnection(connectionString);
    }

    public @NotNull Optional<Ban> getCurrentBan(UUID uniqueId) {
        return Optional.ofNullable(cache.get(uniqueId));
    }

    public @Nullable Ban findCurrentBan(UUID uniqueId) {
        String sql = """
            SELECT ban AS type, b.*
            FROM litebans_bans b
            WHERE uuid = ? AND active = 1
            ORDER BY time DESC
            LIMIT 1
            """;
        try (PreparedStatement statement = this.connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            if (rs.next()) {
                String bannedByName = rs.getString("banned_by_name");
                long start = rs.getLong("time");
                long end = rs.getLong("until");
                boolean isIpBan = rs.getBoolean("ipban");
                boolean isActive = rs.getBoolean("active");

                return new Ban(bannedByName, start, end, isIpBan, isActive);
            }
        } catch (SQLException error) {
            throw new RuntimeException(error);
        }

        return null;
    }
}
