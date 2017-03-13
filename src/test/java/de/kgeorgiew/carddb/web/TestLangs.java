package de.kgeorgiew.carddb.web;

import de.kgeorgiew.carddb.domain.Lang;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @author kgeorgiew
 */
public class TestLangs {

    public static Lang withoutCreatedUpdated(String lang) {
        return Lang.builder().lang(lang).build();
    }

    public static Lang withCreated(String lang) {
        return Lang.builder()
                .lang(lang)
                .created(ZonedDateTime.now())
                .createdBy("creator")
                .build();
    }

    public static Lang withUpdated(String lang) {
        return Lang.builder()
                .lang(lang)
                .created(ZonedDateTime.now())
                .createdBy("creator")
                .updated(ZonedDateTime.now().plusHours(10))
                .updatedBy("updater")
                .build();
    }

    public static List<Lang> random(int size) {
        return Arrays.stream(Locale.getAvailableLocales())
                .limit(size)
                .map(locale -> withUpdated(locale.getISO3Country()))
                .collect(Collectors.toList());
    }

    public static List<Lang> randomUnorderd(int size) {
        return null;
    }

}
