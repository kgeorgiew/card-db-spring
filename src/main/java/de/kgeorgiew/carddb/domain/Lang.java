package de.kgeorgiew.carddb.domain;

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
public class Lang {

    @NotNull
    @Size(min=3, max = 3, message = "{Size.Min.Lang.lang}")
    private String lang;

    private String createdBy;
    private ZonedDateTime created;

}
