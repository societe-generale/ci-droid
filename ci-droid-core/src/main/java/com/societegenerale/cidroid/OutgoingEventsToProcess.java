package com.societegenerale.cidroid;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface OutgoingEventsToProcess {

    @Output("push-on-default-branch")
    MessageChannel pushOnDefaultBranch();

    @Output("push-on-non-default-branch")
    MessageChannel pushOnNonDefaultBranch();

    @Output("pull-request-event")
    MessageChannel pullRequestEvent();

    @Output("actions-to-perform")
    MessageChannel ciDroidActionToPerform();

}
