package com.societegenerale.cidroid.model.gitlab;

public interface GitLabEvent {
    GitLabProject getProject();

    GitLabRepository getRepository();
}
