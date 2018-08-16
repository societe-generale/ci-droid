package com.societegenerale.cidroid.config;

import com.societegenerale.cidroid.controllers.CiDroidActionsController;
import com.societegenerale.cidroid.CiDroidProperties;
import com.societegenerale.cidroid.controllers.WebHookController;
import com.societegenerale.cidroid.api.actionToReplicate.ActionToReplicate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageChannel;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;



@Configuration
@EnableSwagger2
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

    @Bean
    public Docket productApi() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .select().apis(RequestHandlerSelectors.basePackage("com.societegenerale.cidroid.controllers"))
                .paths(PathSelectors.any())
                .build();
        return docket;
    }

}
