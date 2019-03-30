package com.societegenerale.cidroid;

import org.junit.jupiter.api.Test;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class CiDroidPropertiesTest {

    @Test
    void cantHaveBothIncludedAndExcludedRepos() {

        CiDroidProperties ciDroidProperties = new CiDroidProperties(singletonList("repoToExclude"), singletonList("repoToInclude"));

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(ciDroidProperties::validate)
                .withMessage("You can't define both included AND excluded repositories - please configure only one type");
    }

    @Test
    void canHaveIncludedReposOnly() {

        CiDroidProperties ciDroidProperties = new CiDroidProperties(emptyList(), singletonList("repoToInclude"));

        assertThatCode(ciDroidProperties::validate)
                .doesNotThrowAnyException();

    }

    @Test
    void canHaveExcludedReposOnly() {

        CiDroidProperties ciDroidProperties = new CiDroidProperties(singletonList("repoToExclude"), emptyList());

        assertThatCode(ciDroidProperties::validate)
                .doesNotThrowAnyException();

    }

}