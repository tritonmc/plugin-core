package com.rexcantor64.triton.language.parser;

import com.google.gson.JsonParseException;
import com.rexcantor64.triton.SpigotMLP;
import com.rexcantor64.triton.Triton;
import com.rexcantor64.triton.api.config.FeatureSyntax;
import com.rexcantor64.triton.utils.Color;
import lombok.val;
import lombok.var;

import java.util.ArrayList;
import java.util.List;

public abstract class LanguageParser {

    private static Integer[] getPatternIndex(String input, String pattern) {
        int start = -1;
        int contentLength = 0;
        int openedAmount = 0;

        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);
            if (currentChar == '[' && input.length() > i + pattern.length() + 1 && input.substring(i + 1,
                    i + 2 + pattern.length()).equals(pattern + "]")) {
                if (start == -1) start = i;
                openedAmount++;
                i += 1 + pattern.length();
            } else if (currentChar == '[' && input.length() > i + pattern.length() + 2 && input.substring(i + 1,
                    i + 3 + pattern.length()).equals("/" + pattern + "]")) {
                openedAmount--;
                if (openedAmount == 0) {
                    if (contentLength == 0) {
                        start = -1;
                        continue;
                    }
                    return new Integer[]{start, i + 3 + pattern.length(), start + pattern.length() + 2, i};
                }
            } else if (start != -1)
                contentLength++;
        }
        return null;
    }

    public static List<Integer[]> getPatternIndexArray(String input, String pattern) {
        List<Integer[]> result = new ArrayList<>();
        int start = -1;
        int openedAmount = 0;

        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);
            if (currentChar == '[' && input.length() > i + pattern.length() + 1 && input.substring(i + 1,
                    i + 2 + pattern.length()).equals(pattern + "]")) {
                if (start == -1) start = i;
                openedAmount++;
                i += 1 + pattern.length();
            } else if (currentChar == '[' && input.length() > i + pattern.length() + 2 && input.substring(i + 1,
                    i + 3 + pattern.length()).equals("/" + pattern + "]")) {
                openedAmount--;
                if (openedAmount == 0) {
                    result.add(new Integer[]{start, i + 3 + pattern.length(), start + pattern.length() + 2, i});
                    start = -1;
                }
            }
        }
        return result;
    }

    public abstract <T> TranslationResult<T> parseComponent(String language, FeatureSyntax syntax, T text);

    protected abstract AdvancedComponent advancedComponentFromJson(String json) throws JsonParseException;

    protected abstract AdvancedComponent advancedComponentFromLegacy(String text);

    public String replaceLanguages(String input, String language, FeatureSyntax syntax) {
        if (input == null) return null;
        Integer[] i;
        int safeCounter = 0;
        while ((i = getPatternIndex(input, syntax.getLang())) != null) {
            safeCounter++;
            if (safeCounter > 10) {
                Triton.get()
                        .getLogger()
                        .logError("The maximum attempts to translate a message have been exceeded. To prevent the " +
                                "server from crashing, the message might not have been translated. If using " +
                                "BungeeCord, restarting your proxy might fix the problem.");
                break;
            }
            StringBuilder builder = new StringBuilder();
            builder.append(input, 0, i[0]);
            String placeholder = input.substring(i[2], i[3]);
            if (!Triton.get().getConf().getDisabledLine().isEmpty() && Color.stripColor(placeholder)
                    .equals(Triton.get().getConf().getDisabledLine()))
                return null;
            Integer[] argsIndex = getPatternIndex(placeholder, syntax.getArgs());
            if (argsIndex == null) {
                builder.append(SpigotMLP.get().getLanguageManager()
                        .getText(language, Color.stripColor(placeholder)));
                builder.append(input.substring(i[1]));
                input = builder.toString();
                continue;
            }
            String code = Color.stripColor(placeholder.substring(0, argsIndex[0]));
            String args = placeholder.substring(argsIndex[2], argsIndex[3]);
            List<Integer[]> argIndexList = getPatternIndexArray(args, syntax.getArg());
            Object[] argList = new Object[argIndexList.size()];
            for (int k = 0; k < argIndexList.size(); k++) {
                Integer[] argIndex = argIndexList.get(k);
                argList[k] = replaceLanguages(args.substring(argIndex[2], argIndex[3]), language, syntax);
                if (argList[k] == null)
                    return null;
            }
            builder.append(SpigotMLP.get().getLanguageManager().getText(language, code, argList));
            builder.append(input.substring(i[1]));
            input = builder.toString();
        }
        return input;
    }

    public TranslationResult<AdvancedComponent> parseAdvancedComponent(String language, FeatureSyntax syntax, AdvancedComponent advancedComponent) {
        val translationResult = new TranslationResult<AdvancedComponent>();

        var input = advancedComponent.getTextClean();
        input = Triton.get().getLanguageManager().matchPattern(input, language);
        Integer[] i;
        var safeCounter = 0;
        while ((i = getPatternIndex(input, syntax.getLang())) != null) {
            safeCounter++;
            if (safeCounter > 10) {
                Triton.get()
                        .getLogger()
                        .logError("The maximum attempts to translate a message have been exceeded. To prevent the " +
                                "server from crashing, the message might not have been translated. If using " +
                                "BungeeCord, restarting your proxy might fix the problem.");
                break;
            }
            val builder = new StringBuilder();
            builder.append(input, 0, i[0]);
            val placeholder = input.substring(i[2], i[3]);
            val argsIndex = getPatternIndex(placeholder, syntax.getArgs());
            if (argsIndex == null) {
                if (!Triton.get().getConf().getDisabledLine().isEmpty() && Color.stripColor(placeholder)
                        .equals(Triton.get().getConf().getDisabledLine())) {
                    translationResult.setDisabled(true);
                    return translationResult;
                }
                val result = parseTritonTranslation(Triton.get().getLanguageManager()
                        .getText(language, Color.stripColor(placeholder)));
                advancedComponent.getComponents().putAll(result.getComponents());
                advancedComponent.getHovers().putAll(result.getHovers());
                advancedComponent.getAllTranslatableArguments().putAll(result.getAllTranslatableArguments());
                builder.append(result.getTextClean());
                builder.append(input.substring(i[1]));
                input = builder.toString();
                translationResult.setModified(true);
                continue;
            }
            val code = Color.stripColor(placeholder.substring(0, argsIndex[0]));
            if (!Triton.get().getConf().getDisabledLine().isEmpty() && code
                    .equals(Triton.get().getConf().getDisabledLine())) {
                translationResult.setDisabled(true);
                return translationResult;
            }
            val args = placeholder.substring(argsIndex[2], argsIndex[3]);
            val argIndexList = getPatternIndexArray(args, syntax.getArg());
            val argList = new Object[argIndexList.size()];
            for (int k = 0; k < argIndexList.size(); k++) {
                Integer[] argIndex = argIndexList.get(k);
                argList[k] = replaceLanguages(args.substring(argIndex[2], argIndex[3]), language, syntax);
            }
            val result = parseTritonTranslation(SpigotMLP.get().getLanguageManager().getText(language, code, argList));
            advancedComponent.getComponents().putAll(result.getComponents());
            advancedComponent.getHovers().putAll(result.getHovers());
            advancedComponent.getAllTranslatableArguments().putAll(result.getAllTranslatableArguments());
            builder.append(result.getTextClean());
            builder.append(input.substring(i[1]));
            input = builder.toString();
            translationResult.setModified(true);
        }
        advancedComponent.setText(input);
        for (val entry : advancedComponent.getComponents().entrySet())
            advancedComponent.setComponent(entry.getKey(), replaceLanguages(entry.getValue(), language, syntax));

        translationResult.setResult(advancedComponent);
        return translationResult;
    }

    private AdvancedComponent parseTritonTranslation(String translatedResult) {
        if (translatedResult.startsWith("[triton_json]")) {
            translatedResult = translatedResult.substring(13);
            try {
                return advancedComponentFromJson(translatedResult);
            } catch (JsonParseException e) {
                Triton.get().getLogger()
                        .logError("Failed to parse JSON translation (%1): %2", translatedResult, e.getMessage());
                if (Triton.get().getConfig().getLogLevel() >= 2)
                    e.printStackTrace();
            }
        }
        return advancedComponentFromLegacy(translatedResult);
    }

}
