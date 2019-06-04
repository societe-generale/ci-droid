package com.societegenerale.cidroid;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.societegenerale.cidroid.model.github.PushEvent;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class PushEventTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void fieldsAreDeserialized() throws IOException {

        String pushEventAsString = TestUtils.readFromInputStream(getClass().getResourceAsStream("/pushEvent.json"));

        PushEvent pushEvent = objectMapper.readValue(pushEventAsString, PushEvent.class);

        assertThat(pushEvent).isNotNull();

        assertThat(pushEvent.getRepository()).isNotNull();

    }

}