package de.kgeorgiew.carddb.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * @author kgeorgiew
 */
@Component
@RequiredArgsConstructor
public class MessagesService {

    private final @NonNull MessageSource messageSource;

    public String getMessage(String translateKey, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(translateKey, args, locale);
    }

    public String getMessage(String translateKey) {
        return getMessage(translateKey, null);
    }

}
