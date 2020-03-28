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
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping(value = "/cidroid-webhook")
@RestController
public class GitHubWebHookController extends AbstractSourceControlWebHookController{

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
    public ResponseEntity<?> onPushEvent(HttpEntity<String> rawPushEvent) {

        //Mapping the event manually, because we need to forward the original message at the end
        PushEvent pushEvent = mapTo(GitHubPushEvent.class, rawPushEvent);

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



    @PostMapping(headers = "X-Github-Event=pull_request")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> onPullRequestEvent(HttpEntity<String> rawPullRequestEvent) {

        //Mapping the event manually, because we need to forward the original message at the end
        PullRequestEvent pullRequestEvent = mapTo(GitHubPullRequestEvent.class, rawPullRequestEvent);
        pullRequestEvent.setRawMessage(rawPullRequestEvent);

        return processPullRequestEvent(pullRequestEvent);
    }

}