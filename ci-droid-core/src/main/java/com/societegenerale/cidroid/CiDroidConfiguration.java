package com.societegenerale.cidroid;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.societegenerale.cidroid.extensions.actionToReplicate.OverwriteStaticFileAction;
import com.societegenerale.cidroid.extensions.actionToReplicate.ReplaceMavenProfileAction;
import com.societegenerale.cidroid.extensions.actionToReplicate.SimpleReplaceAction;
import com.societegenerale.cidroid.extensions.actionToReplicate.TemplateBasedContentAction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
public class CiDroidConfiguration {

    @Bean
    public MappingJackson2HttpMessageConverter customJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

        jsonConverter.setObjectMapper(objectMapper);
        return jsonConverter;
    }

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