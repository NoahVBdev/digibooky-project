package org.metrunui.digibooky.domain;

import java.util.Set;

public enum Role {
    MEMBER(Feature.LEND_BOOK, Feature.RETURN_BOOK),
    LIBRARIAN(Feature.REGISTER_BOOK, Feature.UPDATE_BOOK, Feature.DELETE_BOOK,
            Feature.VIEW_LENT_BOOKS, Feature.VIEW_OVERDUE_BOOKS),
    ADMIN(Feature.VIEW_ALL_MEMBERS, Feature.REGISTER_ADMIN, Feature.REGISTER_LIBRARIAN);

    private final Set<Feature> features;

    Role(Feature... features) {
        this.features = Set.of(features);
    }

    public boolean hasFeature(Feature feature) {
        return features.contains(feature);
    }
}
