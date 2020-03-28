package com.societegenerale.cidroid.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Repository {

    private String url;

    private String name;

    @JsonProperty("default_branch")
    private String defaultBranch;

    @JsonProperty("full_name")
    private String fullName;

}