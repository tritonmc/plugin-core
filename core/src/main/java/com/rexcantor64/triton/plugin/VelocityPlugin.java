package com.rexcantor64.triton.plugin;

import com.google.inject.Inject;
import com.rexcantor64.triton.VelocityMLP;
import com.rexcantor64.triton.logger.SLF4JLogger;
import com.rexcantor64.triton.logger.TritonLogger;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import org.slf4j.Logger;

import java.io.InputStream;
import java.nio.file.Path;

@Plugin(id = "triton", name = "Triton", url = "https://triton.rexcantor64.com", description =
        "A plugin that replaces any message on your server, to the receiver's language, in real time!",
        version = "@version@",
        authors = {"Rexcantor64"})

@Getter
public class VelocityPlugin implements PluginLoader {
    private final ProxyServer server;
    private final TritonLogger tritonLogger;
    private final Path dataDirectory;

    @Inject
    public VelocityPlugin(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.tritonLogger = new SLF4JLogger(logger);
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onEnable(ProxyInitializeEvent event) {
        new VelocityMLP(this).onEnable();
    }

    @Override
    public PluginType getType() {
        return PluginType.VELOCITY;
    }

    @Override
    public InputStream getResourceAsStream(String fileName) {
        return VelocityPlugin.class.getResourceAsStream("/" + fileName);
    }
}
