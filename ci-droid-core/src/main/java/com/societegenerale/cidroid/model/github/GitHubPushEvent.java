package com.societegenerale.cidroid.model.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.societegenerale.cidroid.model.PushEvent;
import com.societegenerale.cidroid.model.Repository;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubPushEvent extends PushEvent {

    private String ref;

    private Repository repository;

}