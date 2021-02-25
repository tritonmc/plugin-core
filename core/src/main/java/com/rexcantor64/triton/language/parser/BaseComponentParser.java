package com.rexcantor64.triton.language.parser;

import com.rexcantor64.triton.api.config.FeatureSyntax;
import net.md_5.bungee.api.chat.BaseComponent;

public class BaseComponentParser extends LanguageParser {
    @Override
    public <T> T parseComponent(String language, FeatureSyntax syntax, T text) {
        if (text instanceof BaseComponent)
            return (T) parse(language, syntax, (BaseComponent) text)[0];
        if (text instanceof BaseComponent[])
            return (T) parse(language, syntax, (BaseComponent[]) text);
        return text;
    }

    public BaseComponent[] parse(String language, FeatureSyntax syntax, BaseComponent... text) {
        
        return text;
    }
}
