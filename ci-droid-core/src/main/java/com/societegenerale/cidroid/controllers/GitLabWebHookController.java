package com.societegenerale.cidroid.controllers;

import com.societegenerale.cidroid.CiDroidProperties;
import com.societegenerale.cidroid.model.PullRequestEvent;
import com.societegenerale.cidroid.model.PushEvent;
import com.societegenerale.cidroid.model.gitlab.GitLabMergeRequestEvent;
import com.societegenerale.cidroid.model.gitlab.GitLabPushEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping(value = "/cidroid-webhook")
@RestController
@Profile("!gitHub")
public class GitLabWebHookController extends AbstractSourceControlWebHookController{

    public GitLabWebHookController(@Qualifier("push-on-default-branch") MessageChannel pushOnDefaultBranchChannel,
                                   @Qualifier("pull-request-event") MessageChannel pullRequestEventChannel,
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
    public ResponseEntity<?> onPushEvent(HttpEntity<String> rawPushEvent) {

        //Mapping the event manually, because we need to forward the original message at the end
        PushEvent pushEvent = mapTo(GitLabPushEvent.class, rawPushEvent);
        pushEvent.setRawMessage(rawPushEvent);

        return processPushEvent(pushEvent);
    }

    @PostMapping(headers = "X-Gitlab-Event=Merge Request Hook")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> onPullRequestEvent(HttpEntity<String> rawPullRequestEvent){

        //Mapping the event manually, because we need to forward the original message at the end
        PullRequestEvent gitLabMergeRequestHookEvent = mapTo(GitLabMergeRequestEvent.class, rawPullRequestEvent);
        gitLabMergeRequestHookEvent.setRawMessage(rawPullRequestEvent);

        return processPullRequestEvent(gitLabMergeRequestHookEvent);

    }


}
