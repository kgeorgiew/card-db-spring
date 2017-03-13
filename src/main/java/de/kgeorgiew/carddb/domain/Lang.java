package de.kgeorgiew.carddb.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.hateoas.core.Relation;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.lang.annotation.Repeatable;
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
@Builder
public class Lang {

    /**
     * Language code in ISO 639-3
     */
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
