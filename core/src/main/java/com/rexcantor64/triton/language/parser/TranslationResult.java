package com.rexcantor64.triton.language.parser;

import lombok.Data;

@Data
public class TranslationResult<T> {
    private T result;
    private boolean modified = false;
    private boolean disabled = false;

}
