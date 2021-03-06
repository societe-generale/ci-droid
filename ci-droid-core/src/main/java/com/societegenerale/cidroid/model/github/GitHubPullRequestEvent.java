package com.societegenerale.cidroid.model.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.societegenerale.cidroid.model.PullRequestEvent;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubPullRequestEvent extends PullRequestEvent {

    private String action;

    @JsonProperty("number")
    private int prNumber;

}
