package de.kgeorgiew.carddb.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.hateoas.core.DefaultRelProvider;

/**
 * Overrides the behavior of {@link DefaultRelProvider} for collection resources
 * and sets the rel to {@link AppRelProvider#collectionRelName}.
 *
 * @author kgeorgiew
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AppRelProvider extends DefaultRelProvider {

    private final String collectionRelName;

    /**
     *  Sets the collection rel to the given parameter value
     *
     * @param collectionRelName, Relation type to be used to point to a collection resource
     */
    public AppRelProvider(final String collectionRelName) {
        this.collectionRelName = collectionRelName;
    }

    /**
     * (non-Javadoc)
     * @see org.springframework.hateoas.RelProvider#getCollectionResourceRelFor(java.lang.Class)
     */
    @Override
    public String getCollectionResourceRelFor(Class<?> type) {
        return collectionRelName;
    }

}
