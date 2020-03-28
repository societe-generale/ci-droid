package com.societegenerale.cidroid.model.gitlab;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.societegenerale.cidroid.model.PushEvent;
import com.societegenerale.cidroid.model.Repository;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(converter = GitLabPushEvent.GitLabPushEventSanitizer.class)
public class GitLabPushEvent extends PushEvent {

    @JsonProperty("ref")
    private String ref;

    private Repository repository;

    private GitLabProject gitLabProject;


    class GitLabPushEventSanitizer extends StdConverter<GitLabPushEvent,GitLabPushEvent> {

        @Override
        public GitLabPushEvent convert(GitLabPushEvent gitLabPushEvent) {

            gitLabPushEvent.getRepository().setDefaultBranch(gitLabPushEvent.getGitLabProject().getDefaultBranch());

            return gitLabPushEvent;
        }

    }
}
