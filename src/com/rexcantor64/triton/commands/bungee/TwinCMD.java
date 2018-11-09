package com.rexcantor64.triton.commands.bungee;

import com.rexcantor64.triton.MultiLanguagePlugin;
import com.rexcantor64.triton.web.TwinManager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;

@SuppressWarnings("deprecation")
public class TwinCMD extends Command {

    public TwinCMD() {
        super("twin");

    }

    @Override
    public void execute(CommandSender s, String[] args) {
        if (args.length == 0) {
            if (!s.hasPermission("twin.upload")) {
                s.sendMessage(MultiLanguagePlugin.get().getMessage("error.no-permission", "&cNo permission. Permission required: &4%1", "twin.upload"));
                return;
            }
            BungeeCord.getInstance().getScheduler().runAsync(MultiLanguagePlugin.get().getLoader().asBungee(), () -> upload(s));
        } else {
            if (!s.hasPermission("twin.download")) {
                s.sendMessage(MultiLanguagePlugin.get().getMessage("error.no-permission", "&cNo permission. Permission required: &4%1", "twin.download"));
                return;
            }
            BungeeCord.getInstance().getScheduler().runAsync(MultiLanguagePlugin.get().getLoader().asBungee(), () -> download(s, args[0]));
        }
    }

    private void upload(CommandSender s) {
        s.sendMessage(MultiLanguagePlugin.get().getMessage("twin.connecting", "&aConnecting to TWIN... Please wait"));

        TwinManager.HttpResponse response = MultiLanguagePlugin.get().getTwinManager().upload();

        if (response == null) {
            s.sendMessage(MultiLanguagePlugin.get().getMessage("twin.failed-bungeecord", "&cCan't upload the config because you have BungeeCord enabled on config! Please execute this command through BungeeCord."));
            return;
        }

        if (response.getStatusCode() == 0) {
            s.sendMessage(MultiLanguagePlugin.get().getMessage("twin.no-internet", "&4Failed to upload config. Please check your internet connection and/or firewall! Error description: %1", response.getPage()));
            return;
        }

        if (response.getStatusCode() == 401) {
            s.sendMessage(MultiLanguagePlugin.get().getMessage("twin.no-token", "&4Invalid token! Please check if you have setup TWIN correctly on config."));
            return;
        }

        if (response.getStatusCode() != 200) {
            s.sendMessage(MultiLanguagePlugin.get().getMessage("twin.failed-upload", "&cFailed to upload the config: %1", MultiLanguagePlugin.get().getMessage("twin.incorrect-status", "&4status is not 200 (received &l%1&4)", response.getStatusCode())));
            return;
        }

        s.sendMessage(MultiLanguagePlugin.get().getMessage("twin.uploaded", "&aYour config is live! Start editing now at &6%1", "https://twin.rexcantor64.com/" + response.getPage()));
    }

    private void download(CommandSender s, String id) {
        s.sendMessage(MultiLanguagePlugin.get().getMessage("twin.connecting", "&aConnecting to TWIN... Please wait"));

        TwinManager.HttpResponse response = MultiLanguagePlugin.get().getTwinManager().download(id);

        if (response == null) {
            s.sendMessage(MultiLanguagePlugin.get().getMessage("twin.failed-bungeecord", "&cCan't upload the config because you have BungeeCord enabled on config! Please execute this command through BungeeCord."));
            return;
        }

        if (response.getStatusCode() == 0) {
            s.sendMessage(MultiLanguagePlugin.get().getMessage("twin.no-internet", "&4please check your internet connection and/or firewall! Error description: %1", response.getPage()));
            return;
        }

        if (response.getStatusCode() != 200) {
            s.sendMessage(MultiLanguagePlugin.get().getMessage("twin.failed-fetch", "&cFailed to fetch the config: %1", MultiLanguagePlugin.get().getMessage("twin.incorrect-status", "&4status is not 200 (received &l%1&4)", response.getStatusCode())));
            return;
        }

        try {
            JSONObject responseJson = new JSONObject(response.getPage());
            JSONArray storage = MultiLanguagePlugin.get().getLanguageConfig().getRaw();
            JSONArray deleted = responseJson.optJSONArray("deleted");
            JSONObject modified = responseJson.optJSONObject("modified");

            storageLoop:
            for (int k = 0; k < storage.length(); k++) {
                JSONObject obj = storage.optJSONObject(k);
                if (obj == null) continue;

                if (deleted != null)
                    for (int i = 0; i < deleted.length(); i++) {
                        String key = deleted.optString(i);
                        if (key.isEmpty()) continue;
                        if (key.equals(obj.optString("key"))) {
                            storage.remove(k--);
                            continue storageLoop;
                        }
                    }
                if (modified != null) {
                    String key = obj.optString("key");
                    if (!key.isEmpty() && modified.optJSONObject(key) != null)
                        storage.put(k, modified.optJSONObject(key));
                }
            }
            JSONArray added = responseJson.optJSONArray("added");

            if (added != null)
                for (int k = 0; k < added.length(); k++)
                    if (added.optJSONObject(k) != null) storage.put(added.optJSONObject(k));

            try {
                FileWriter fileWriter = new FileWriter(new File(MultiLanguagePlugin.get().getDataFolder(), "languages.json"));
                fileWriter.write(storage.toString(4));
                fileWriter.flush();
            } catch (Exception e) {
                s.sendMessage(MultiLanguagePlugin.get().getMessage("twin.failed-file-update", "&cError while writing to file '%1': %2", "languages.json", e.getMessage()));
            }
            MultiLanguagePlugin.get().reload();
            s.sendMessage(MultiLanguagePlugin.get().getMessage("twin.success", "&aSuccessfully fetched the config from TWIN and applied it into the server!"));
        } catch (Exception e) {
            s.sendMessage(MultiLanguagePlugin.get().getMessage("twin.failed-fetch", "&cFailed to fetch the config: %1", e.getMessage()));
        }
    }
}