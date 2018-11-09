package com.rexcantor64.triton.language.item;

import com.rexcantor64.triton.components.api.ChatColor;
import com.rexcantor64.triton.utils.LocationUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public abstract class LanguageItem {


    private final String key;

    public LanguageItem(String key) {
        this.key = key;
    }

    public static LanguageItem fromJSON(JSONObject obj) {
        if (obj == null) return null;
        if (obj.optBoolean("archived", false)) return null;
        LanguageItemType type = LanguageItemType.getType(obj.optString("type", ""));
        if (type == null) return null;
        String key = obj.optString("key");
        if (key == null || key.isEmpty()) return null;
        switch (type) {
            case TEXT:
                JSONObject languages = obj.optJSONObject("languages");
                if (languages == null) return null;
                HashMap<String, String> map = new HashMap<>();
                for (String lKey : languages.keySet()) {
                    String a = languages.optString(lKey);
                    if (a != null) map.put(lKey, a);
                }
                if (map.size() == 0) return null;
                return new LanguageText(key, map, obj.optBoolean("universal", false), obj.optBoolean("blacklist", false), obj.optJSONArray("servers"));
            case SIGN:
                JSONArray loc = obj.optJSONArray("locations");
                if (loc == null) return null;
                JSONObject signLanguages = obj.optJSONObject("lines");
                if (signLanguages == null) return null;
                HashMap<String, String[]> signMap = new HashMap<>();
                for (String lKey : signLanguages.keySet()) {
                    JSONArray a = signLanguages.optJSONArray(lKey);
                    String[] b = new String[4];
                    if (a != null)
                        for (int k = 0; k < 4; k++)
                            if (a.length() > k)
                                b[k] = ChatColor.translateAlternateColorCodes('&', a.optString(k, ""));
                            else
                                b[k] = "";
                    signMap.put(lKey, b);
                }
                if (signMap.size() == 0) return null;
                return new LanguageSign(key, LocationUtils.jsonToLocationArray(loc), signMap);
        }
        return null;
    }

    public String getKey() {
        return key;
    }

    public abstract LanguageItemType getType();

    public enum LanguageItemType {
        TEXT("text"), SIGN("sign");

        private final String code;

        LanguageItemType(String code) {
            this.code = code;
        }

        private static LanguageItemType getType(String code) {
            for (LanguageItemType item : values())
                if (item.code.equalsIgnoreCase(code)) return item;
            return null;
        }
    }

}