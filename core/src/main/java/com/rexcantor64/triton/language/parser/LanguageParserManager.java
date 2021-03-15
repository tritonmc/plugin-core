package com.rexcantor64.triton.language.parser;

import com.rexcantor64.triton.api.config.FeatureSyntax;
import com.rexcantor64.triton.player.LanguagePlayer;

import java.util.HashMap;
import java.util.function.Supplier;

public class LanguageParserManager {

    private final HashMap<Class<?>, LanguageParser> parserMap = new HashMap<>();

    public LanguageParserManager() {
        addParser("net.md_5.bungee.api.chat.BaseComponent", BaseComponentParser::new);
        addParser("[Lnet.md_5.bungee.api.chat.BaseComponent;", BaseComponentParser::new);
        addParser("net.kyori.adventure.text.Component", AdventureParser::new);
        parserMap.put(String.class, new StringParser());
    }

    private void addParser(String className, Supplier<LanguageParser> parser) {
        try {
            parserMap.put(Class.forName(className), parser.get());
        } catch (ClassNotFoundException ignore) {
            // Library does not exist in this environment
        }
    }

    public <T> TranslationResult<T> parseComponent(LanguagePlayer p, FeatureSyntax syntax, T text) {
        return parseComponent(p.getLang().getName(), syntax, text);
    }

    public <T> TranslationResult<T> parseComponent(String lang, FeatureSyntax syntax, T component) {
        if (parserMap.containsKey(component.getClass()))
            return parserMap.get(component.getClass()).parseComponent(lang, syntax, component);

        throw new RuntimeException("Tried to pass an unknown type to the language parser (" +
                component.getClass().toString() + "). Falling back to string parsing.");
    }

}
