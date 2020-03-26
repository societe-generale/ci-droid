package com.societegenerale.cidroid.model.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PushEvent implements GitHubEvent{

    private String ref;

    private Repository repository;

}