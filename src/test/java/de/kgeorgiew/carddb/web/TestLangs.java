package de.kgeorgiew.carddb.web;

import de.kgeorgiew.carddb.domain.Lang;

/**
 * @author kgeorgiew
 */
public class TestLangs {

    public Lang newLang(String lang) {
        return Lang.builder().lang(lang).build();
    }
}
