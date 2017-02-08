package de.kgeorgiew.carddb.service;

import de.kgeorgiew.carddb.domain.Lang;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author kgeorgiew
 */
@Service
public class JooqLangRepository implements LangRepository {

    @Override
    public Lang create(Lang lang) {
        return null;
    }

    @Override
    public Optional<Lang> get(String id) {
        return null;
    }

}
