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

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.core.AllOf.allOf;
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

    private Class<LangController> controller;
    private Lang prePersistlang;

    @Before
    public void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);

        controller = LangController.class;
        prePersistlang = new Lang("DEU", null, null);
    }

    @Test
    public void shouldCreateValidLinkForUpdate() {
        Link actual = linkTo(methodOn(controller).update(prePersistlang.getLang(), prePersistlang)).withRel("update");
        Link expected = linkTo(methodOn(controller).get(prePersistlang.getLang())).withSelfRel();

        assertThat(expected.getHref(), equalTo(actual.getHref()));
    }

    @Test
    public void shouldHaveSelfUpdateDeleteLink() {
        List<Link> actual = new LangResourceAssembler().toResource(prePersistlang).getLinks();

        List<Link> expected = Arrays.asList(
                linkTo(methodOn(controller).get(prePersistlang.getLang())).withSelfRel(),
                linkTo(methodOn(controller).delete(prePersistlang.getLang())).withRel("delete"),
                linkTo(methodOn(controller).update(prePersistlang.getLang(), prePersistlang)).withRel("update")
        );

        assertThat("Resource should have self, delete and update links.", actual, equalTo(expected));
    }

}
