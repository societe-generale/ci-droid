package com.societegenerale.cidroid.controllers;

import com.societegenerale.cidroid.CiDroidProperties;
import com.societegenerale.cidroid.model.PullRequestEvent;
import com.societegenerale.cidroid.model.PushEvent;
import com.societegenerale.cidroid.model.github.GitHubPullRequestEvent;
import com.societegenerale.cidroid.model.github.GitHubPushEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping(value = "/cidroid-webhook")
@RestController
public class GitHubWebHookController extends AbstractSourceControlWebHookController{

    public GitHubWebHookController(@Qualifier("push-on-default-branch") MessageChannel pushOnDefaultBranchChannel,
                                   @Qualifier("pull-request-event") MessageChannel pullRequestEventChannel,
                                   @Qualifier("push-on-non-default-branch") MessageChannel pushOnNonDefaultBranchChannel,
                                   CiDroidProperties properties) {
        this.pushOnDefaultBranchChannel = pushOnDefaultBranchChannel;
        this.pullRequestEventChannel = pullRequestEventChannel;
        this.pushOnNonDefaultBranchChannel = pushOnNonDefaultBranchChannel;

        repositoriesToExclude = properties.getExcluded();
        repositoriesToInclude = properties.getIncluded();

        processNonDefaultBranchEvents = properties.isProcessNonDefaultBranchEvents();
    }

    @PostMapping(headers = "X-Github-Event=ping")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void onGitHubPingEvent() {
    }


    @PostMapping(headers = "X-Github-Event=push")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> onPushEvent(HttpEntity<String> rawPushEvent) {

        PushEvent pushEvent = mapToPushEvent(GitHubPushEvent.class, rawPushEvent);

        return processPushEvent(pushEvent);
    }

    @PostMapping(headers = "X-Github-Event=pull_request")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> onPullRequestEvent(HttpEntity<String> rawPullRequestEvent) {

        //Mapping the event manually, because we need to forward the original message at the end
        PullRequestEvent pullRequestEvent = mapToPullRequestEvent(GitHubPullRequestEvent.class, rawPullRequestEvent);

        return processPullRequestEvent(pullRequestEvent);
    }

}