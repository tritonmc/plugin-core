package com.rexcantor64.triton.language.parser;

import com.google.gson.JsonParseException;
import com.rexcantor64.triton.api.config.FeatureSyntax;
import lombok.val;

public class AdventureParser extends LanguageParser {
    @Override
    public <T> TranslationResult<T> parseComponent(String language, FeatureSyntax syntax, T text) {
        val result = new TranslationResult<T>();
        result.setResult(text);
        return result;
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
