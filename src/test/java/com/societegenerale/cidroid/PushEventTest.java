package com.societegenerale.cidroid;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.societegenerale.cidroid.model.github.PushEvent;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class PushEventTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void fieldsAreDeserialized() throws IOException {

        String pushEventAsString= TestUtils.readFromInputStream(getClass().getResourceAsStream("/pushEvent.json"));

        PushEvent pushEvent=objectMapper.readValue(pushEventAsString,PushEvent.class);

        assertThat(pushEvent).isNotNull();

        assertThat(pushEvent.getRepository()).isNotNull();

    }


}