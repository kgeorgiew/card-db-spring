package de.kgeorgiew.carddb.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * @author kgeorgiew
 *
 * Represents the language of a entity
 *
 */
public class Lang {

    @NotNull
    @Size(min=3, max = 3, message = "{Size.Min.Lang.lang}")
    private String lang;

    private String createdBy;
    private ZonedDateTime created;

    Lang() {

    }

    public Lang(String lang, String createdBy, ZonedDateTime created) {
        this.lang = lang;
        this.createdBy = createdBy;
        this.created = created;
    }

    public String getLang() {
        return lang;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lang lang1 = (Lang) o;
        return Objects.equals(lang, lang1.lang);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lang);
    }
}
