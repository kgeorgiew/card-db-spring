package de.kgeorgiew.carddb.service;

import de.kgeorgiew.carddb.domain.Lang;
import de.kgeorgiew.carddb.web.LangController;
import org.junit.Before;
import org.junit.Test;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 *
 * Check if all links are present and got the proper href
 *
 * @author kgeorgiew
 */
public class LangResourceAssemblerTest {

    @Before
    public void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);
    }

    @Test
    public void shouldHaveSelfUpdateDeleteLink() {
        Class<LangController> controller = LangController.class;

        Lang lang = new Lang("DEU", null, null);

        Link selfRel = linkTo(methodOn(controller).get(lang.getLang())).withSelfRel();
        Link deleteRel = linkTo(methodOn(controller).get(lang.getLang())).withRel("delete");
        Link updateRel = linkTo(methodOn(controller).get(lang.getLang())).withRel("update");

        Resource<Lang> resource = new LangResourceAssembler().toResource(lang);

        assertThat(resource, is(notNullValue()));
        assertThat(resource.getLinks().size(), equalTo(3));

        assertThat(resource.getLink("self"), equalTo(selfRel));
        assertThat(resource.getLink("update"), equalTo(updateRel));
        assertThat(resource.getLink("delete"), equalTo(deleteRel));
    }

}
