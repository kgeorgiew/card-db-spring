package de.kgeorgiew.carddb.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

/**
 * @author kgeorgiew
 *
 * Represents the language of a entity
 *
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"lang"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Lang {

    @NotNull
    @Size(min=3, max = 3, message = "{Size.Min.Lang.lang}")
    private String lang;

    private String createdBy;
    private ZonedDateTime created;

    private String updatedBy;
    private ZonedDateTime updated;


    public Lang(String lang, String createdBy, ZonedDateTime created) {
        this(lang, createdBy, created, null, null);
    }

}
