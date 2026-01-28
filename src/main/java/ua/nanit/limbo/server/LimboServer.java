/*
 * Copyright (C) 2020 Nan1t
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ua.nanit.limbo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.util.ResourceLeakDetector;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.nanit.limbo.configuration.LimboConfig;
import ua.nanit.limbo.connection.ClientChannelInitializer;
import ua.nanit.limbo.connection.ClientConnection;
import ua.nanit.limbo.connection.PacketHandler;
import ua.nanit.limbo.connection.PacketSnapshots;
import ua.nanit.limbo.litebans.LiteBansIntegration;
import ua.nanit.limbo.world.DimensionRegistry;

import java.nio.file.Paths;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Getter
public final class LimboServer {

    private static LimboServer INSTANCE;

    private LimboConfig config;
    private PacketHandler packetHandler;
    private Connections connections;
    private DimensionRegistry dimensionRegistry;
    private ScheduledFuture<?> keepAliveTask;
    private @Nullable LiteBansIntegration liteBans;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private CommandManager commandManager;

    public void start() throws Exception {
        INSTANCE = this;

        config = new LimboConfig(Paths.get("./"));
        config.load();

        Log.setLevel(config.getDebugLevel());
        Log.info("Starting server...");

        if (System.getProperty("io.netty.leakDetectionLevel") == null && System.getProperty("io.netty.leakDetection.level") == null) {
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);
        }

        packetHandler = new PacketHandler(this);
        dimensionRegistry = new DimensionRegistry(this);
        dimensionRegistry.load();
        connections = new Connections(config.isLogIPs());

        PacketSnapshots.initPackets(this);

        startBootstrap();

        keepAliveTask = workerGroup.scheduleAtFixedRate(this::broadcastKeepAlive, 0L, 5L, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop, "NanoLimbo shutdown thread"));

        Log.info("Server started on %s", config.getAddress());

        commandManager = new CommandManager();
        commandManager.registerAll(this);
        commandManager.start();

        if (config.isLiteBansIntegration()) {
            Log.info("Connecting to LiteBans Database...");
            try {
                this.liteBans = new LiteBansIntegration(config.getLiteBansConnectionString());
                Log.info("Success! Connected to LiteBans Database.");
            } catch (Exception error) {
                Log.error("Failed to connect to LiteBans", error);
            }
        }

        System.gc();
    }

    private void startBootstrap() {
        TransportType transportType = config.getTransportType();
        if (!transportType.isAvailable()) {
            Log.debug("Transport type " + transportType.name() + " is not available! Using NIO.");
            transportType = TransportType.NIO;
        }

        Log.debug("Using " + transportType.name() + " transport type");

        ChannelFactory<? extends ServerChannel> channelFactory = transportType.getChannelFactory();
        IoHandlerFactory ioHandlerFactory = transportType.getIoHandlerFactory();

        bossGroup = new MultiThreadIoEventLoopGroup(config.getBossGroupSize(), ioHandlerFactory);
        workerGroup = new MultiThreadIoEventLoopGroup(config.getWorkerGroupSize(), ioHandlerFactory);

        new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channelFactory(channelFactory)
                .childHandler(new ClientChannelInitializer(this))
                .childOption(ChannelOption.TCP_NODELAY, true)
                .localAddress(config.getAddress())
                .bind();
    }

    private void broadcastKeepAlive() {
        connections.getAllConnections().forEach(ClientConnection::sendKeepAlive);
    }

    private void stop() {
        Log.info("Stopping server...");

        if (keepAliveTask != null) {
            keepAliveTask.cancel(true);
        }

        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }

        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }

        Log.info("Server stopped, Goodbye!");
    }

    public @NotNull Optional<LiteBansIntegration> getLiteBans() {
        return Optional.ofNullable(this.liteBans);
    }

    public static @NotNull LimboServer getInstance() {
        return INSTANCE;
    }
}
