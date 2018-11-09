package com.rexcantor64.triton.bridge;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.rexcantor64.triton.MultiLanguagePlugin;
import com.rexcantor64.triton.config.MainConfig;
import com.rexcantor64.triton.language.Language;
import com.rexcantor64.triton.language.item.LanguageItem;
import com.rexcantor64.triton.language.item.LanguageSign;
import com.rexcantor64.triton.language.item.LanguageText;
import com.rexcantor64.triton.player.LanguagePlayer;
import com.rexcantor64.triton.player.SpigotLanguagePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SpigotBridgeManager implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        if (!channel.equals("triton:main")) return;

        long start = System.currentTimeMillis();

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
        try {
            byte action = in.readByte();
            if (action == 0) {
                try {
                    MainConfig config = MultiLanguagePlugin.get().getConf();
                    config.setMainLanguage(in.readUTF());
                    short languageSize = in.readShort();
                    JSONObject languages = new JSONObject();
                    for (int i = 0; i < languageSize; i++) {
                        String name = in.readUTF();
                        String displayName = in.readUTF();
                        String flag = in.readUTF();
                        List<String> minecraftCodes = new ArrayList<>();
                        short mcSize = in.readShort();
                        for (int k = 0; k < mcSize; k++)
                            minecraftCodes.add(in.readUTF());
                        JSONObject lang = new JSONObject();
                        lang.put("flag", flag);
                        lang.put("minecraft-code", minecraftCodes);
                        lang.put("display-name", displayName);
                        lang.put("main", name.equals(config.getMainLanguage()));
                        languages.put(name, lang);
                    }
                    config.setLanguages(languages);
                    File file = new File(MultiLanguagePlugin.get().getDataFolder(), "cache.json");
                    Files.write(file.toPath(), languages.toString(4).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
                    // Read language files
                    List<LanguageItem> languageItems = new ArrayList<>();
                    int itemsSize = in.readInt();
                    for (int i = 0; i < itemsSize; i++) {
                        byte type = in.readByte();
                        String key = in.readUTF();
                        switch (type) {
                            case 0:
                                HashMap<String, String> msgs = new HashMap<>();
                                short langSize2 = in.readShort();
                                for (int k = 0; k < langSize2; k++)
                                    msgs.put(in.readUTF(), in.readUTF());
                                languageItems.add(new LanguageText(key, msgs));
                                break;
                            case 1:
                                List<LanguageSign.SignLocation> signLocations = new ArrayList<>();
                                short locSize = in.readShort();
                                for (int k = 0; k < locSize; k++) {
                                    signLocations.add(new LanguageSign.SignLocation(in.readUTF(), in.readInt(), in.readInt(), in.readInt()));
                                }
                                HashMap<String, String[]> signLines = new HashMap<>();
                                short langSize = in.readShort();
                                for (int k = 0; k < langSize; k++)
                                    signLines.put(in.readUTF(), new String[]{in.readUTF(), in.readUTF(), in.readUTF(), in.readUTF()});
                                languageItems.add(new LanguageSign(key, signLocations, signLines));
                                break;
                            default:
                                MultiLanguagePlugin.get().logDebugWarning("Received invalid type language item type while reading from BungeeCord: %1", type);
                                break;
                        }
                    }
                    MultiLanguagePlugin.get().getLanguageConfig().setItems(languageItems).saveToCache();
                    MultiLanguagePlugin.get().logDebug("Received config from BungeeCord and parsed it in %1ms!", System.currentTimeMillis() - start);
                } finally {
                    MultiLanguagePlugin.get().getLanguageManager().setup();
                    Bukkit.getScheduler().runTaskLater(MultiLanguagePlugin.get().getLoader().asSpigot(), () -> {
                        for (LanguagePlayer lp : MultiLanguagePlugin.get().getPlayerManager().getAll())
                            ((SpigotLanguagePlayer) lp).refreshAll();
                    }, 10L);
                }
            } else if (action == 1) {
                final UUID uuid = UUID.fromString(in.readUTF());
                final Language lang = MultiLanguagePlugin.get().getLanguageManager().getLanguageByName(in.readUTF(), true);
                Bukkit.getScheduler().runTaskLater(MultiLanguagePlugin.get().getLoader().asSpigot(), () -> ((SpigotLanguagePlayer) MultiLanguagePlugin.get().getPlayerManager().get(uuid)).setLang(lang, false), 10L);
            } else if (action == 2) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), in.readUTF());
            }
        } catch (Exception e) {
            MultiLanguagePlugin.get().logError("Failed to parse plugin message: %1", e.getMessage());
            if (MultiLanguagePlugin.get().getConf().isDebug())
                e.printStackTrace();
        }
    }

    public void updatePlayerLanguage(SpigotLanguagePlayer lp) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        // Action (0): updatePlayerLanguage
        out.writeByte(0);
        out.writeUTF(lp.getUUID().toString());
        out.writeUTF(lp.getLang().getName());
        lp.toBukkit().sendPluginMessage(MultiLanguagePlugin.get().getLoader().asSpigot(), "triton:main", out.toByteArray());
    }

}