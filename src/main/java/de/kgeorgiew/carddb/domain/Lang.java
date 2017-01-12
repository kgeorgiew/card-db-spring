package de.kgeorgiew.carddb.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * @author kgeorgiew
 */
public class Lang {

    @NotNull
    @Size(min=3, max = 3, message = "{Size.Min.Lang.lang}")
    private String lang;

    private String createdBy;
    private LocalDateTime created;

    Lang() {

    }

    public Lang(String lang, String createdBy, LocalDateTime created) {
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

    public LocalDateTime getCreated() {
        return created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Lang lang1 = (Lang) o;

        return lang != null ? lang.equals(lang1.lang) : lang1.lang == null;
    }

    @Override
    public int hashCode() {
        return lang != null ? lang.hashCode() : 0;
    }
}
