package com.rexcantor64.triton.language;

import com.rexcantor64.triton.api.config.FeatureSyntax;
import com.rexcantor64.triton.player.LanguagePlayer;
import com.rexcantor64.triton.utils.ComponentUtils;
import lombok.val;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;

import java.util.ArrayList;
import java.util.List;

public class LanguageParser implements com.rexcantor64.triton.api.language.LanguageParser {

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

    public boolean hasTranslatableComponent(BaseComponent... comps) {
        for (BaseComponent c : comps) {
            if (c instanceof TranslatableComponent)
                return true;
            if (c.getExtra() != null && hasTranslatableComponent(c.getExtra().toArray(new BaseComponent[0])))
                return true;
        }
        return false;
    }

    public String parseString(String language, FeatureSyntax syntax, String input) {
        return input;
    }

    public String replaceLanguages(String input, LanguagePlayer p, FeatureSyntax syntax) {
        return input;
    }

    public String replaceLanguages(String input, String lang, FeatureSyntax syntax) {
        return input;
    }


    private List<BaseComponent> removeTritonLinks(BaseComponent... baseComponents) {
        val result = new ArrayList<BaseComponent>();
        for (val component : baseComponents) {
            if (component.getClickEvent() != null && component.getClickEvent()
                    .getAction() == ClickEvent.Action.OPEN_URL && !ComponentUtils
                    .isLink(component.getClickEvent().getValue()))
                component.setClickEvent(null);
            if (component.getExtra() != null)
                component.setExtra(removeTritonLinks(component.getExtra().toArray(new BaseComponent[0])));

            val lastComp = result.size() > 0 ? result.get(result.size() - 1) : null;
            if (lastComp instanceof TextComponent &&
                    component instanceof TextComponent &&
                    !ComponentUtils.hasExtra(lastComp) &&
                    !ComponentUtils.hasExtra(component) &&
                    ComponentUtils.haveSameFormatting(lastComp, component)
            ) {
                val lastTextComp = (TextComponent) lastComp;
                val textComp = (TextComponent) component;
                lastTextComp.setText(lastTextComp.getText() + textComp.getText());
                continue;
            }
            result.add(component);
        }
        return result;
    }

    public BaseComponent[] parseComponent(LanguagePlayer p, FeatureSyntax syntax, BaseComponent... text) {
        return parseComponent(p.getLang().getName(), syntax, text);
    }

    public BaseComponent[] parseComponent(String language, FeatureSyntax syntax, BaseComponent... text) {
        return text;
    }


}
