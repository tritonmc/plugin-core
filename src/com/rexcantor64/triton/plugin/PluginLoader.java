package com.rexcantor64.triton.plugin;

import java.io.InputStream;
import java.util.logging.Logger;

public interface PluginLoader {

    PluginType getType();

    SpigotPlugin asSpigot();

    BungeePlugin asBungee();

    Logger getLogger();

    InputStream getResourceAsStream(String fileName);

    void shutdown();

    enum PluginType {
        SPIGOT, BUNGEE
    }

}