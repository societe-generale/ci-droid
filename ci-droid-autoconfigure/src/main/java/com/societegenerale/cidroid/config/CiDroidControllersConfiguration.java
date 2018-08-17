package com.societegenerale.cidroid.config;

import com.societegenerale.cidroid.CiDroidProperties;
import com.societegenerale.cidroid.api.actionToReplicate.ActionToReplicate;
import com.societegenerale.cidroid.controllers.CiDroidActionsController;
import com.societegenerale.cidroid.controllers.WebHookController;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageChannel;

import java.util.List;

@Configuration
public class CiDroidControllersConfiguration {

    @Bean
    public WebHookController webHookController(@Qualifier("push-on-default-branch") MessageChannel pushOnDefaultBranchChannel,
                                               @Qualifier("pull-request-event") MessageChannel pullRequestEventChannel,
                                               CiDroidProperties properties) {


        return new WebHookController(pushOnDefaultBranchChannel,pullRequestEventChannel,properties);

    }

    @Bean
    public CiDroidActionsController ciDroidActionsController(@Qualifier("actions-to-perform") MessageChannel ciDroidActionsChannel,
                                                             List<ActionToReplicate> availableActions) {


        return new CiDroidActionsController(ciDroidActionsChannel,availableActions);
    }

}
