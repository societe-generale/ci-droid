package com.societegenerale.cidroid.controllers;

import com.societegenerale.cidroid.AdditionalTestConfig;
import com.societegenerale.cidroid.CiDroidProperties;
import com.societegenerale.cidroid.TestUtils;
import com.societegenerale.cidroid.controllers.WebHookController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(WebHookController.class)
@Import(AdditionalTestConfig.class)
public class WebHookControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean(name = "push-on-default-branch")
    MessageChannel pushOnDefaultBranchOutputChannel;

    @MockBean(name = "pull-request-event")
    MessageChannel pullRequestEventChannel;

    @Autowired
    CiDroidProperties ciDroidProperties;

    @Captor
    ArgumentCaptor<Message> sentMessage;

    String pushEventAsString;

    @Before
    public void loadEventAndConfigureMock() throws IOException {
        pushEventAsString = TestUtils.readFromInputStream(getClass().getResourceAsStream("/pushEvent.json"));

        ciDroidProperties.getExcluded().clear();
        ciDroidProperties.getIncluded().clear();
    }

    @Test
    public void forwardEventToDefaultBranchOutputChannel_whenPushOnDefaultBranch() throws Exception {

        performPOSTandExpectSuccess(pushEventAsString);

        verify(pushOnDefaultBranchOutputChannel, times(1)).send(sentMessage.capture());

        assertThat(sentMessage.getValue().getPayload()).isEqualTo(pushEventAsString);

        verify(pullRequestEventChannel, never()).send(any(Message.class));
    }

    @Test
    public void dontForwardAnything_ifPushEventNotOnDefaultBranch() throws Exception {

        String pushEventOnBranchAsString = pushEventAsString.replaceAll("refs/heads/master", "refs/heads/someOtherBranch");

        performPOSTandExpectSuccess(pushEventOnBranchAsString);

        verify(pushOnDefaultBranchOutputChannel, never()).send(any(Message.class));
        verify(pullRequestEventChannel, never()).send(any(Message.class));
    }

    @Test
    public void dontForwardAnything_ifRepoIsExcluded() throws Exception {

        ciDroidProperties.getExcluded().add("public-repo");

        String pushEventOnBranchAsString = pushEventAsString.replaceAll("refs/heads/master", "refs/heads/someOtherBranch");
        performPOSTandExpectSuccess(pushEventOnBranchAsString);

        performPOSTandExpectSuccess(pushEventAsString);

        verify(pushOnDefaultBranchOutputChannel, never()).send(any(Message.class));
        verify(pullRequestEventChannel, never()).send(any(Message.class));

    }

    @Test
    public void dontForwardAnythingIfRepoNotIncluded() throws Exception {

        ciDroidProperties.getIncluded().add("someIncludedRepo");

        performPOSTandExpectSuccess(pushEventAsString);

        verify(pushOnDefaultBranchOutputChannel, never()).send(any(Message.class));
        verify(pullRequestEventChannel, never()).send(any(Message.class));

    }

    @Test
    public void forwardEventTo_pullRequestEventChannel_whenPullRequestEvent() throws Exception {

        String pullRequestEventAsString = TestUtils.readFromInputStream(getClass().getResourceAsStream("/pullRequestEvent.json"));

        mvc.perform(post("/cidroid-webhook")
                .header("X-Github-Event", "pull_request")
                .contentType(APPLICATION_JSON)
                .content(pullRequestEventAsString))
                .andExpect(status().is2xxSuccessful());

        verify(pushOnDefaultBranchOutputChannel, never()).send(any(Message.class));
        verify(pullRequestEventChannel, times(1)).send(sentMessage.capture());

        assertThat(sentMessage.getValue().getPayload()).isEqualTo(pullRequestEventAsString);



    }

    private void performPOSTandExpectSuccess(String requestBodyContent) throws Exception {
        mvc.perform(post("/cidroid-webhook")
                .header("X-Github-Event", "push")
                .contentType(APPLICATION_JSON)
                .content(requestBodyContent))

                .andExpect(status().is2xxSuccessful());
    }

}