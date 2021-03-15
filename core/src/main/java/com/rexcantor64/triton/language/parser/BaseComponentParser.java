package com.rexcantor64.triton.language.parser;

import com.google.gson.JsonParseException;
import com.rexcantor64.triton.Triton;
import com.rexcantor64.triton.api.config.FeatureSyntax;
import com.rexcantor64.triton.wrappers.legacy.HoverComponentWrapper;
import lombok.val;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

import java.util.Objects;
import java.util.stream.Collectors;

public class BaseComponentParser extends LanguageParser {
    @Override
    public <T> TranslationResult<T> parseComponent(String language, FeatureSyntax syntax, T text) {
        val result = new TranslationResult<T>();
        result.setResult(text);
        if (text instanceof BaseComponent) {
            val parseResult = parse(language, syntax, (BaseComponent) text);
            result.setDisabled(parseResult.isDisabled());
            result.setModified(parseResult.isModified());
            if (parseResult.getResult() != null) result.setResult((T) parseResult.getResult()[0]);
        } else if (text instanceof BaseComponent[]) {
            return (TranslationResult<T>) parse(language, syntax, (BaseComponent[]) text);

        }
        return result;
    }

    @Override
    protected AdvancedComponent advancedComponentFromJson(String json) throws JsonParseException {
        return Md5AdvancedComponent.fromBaseComponent(ComponentSerializer.parse(json));
    }

    @Override
    protected AdvancedComponent advancedComponentFromLegacy(String text) {
        return Md5AdvancedComponent.fromBaseComponent(TextComponent.fromLegacyText(text));
    }

    public TranslationResult<BaseComponent[]> parse(String language, FeatureSyntax syntax, BaseComponent... text) {
        val result = new TranslationResult<BaseComponent[]>();

        val advancedCompResult = super.parseAdvancedComponent(language, syntax, Md5AdvancedComponent.fromBaseComponent(text));
        if (advancedCompResult.isDisabled()) {
            result.setDisabled(true);
            return result;
        }
        result.setModified(advancedCompResult.isModified());
        val advancedComponent = advancedCompResult.getResult();

        try {
            for (val entry : advancedComponent.getHovers().entrySet())
                entry.setValue(com.rexcantor64.triton.wrappers.HoverComponentWrapper
                        .handleHoverEvent((HoverEvent) entry.getValue(), language, syntax));
        } catch (NoSuchMethodError e) {
            for (val entry : advancedComponent.getHovers().entrySet()) {
                val event = (HoverEvent) entry.getValue();
                val comps = HoverComponentWrapper.getValue(event);
                val string = TextComponent.toLegacyText(comps);
                val replaced = replaceLanguages(Triton.get().getLanguageManager()
                        .matchPattern(string, language), language, syntax);
                if (!Objects.equals(string, replaced)) result.setModified(true);
                if (replaced == null) {
                    if (event.getAction() != HoverEvent.Action.SHOW_ITEM)
                        entry.setValue(null);
                    continue;
                }
                entry.setValue(HoverComponentWrapper
                        .setValue(event, TextComponent.fromLegacyText(replaced)));
            }
        }

        for (val entry : advancedComponent.getAllTranslatableArguments().entrySet())
            advancedComponent.getAllTranslatableArguments().put(entry.getKey(), entry.getValue().stream()
                    .map(comp -> super.parseAdvancedComponent(language, syntax, comp).getResult()).collect(Collectors.toList()));

        result.setResult(((Md5AdvancedComponent) advancedComponent).toBaseComponent());
        return result;
    }


}
