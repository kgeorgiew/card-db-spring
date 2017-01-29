package de.kgeorgiew.carddb.service;

import de.kgeorgiew.carddb.domain.Lang;
import org.springframework.data.repository.Repository;

/**
 * @author kgeorgiew
 */
public interface LangRepository extends Repository<Lang, String> {

    Lang create(Lang lang);
}
