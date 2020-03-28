package com.societegenerale.cidroid.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.societegenerale.cidroid.model.SourceControlEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageChannel;

import java.io.IOException;
import java.util.List;

@Slf4j
public abstract class AbstractSourceControlWebHookController {

    MessageChannel pushOnDefaultBranchChannel;

    MessageChannel pullRequestEventChannel;

    MessageChannel pushOnNonDefaultBranchChannel;

    List<String> repositoriesToExclude;

    List<String> repositoriesToInclude;

    boolean processNonDefaultBranchEvents;

    private ObjectMapper objectMapper = new ObjectMapper();

    public abstract ResponseEntity<?> onPushEvent(HttpEntity<String> rawPushEvent);

    public abstract ResponseEntity<?> onPullRequestEvent(HttpEntity<String> rawPullRequestEvent);


    <T> T mapTo(Class<T> targetClass, HttpEntity<String> rawEvent) {

        try {
            return objectMapper.readValue(rawEvent.getBody(), targetClass);
        } catch (IOException e) {
            log.warn("unable to read the incoming {} {}", targetClass.getName(), rawEvent, e);
        }

        return null;
    }

    boolean shouldNotProcess(SourceControlEvent gitHubEvent) {

        if (gitHubEvent == null) {
            return true;
        }

        if (!repositoriesToExclude.isEmpty() && repositoriesToExclude.contains(gitHubEvent.getRepository().getName())) {
            log.debug("received an event for repo {}, which is configured as excluded -> not forwarding it to any channel", gitHubEvent.getRepository().getName());
            return true;
        }

        if (!repositoriesToInclude.isEmpty() && !repositoriesToInclude.contains(gitHubEvent.getRepository().getName())) {
            log.debug("received an event for repo {}, which is not configured as included -> not forwarding it to any channel", gitHubEvent.getRepository().getName());
            return true;
        }

        return false;
    }
}
