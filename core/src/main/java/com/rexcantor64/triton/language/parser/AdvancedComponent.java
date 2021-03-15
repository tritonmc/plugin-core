package com.rexcantor64.triton.language.parser;

import lombok.Getter;
import lombok.var;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class AdvancedComponent {

    @Getter
    private String text;
    @Getter
    private HashMap<String, String> components = new HashMap<>();
    private HashMap<String, List<AdvancedComponent>> translatableArguments = new HashMap<>();
    @Getter
    private HashMap<String, Object> hovers = new HashMap<>();


    public String getTextClean() {
        var result = text;
        while (result.startsWith("§r"))
            result = result.substring(2);
        return result;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setComponent(UUID uuid, String text) {
        components.put(uuid.toString(), text);
    }

    public void setComponent(String uuid, String text) {
        components.put(uuid, text);
    }

    public void setHover(UUID uuid, Object hover) {
        hovers.put(uuid.toString(), hover);
    }

    public String getComponent(String uuid) {
        return components.get(uuid);
    }

    public void setTranslatableArguments(String uuid, List<AdvancedComponent> list) {
        translatableArguments.put(uuid, list);
    }

    public List<AdvancedComponent> getTranslatableArguments(String uuid) {
        return translatableArguments.get(uuid);
    }

    public HashMap<String, List<AdvancedComponent>> getAllTranslatableArguments() {
        return translatableArguments;
    }

    @Override
    public String toString() {
        return "AdvancedComponent{" +
                "text='" + text + '\'' +
                ", components=" + components +
                ", translatableArguments=" + translatableArguments +
                '}';
    }
}