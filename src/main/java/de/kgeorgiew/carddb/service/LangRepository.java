package de.kgeorgiew.carddb.service;

import de.kgeorgiew.carddb.domain.Lang;

import java.util.Optional;

/**
 * @author kgeorgiew
 */
public interface LangRepository {

    Lang create(Lang lang);

    Optional<Lang> get(String id);

    void delete(String id);

    Lang update(Lang lang);
}
