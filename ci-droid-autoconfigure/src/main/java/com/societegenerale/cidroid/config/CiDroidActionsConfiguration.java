package com.societegenerale.cidroid.config;

import com.societegenerale.cidroid.extensions.actionToReplicate.OverwriteStaticFileAction;
import com.societegenerale.cidroid.extensions.actionToReplicate.ReplaceMavenProfileAction;
import com.societegenerale.cidroid.extensions.actionToReplicate.SimpleReplaceAction;
import com.societegenerale.cidroid.extensions.actionToReplicate.TemplateBasedContentAction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CiDroidActionsConfiguration {

    @Bean
    public OverwriteStaticFileAction overwriteStaticFileAction() {

        return new OverwriteStaticFileAction();
    }

    @Bean
    public ReplaceMavenProfileAction replaceMavenProfileAction() {

        return new ReplaceMavenProfileAction();
    }

    @Bean
    public SimpleReplaceAction simpleReplaceAction() {

        return new SimpleReplaceAction();
    }

    @Bean
    public TemplateBasedContentAction templateBasedContentAction() {

        return new TemplateBasedContentAction();
    }

}
