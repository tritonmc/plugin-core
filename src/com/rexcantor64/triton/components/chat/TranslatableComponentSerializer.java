package com.rexcantor64.triton.components.chat;

import com.google.gson.*;
import com.rexcantor64.triton.components.api.chat.BaseComponent;
import com.rexcantor64.triton.components.api.chat.TranslatableComponent;

import java.lang.reflect.Type;
import java.util.Arrays;

public class TranslatableComponentSerializer
        extends BaseComponentSerializer
        implements JsonSerializer<TranslatableComponent>, JsonDeserializer<TranslatableComponent> {
    public TranslatableComponent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        TranslatableComponent component = new TranslatableComponent();
        JsonObject object = json.getAsJsonObject();
        deserialize(object, component, context);
        component.setTranslate(object.get("translate").getAsString());
        if (object.has("with")) {
            component.setWith(Arrays.asList((BaseComponent[]) context.deserialize(object.get("with"), BaseComponent[].class)));
        }
        return component;
    }

    public JsonElement serialize(TranslatableComponent src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        serialize(object, src, context);
        object.addProperty("translate", src.getTranslate());
        if (src.getWith() != null) {
            object.add("with", context.serialize(src.getWith()));
        }
        return object;
    }
}