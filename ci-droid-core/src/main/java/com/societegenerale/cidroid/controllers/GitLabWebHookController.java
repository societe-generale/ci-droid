package com.societegenerale.cidroid.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.societegenerale.cidroid.CiDroidProperties;
import com.societegenerale.cidroid.model.github.GitHubEvent;
import com.societegenerale.cidroid.model.github.PullRequestEvent;
import com.societegenerale.cidroid.model.github.PushEvent;
import com.societegenerale.cidroid.model.gitlab.GitLabEvent;
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

    private MessageChannel pullRequestEventChannel;

    private List<String> repositoriesToExclude;

    private List<String> repositoriesToInclude;

    private ObjectMapper objectMapper = new ObjectMapper();

    public GitLabWebHookController(@Qualifier("push-on-default-branch") MessageChannel pushOnDefaultBranchChannel,
                                   @Qualifier("pull-request-event") MessageChannel pullRequestEventChannel,
                                   CiDroidProperties properties) {
        this.pushOnDefaultBranchChannel = pushOnDefaultBranchChannel;
        this.pullRequestEventChannel = pullRequestEventChannel;


        repositoriesToExclude = properties.getExcluded();
        repositoriesToInclude = properties.getIncluded();
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
            log.info("sending to consumers : Pushevent on default branch {} on repo {}", repoDefaultBranch, pushEvent.getProject().getFullName());

            pushOnDefaultBranchChannel.send(rawPushEventMessage);
        } else {
            log.info("Not sending pushevent on NON default branch {} on repo {}", repoDefaultBranch, pushEvent.getProject().getFullName());
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

    private boolean shouldNotProcess(GitLabEvent gitLabEvent) {

        if (gitLabEvent == null) {
            return true;
        }

        if (!repositoriesToExclude.isEmpty() && repositoriesToExclude.contains(gitLabEvent.getProject().getName())) {
            log.debug("received an event for repo {}, which is configured as excluded -> not forwarding it to any channel", gitLabEvent.getProject().getName());
            return true;
        }

        if (!repositoriesToInclude.isEmpty() && !repositoriesToInclude.contains(gitLabEvent.getProject().getName())) {
            log.debug("received an event for repo {}, which is not configured as included -> not forwarding it to any channel", gitLabEvent.getProject().getName());
            return true;
        }

        return false;
    }

}
