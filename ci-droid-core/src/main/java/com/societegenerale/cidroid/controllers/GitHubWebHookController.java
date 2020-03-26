package com.societegenerale.cidroid.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.societegenerale.cidroid.CiDroidProperties;
import com.societegenerale.cidroid.model.SourceControlEvent;
import com.societegenerale.cidroid.model.github.PullRequestEvent;
import com.societegenerale.cidroid.model.github.PushEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequestMapping(value = "/cidroid-webhook")
@RestController
public class GitHubWebHookController {

    private MessageChannel pushOnDefaultBranchChannel;

    private MessageChannel pullRequestEventChannel;

    private List<String> repositoriesToExclude;

    private List<String> repositoriesToInclude;

    private ObjectMapper objectMapper = new ObjectMapper();

    public GitHubWebHookController(@Qualifier("push-on-default-branch") MessageChannel pushOnDefaultBranchChannel,
                                   @Qualifier("pull-request-event") MessageChannel pullRequestEventChannel,
                                   CiDroidProperties properties) {
        this.pushOnDefaultBranchChannel = pushOnDefaultBranchChannel;
        this.pullRequestEventChannel = pullRequestEventChannel;


        repositoriesToExclude = properties.getExcluded();
        repositoriesToInclude = properties.getIncluded();
    }

    @PostMapping(headers = "X-Github-Event=ping")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void onGitHubPingEvent() {
    }


    @PostMapping(headers = "X-Github-Event=push")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> onGitHubPushEvent(HttpEntity<String> rawPushEvent) {

        //Mapping the event manually, because we need to forward the original message at the end
        PushEvent pushEvent = mapTo(PushEvent.class, rawPushEvent);

        if (shouldNotProcess(pushEvent)) {
            return ResponseEntity.accepted().build();
        }

        String repoDefaultBranch = pushEvent.getRepository().getDefaultBranch();
        String eventRef = pushEvent.getRef();

        Message rawPushEventMessage = MessageBuilder.withPayload(rawPushEvent.getBody()).build();

        Boolean endsWith = eventRef.endsWith(repoDefaultBranch);
        if (eventRef.endsWith(repoDefaultBranch)) {
            log.info("sending to consumers : Pushevent on default branch {} on repo {}", repoDefaultBranch, pushEvent.getRepository().getFullName());

            pushOnDefaultBranchChannel.send(rawPushEventMessage);
        } else {
            log.info("Not sending pushevent on NON default branch {} on repo {}", repoDefaultBranch, pushEvent.getRepository().getFullName());
        }

        return ResponseEntity.accepted().build();
    }

    private <T> T mapTo(Class<T> targetClass, HttpEntity<String> rawEvent) {

        try {
            return objectMapper.readValue(rawEvent.getBody(), targetClass);
        } catch (IOException e) {
            log.warn("unable to read the incoming {} {}", targetClass.getName(), rawEvent, e);
        }

        return null;
    }

    @PostMapping(headers = "X-Github-Event=pull_request")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> onGitHubPullRequestEvent(HttpEntity<String> rawPullRequestEvent) {

        //Mapping the event manually, because we need to forward the original message at the end
        PullRequestEvent pullRequestEvent = mapTo(PullRequestEvent.class, rawPullRequestEvent);

        if (shouldNotProcess(pullRequestEvent)) {
            return ResponseEntity.accepted().build();
        }

        Message rawPullRequestEventMessage = MessageBuilder.withPayload(rawPullRequestEvent.getBody()).build();

        log.info("sending to consumers : PullRequestEvent for PR #{} on repo {}", pullRequestEvent.getPrNumber(), pullRequestEvent.getRepository().getFullName());

        pullRequestEventChannel.send(rawPullRequestEventMessage);

        return ResponseEntity.accepted().build();
    }

    private boolean shouldNotProcess(SourceControlEvent gitHubEvent) {

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