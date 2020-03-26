package com.societegenerale.cidroid.model.gitlab;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitLabMergeRequestHookEvent implements GitLabEvent{

    @JsonProperty("object_kind")
    private String objectKind;

    private GitLabProject project;

    private GitLabRepository repository;

}
