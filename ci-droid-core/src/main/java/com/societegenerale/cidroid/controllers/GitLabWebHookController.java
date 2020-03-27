package com.societegenerale.cidroid.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.societegenerale.cidroid.CiDroidProperties;
import com.societegenerale.cidroid.model.SourceControlEvent;
import com.societegenerale.cidroid.model.gitlab.GitLabMergeRequestHookEvent;
import com.societegenerale.cidroid.model.gitlab.GitLabPushEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
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
@Profile("!gitHub")
public class GitLabWebHookController {

    private MessageChannel pushOnDefaultBranchChannel;

    private MessageChannel pushOnNonDefaultBranchChannel;

    private MessageChannel pullRequestEventChannel;

    private List<String> repositoriesToExclude;

    private List<String> repositoriesToInclude;

    private ObjectMapper objectMapper = new ObjectMapper();

    private boolean processNonDefaultBranchEvents;

    public GitLabWebHookController(@Qualifier("push-on-default-branch") MessageChannel pushOnDefaultBranchChannel,
                                   @Qualifier("merge-request-event") MessageChannel pullRequestEventChannel,
                                   @Qualifier("push-on-non-default-branch") MessageChannel pushOnNonDefaultBranchChannel,
                                   CiDroidProperties properties) {
        this.pushOnDefaultBranchChannel = pushOnDefaultBranchChannel;
        this.pushOnNonDefaultBranchChannel = pushOnNonDefaultBranchChannel;
        this.pullRequestEventChannel = pullRequestEventChannel;

        repositoriesToExclude = properties.getExcluded();
        repositoriesToInclude = properties.getIncluded();

        processNonDefaultBranchEvents = properties.isProcessNonDefaultBranchEvents();
    }


    @PostMapping(headers = "X-Gitlab-Event=ping")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void onGitHubPingEvent() {
    }


    @PostMapping(headers = "X-Gitlab-Event=Push Hook")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> onGitHubPushEvent(HttpEntity<String> rawPushEvent) {

        //Mapping the event manually, because we need to forward the original message at the end
        GitLabPushEvent pushEvent = mapTo(GitLabPushEvent.class, rawPushEvent);

        if (shouldNotProcess(pushEvent)) {
            return ResponseEntity.accepted().build();
        }

        String repoDefaultBranch = pushEvent.getProject().getDefaultBranch();
        String eventRef = pushEvent.getRef();

        Message rawPushEventMessage = MessageBuilder.withPayload(rawPushEvent.getBody()).build();

        if (eventRef.endsWith(repoDefaultBranch)) {
            log.info("sending to consumers : Pushevent on default branch {} on repo {}", repoDefaultBranch, pushEvent.getRepository().getName());

            pushOnDefaultBranchChannel.send(rawPushEventMessage);
        } else if (processNonDefaultBranchEvents) {
            log.info("sending to consumers : Pushevent on NON default branch {} on repo {}", repoDefaultBranch, pushEvent.getRepository().getName());

            pushOnNonDefaultBranchChannel.send(rawPushEventMessage);
        }
        else{
            log.info("Not sending pushevent on NON default branch {} on repo {}", repoDefaultBranch, pushEvent.getRepository().getName());
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

    private boolean shouldNotProcess(SourceControlEvent gitLabEvent) {

        if (gitLabEvent == null) {
            return true;
        }

        if (!repositoriesToExclude.isEmpty() && repositoriesToExclude.contains(gitLabEvent.getRepository().getName())) {
            log.debug("received an event for repo {}, which is configured as excluded -> not forwarding it to any channel", gitLabEvent.getRepository().getName());
            return true;
        }

        if (!repositoriesToInclude.isEmpty() && !repositoriesToInclude.contains(gitLabEvent.getRepository().getName())) {
            log.debug("received an event for repo {}, which is not configured as included -> not forwarding it to any channel", gitLabEvent.getRepository().getName());
            return true;
        }
        else {
            return false;
        }
    }

    @PostMapping(headers = "X-Gitlab-Event=Merge Request Hook")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> onGitHubPullRequestEvent(HttpEntity<String> rawPullRequestEvent){

        //Mapping the event manually, because we need to forward the original message at the end
        GitLabMergeRequestHookEvent gitLabMergeRequestHookEvent = mapTo(GitLabMergeRequestHookEvent.class, rawPullRequestEvent);

        if (shouldNotProcess(gitLabMergeRequestHookEvent)) {
            return ResponseEntity.accepted().build();
        }

        Message rawPullRequestEventMessage = MessageBuilder.withPayload(rawPullRequestEvent.getBody()).build();

        log.info("sending to consumers : MergeRequestEvent for PR #{} on repo {}", gitLabMergeRequestHookEvent.getObjectKind(), gitLabMergeRequestHookEvent.getRepository().getName());

        pullRequestEventChannel.send(rawPullRequestEventMessage);

        return ResponseEntity.accepted().build();
    }

}
