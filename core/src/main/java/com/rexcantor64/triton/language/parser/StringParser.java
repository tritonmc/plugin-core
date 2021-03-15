package com.rexcantor64.triton.language.parser;

import com.google.gson.JsonParseException;
import com.rexcantor64.triton.api.config.FeatureSyntax;

public class StringParser extends LanguageParser {

    @Override
    public <T> TranslationResult<T> parseComponent(String language, FeatureSyntax syntax, T text) {
        return null;
    }

    @Override
    protected AdvancedComponent advancedComponentFromJson(String json) throws JsonParseException {
        return null;
    }

    @Override
    protected AdvancedComponent advancedComponentFromLegacy(String text) {
        return null;
    }
}
