package com.societegenerale.cidroid.model.github;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.societegenerale.cidroid.TestUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class GitHubPushEventTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void fieldsAreDeserialized() throws IOException {

        String pushEventAsString = TestUtils.readFromInputStream(getClass().getResourceAsStream("/pushEvent.json"));

        GitHubPushEvent pushEvent = objectMapper.readValue(pushEventAsString, GitHubPushEvent.class);

        assertThat(pushEvent).isNotNull();

        assertThat(pushEvent.getRepository()).isNotNull();
    }

}