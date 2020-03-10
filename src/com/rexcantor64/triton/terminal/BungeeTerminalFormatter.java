package com.rexcantor64.triton.terminal;

import com.rexcantor64.triton.Triton;
import net.md_5.bungee.log.ConciseFormatter;

import java.util.logging.LogRecord;

public class BungeeTerminalFormatter extends ConciseFormatter {

    @Override
    public String format(LogRecord record) {
        String superResult = super.format(record);
        String result = Triton.get().getLanguageParser()
                .replaceLanguages(superResult, Triton.get().getLanguageManager().getMainLanguage().getName(), Triton
                        .get().getConf().getChatSyntax());
        if (result != null) return result;
        return superResult;
    }
}