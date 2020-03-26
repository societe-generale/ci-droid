package com.societegenerale.cidroid.model.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.societegenerale.cidroid.model.Repository;
import com.societegenerale.cidroid.model.SourceControlEvent;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PushEvent implements SourceControlEvent {

    private String ref;

    private Repository repository;

}