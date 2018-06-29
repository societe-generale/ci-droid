package com.societegenerale.cidroid;

import org.junit.Test;

import java.util.Arrays;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class CiDroidPropertiesTest {

    @Test
    public void cantHaveBothIncludedAndExcludedRepos(){

        CiDroidProperties ciDroidProperties=new CiDroidProperties(Arrays.asList("repoToExclude"),Arrays.asList("repoToInclude"));

        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(
                () -> {ciDroidProperties.validate();}
        ).withMessage("You can't define both included AND excluded repositories - please configure only one type");

    }

    @Test
    public void canHaveIncludedReposOnly(){

        CiDroidProperties ciDroidProperties=new CiDroidProperties(emptyList(),Arrays.asList("repoToInclude"));

        assertThatCode(
                () -> {ciDroidProperties.validate();}
        ).doesNotThrowAnyException();

    }

    @Test
    public void canHaveExcludedReposOnly(){

        CiDroidProperties ciDroidProperties=new CiDroidProperties(Arrays.asList("repoToExclude"),emptyList());

        assertThatCode(
                () -> {ciDroidProperties.validate();}
        ).doesNotThrowAnyException();

    }


}