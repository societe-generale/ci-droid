package com.societegenerale.cidroid.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.societegenerale.cidroid.model.PullRequestEvent;
import com.societegenerale.cidroid.model.PushEvent;
import com.societegenerale.cidroid.model.SourceControlEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

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

     ResponseEntity<?> processPullRequestEvent(PullRequestEvent pullRequestEvent) {

         if (shouldNotProcess(pullRequestEvent)) {
             return ResponseEntity.accepted().build();
         }

         Message rawPullRequestEventMessage = MessageBuilder.withPayload(pullRequestEvent.getRawMessage().getBody()).build();

         log.info("sending to consumers : MergeRequestEvent for PR #{} on repo {}", pullRequestEvent.getPrNumber(), pullRequestEvent.getRepository().getName());

         pullRequestEventChannel.send(rawPullRequestEventMessage);

         return ResponseEntity.accepted().build();

     }

    protected ResponseEntity<?> processPushEvent(PushEvent pushEvent) {
        if (shouldNotProcess(pushEvent)) {
            return ResponseEntity.accepted().build();
        }

        String repoDefaultBranch = pushEvent.getRepository().getDefaultBranch();
        String eventRef = pushEvent.getRef();

        Message rawPushEventMessage = MessageBuilder.withPayload(pushEvent.getRawMessage().getBody()).build();

        if (eventRef.endsWith(repoDefaultBranch)) {
            log.info("sending to consumers : Pushevent on default branch {} on repo {}", repoDefaultBranch, pushEvent.getRepository().getFullName());

            pushOnDefaultBranchChannel.send(rawPushEventMessage);
        }
        else if (processNonDefaultBranchEvents) {
            log.info("sending to consumers : Pushevent on NON default branch {} on repo {}", repoDefaultBranch, pushEvent.getRepository().getName());

            pushOnNonDefaultBranchChannel.send(rawPushEventMessage);
        }
        else{
            log.info("Not sending pushevent on NON default branch {} on repo {}", repoDefaultBranch, pushEvent.getRepository().getFullName());
        }

        return ResponseEntity.accepted().build();
    }

    <T> T mapTo(Class<T> targetClass, HttpEntity<String> rawEvent) {

        try {
            T sourceControlEvent = objectMapper.readValue(rawEvent.getBody(), targetClass);

            return sourceControlEvent;
        } catch (IOException e) {
            log.warn("unable to read the incoming {} {}", targetClass.getName(), rawEvent, e);
        }

        return null;
    }

    boolean shouldNotProcess(SourceControlEvent sourceControlEvent) {

        if (sourceControlEvent == null) {
            return true;
        }

        if (!repositoriesToExclude.isEmpty() && repositoriesToExclude.contains(sourceControlEvent.getRepository().getName())) {
            log.debug("received an event for repo {}, which is configured as excluded -> not forwarding it to any channel", sourceControlEvent.getRepository().getName());
            return true;
        }

        if (!repositoriesToInclude.isEmpty() && !repositoriesToInclude.contains(sourceControlEvent.getRepository().getName())) {
            log.debug("received an event for repo {}, which is not configured as included -> not forwarding it to any channel", sourceControlEvent.getRepository().getName());
            return true;
        }

        return false;
    }



}
