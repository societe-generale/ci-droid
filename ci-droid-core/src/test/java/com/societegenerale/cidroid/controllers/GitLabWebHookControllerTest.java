package com.societegenerale.cidroid.controllers;


import com.societegenerale.cidroid.AdditionalTestConfig;
import com.societegenerale.cidroid.CiDroidProperties;
import com.societegenerale.cidroid.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;


import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(GitLabWebHookController.class)
@Import(AdditionalTestConfig.class)
@ActiveProfiles("gitLab")
public class GitLabWebHookControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean(name = "push-on-non-default-branch")
    private MessageChannel pushOnNonDefaultBranchOutputChannel;

    @MockBean(name = "push-on-default-branch")
    private MessageChannel pushOnDefaultBranchOutputChannel;

    @MockBean(name = "merge-request-event")
    private MessageChannel mergeRequestEventChannel;

    @Autowired
    private CiDroidProperties ciDroidProperties;

    @Captor
    private ArgumentCaptor<Message> sentMessage;

    private String pushEventAsString;

    @BeforeEach
    void loadEventAndConfigureMock() throws IOException {
        pushEventAsString = TestUtils.readFromInputStream(getClass().getResourceAsStream("/pushEventGitLab.json"));

        ciDroidProperties.getExcluded().clear();
        ciDroidProperties.getIncluded().clear();
    }

    @Test
    void forwardEventToNonDefaultBranchOutputChannel_whenPushOnNonDefaultBranch() throws Exception {

        String pushEventOnBranchAsString = pushEventAsString.replaceAll("refs/heads/master", "refs/heads/someOtherBranch");

        performPOSTandExpectSuccess(pushEventOnBranchAsString, "Push Hook");

        verify(pushOnNonDefaultBranchOutputChannel, times(1)).send(sentMessage.capture());

        assertThat(sentMessage.getValue().getPayload()).isEqualTo(pushEventOnBranchAsString);

        verify(mergeRequestEventChannel, never()).send(any(Message.class));
        verify(pushOnDefaultBranchOutputChannel, never()).send(any(Message.class));

    }

    @Test
    void forwardEventToDefaultBranchOutputChannel_whenPushOnDefaultBranch() throws Exception {

        performPOSTandExpectSuccess(pushEventAsString, "Push Hook");

        verify(pushOnDefaultBranchOutputChannel, times(1)).send(sentMessage.capture());

        assertThat(sentMessage.getValue().getPayload()).isEqualTo(pushEventAsString);

        verify(mergeRequestEventChannel, never()).send(any(Message.class));
    }


    @Test
    void dontForwardAnything_ifPushEventNotOnDefaultBranchAndProcessNonDefaultBranchesIsFalse() throws Exception {

        String pushEventOnBranchAsString = pushEventAsString.replaceAll("refs/heads/master", "refs/heads/someOtherBranch");

        performPOSTandExpectSuccess(pushEventOnBranchAsString, "Push Hook");

        //Has to be tested to, but how to set processNonDefaultBranch to false again?
        //verify(pushOnNonDefaultBranchOutputChannel, never()).send(any(Message.class));
        verify(pushOnDefaultBranchOutputChannel, never()).send(any(Message.class));
        verify(mergeRequestEventChannel, never()).send(any(Message.class));
    }

    @Test
    void dontForwardAnything_ifRepoIsExcluded() throws Exception {

        ciDroidProperties.getExcluded().add("Diaspora");

        String pushEventOnBranchAsString = pushEventAsString.replaceAll("refs/heads/master", "refs/heads/someOtherBranch");

        performPOSTandExpectSuccess(pushEventOnBranchAsString, "Push Hook");

        performPOSTandExpectSuccess(pushEventAsString, "Push Hook");

        verify(pushOnDefaultBranchOutputChannel, never()).send(any(Message.class));
        verify(mergeRequestEventChannel, never()).send(any(Message.class));

    }

    @Test
    void dontForwardAnythingIfRepoNotIncluded() throws Exception {

        ciDroidProperties.getIncluded().add("someIncludedRepo");

        performPOSTandExpectSuccess(pushEventAsString, "Push Hook");

        verify(pushOnDefaultBranchOutputChannel, never()).send(any(Message.class));
        verify(mergeRequestEventChannel, never()).send(any(Message.class));

    }

    @Test
    void forwardEventTo_pullRequestEventChannel_whenPullRequestEvent() throws Exception {

        String pullRequestEventAsString = TestUtils.readFromInputStream(getClass().getResourceAsStream("/mergeRequestHookGitLab.json"));

        performPOSTandExpectSuccess(pullRequestEventAsString, "Merge Request Hook");

        verify(pushOnDefaultBranchOutputChannel, never()).send(any(Message.class));
        verify(mergeRequestEventChannel, times(1)).send(sentMessage.capture());

        assertThat(sentMessage.getValue().getPayload()).isEqualTo(pullRequestEventAsString);



    }

    private void performPOSTandExpectSuccess(String requestBodyContent, String headerValues) throws Exception {
        mvc.perform(post("/cidroid-webhook")
                .header("X-Gitlab-Event", headerValues)
                .contentType(APPLICATION_JSON)
                .content(requestBodyContent))
                .andExpect(status().is2xxSuccessful());
    }
}
