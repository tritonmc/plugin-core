package com.rexcantor64.triton.language.parser;

import com.rexcantor64.triton.api.config.FeatureSyntax;
import net.kyori.adventure.text.TextComponent;

public class AdventureParser extends LanguageParser {
    @Override
    public Object parseComponent(String language, FeatureSyntax syntax, Object text) {
        if (text instanceof TextComponent)
            return null;

        return text;
    }
}
