package com.societegenerale.cidroid;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.societegenerale.cidroid.api.ResourceToUpdate;
import com.societegenerale.cidroid.api.actionToReplicate.ActionToReplicate;
import com.societegenerale.cidroid.api.actionToReplicate.fields.TextArea;
import com.societegenerale.cidroid.api.gitHubInteractions.DirectPushGitHubInteraction;
import com.societegenerale.cidroid.extensions.actionToReplicate.SimpleReplaceAction;
import com.societegenerale.cidroid.model.BulkUpdateCommand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import static com.societegenerale.cidroid.TestUtils.readFromInputStream;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(CiDroidActionsController.class)
public class CiDroidActionsControllerTest {

    private static final String SOME_USER_NAME = "someUserName";

    private static final String SOME_OAUTH_TOKEN = "abcdef123456";

    private static final String SOME_EMAIL = "someEmail@someDomain.com";

    private static final String COMMIT_MESSAGE= "some commit message";

    @Autowired
    private MockMvc mvc;

    @Autowired
    CiDroidActionsController ciDroidActionsController;

    @MockBean(name = "actions-to-perform")
    private MessageChannel ciDroidActionsToPerformChannel;

    @MockBean(name = "mockAvailableAction1")
    private ActionToReplicate mockAvailableAction1;

    @MockBean(name = "mockAvailableAction2")
    private ActionToReplicate mockAvailableAction2;

    private BulkUpdateCommand.BulkUpdateCommandBuilder baseBatchUpdateCommandBuilder=BulkUpdateCommand.builder().gitLogin(SOME_USER_NAME)
                                                                                        .gitHubOauthToken(SOME_OAUTH_TOKEN)
                                                                                        .email(SOME_EMAIL);

    private ObjectMapper objectMapper=new ObjectMapper();

    private ResourceToUpdate resourceToUpdateOnMaster=new ResourceToUpdate("someRepo","jenkinsFile","master",null);
    private ResourceToUpdate resourceToUpdateOnSomeBranch=new ResourceToUpdate("someRepo","jenkinsFile","someBranch",null);

    List<ResourceToUpdate> resourcesToUpdate= Arrays.asList(resourceToUpdateOnMaster, resourceToUpdateOnSomeBranch);

    ActionToReplicate replaceAction=new SimpleReplaceAction("from","to");

    @Captor
    ArgumentCaptor<Message> sentMessage;

    @Test
    public void shouldSplitResourcesIntoIndividualMessages() throws Exception {

        BulkUpdateCommand bulkUpdateCommand =baseBatchUpdateCommandBuilder.gitHubInteractionType(new DirectPushGitHubInteraction())
                .commitMessage(COMMIT_MESSAGE)
                .resourcesToUpdate(resourcesToUpdate)
                .updateAction(replaceAction)
                .build();

        mvc.perform(post("/cidroid-actions/bulkUpdates")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bulkUpdateCommand)))
                .andExpect(status().is2xxSuccessful());

        verify(ciDroidActionsToPerformChannel, times(2)).send(sentMessage.capture());

        List<Message> actualMessages=sentMessage.getAllValues();

        assertThat(actualMessages).hasSize(2);

        List<BulkUpdateCommand> actualBulkUpdateCommands =actualMessages.stream().map( m -> (BulkUpdateCommand)m.getPayload()).collect(toList());

        List<ResourceToUpdate> actualSentResourcesToUpdate=new ArrayList<>();

        for(BulkUpdateCommand actualBulkUpdateCommand : actualBulkUpdateCommands){

            assertThat(actualBulkUpdateCommand.getGitLogin()).as("login" + getAssertionMessage(actualBulkUpdateCommand)).isEqualTo(SOME_USER_NAME);
            assertThat(actualBulkUpdateCommand.getGitHubOauthToken()).as("OAuth token" + getAssertionMessage(actualBulkUpdateCommand)).isEqualTo(SOME_OAUTH_TOKEN);
            assertThat(actualBulkUpdateCommand.getEmail()).as("email" + getAssertionMessage(actualBulkUpdateCommand)).isEqualTo(SOME_EMAIL);
            assertThat(actualBulkUpdateCommand.getGitHubInteractionType()).as("gitub interaction type" + getAssertionMessage(actualBulkUpdateCommand)).isEqualTo(new DirectPushGitHubInteraction());
            assertThat(actualBulkUpdateCommand.getUpdateAction()).as("updateAction" + getAssertionMessage(actualBulkUpdateCommand)).isEqualTo(replaceAction);
            assertThat(actualBulkUpdateCommand.getCommitMessage()).as("commitMessage" + getAssertionMessage(actualBulkUpdateCommand)).isEqualTo(COMMIT_MESSAGE);


            assertThat(actualBulkUpdateCommand.getResourcesToUpdate()).as("there should be exactly one resource to update when message gets sent").hasSize(1);

            actualSentResourcesToUpdate.add(actualBulkUpdateCommand.getResourcesToUpdate().get(0));
        }

        assertThat(actualSentResourcesToUpdate).containsExactlyInAnyOrder(resourceToUpdateOnMaster,resourceToUpdateOnSomeBranch);

    }

    @Test
    public void shouldReturnAllExistingActionsAsAvailableActionsForUI() throws Exception {

        when(mockAvailableAction1.getExpectedUIFields()).thenReturn(Arrays.asList(new TextArea("staticContent1", "content1 to overwrite/create")));
        when(mockAvailableAction2.getExpectedUIFields()).thenReturn(Arrays.asList(new TextArea("staticContent2", "content2 to overwrite/create")));

        ciDroidActionsController.init();

        MvcResult result=mvc.perform(get("/cidroid-actions/availableActions"))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        String responseAsString=result.getResponse().getContentAsString();

        List actualActionsForUi=objectMapper.readValue(responseAsString, List.class);

        assertThat(actualActionsForUi).hasSize(2);

        //can't easily deserialize action for UI, so keeping it as Object, then Maps
        for(Object actualActionForUi : actualActionsForUi) {

            LinkedHashMap actualActionForUiAsMap = (LinkedHashMap) actualActionForUi;
            assertThat(actualActionForUiAsMap.keySet()).containsExactlyInAnyOrder("label", "actionClassToSend", "expectedFields");

            List actualExpectedFields = (ArrayList) actualActionForUiAsMap.get("expectedFields");
            LinkedHashMap firstActualExpectedFields = (LinkedHashMap) actualExpectedFields.get(0);
            assertThat(firstActualExpectedFields.keySet()).containsAnyOf("label", "name", "fieldType");
        }
    }

    @Test
    public void shouldReturnBadRequestIfGitLoginIsNotProvided() throws Exception {

        BulkUpdateCommand invalidBulkUpdateCommand_noLogin =BulkUpdateCommand.builder()
                .gitHubOauthToken(SOME_OAUTH_TOKEN)
                .email(SOME_EMAIL)
                .build();


        postAndAssert4xxClientError(invalidBulkUpdateCommand_noLogin);
    }

    @Test
    public void shouldReturnBadRequestIfGitPasswordIsNotProvided() throws Exception {

        BulkUpdateCommand invalidBulkUpdateCommand_noPassword =BulkUpdateCommand.builder()
                .gitLogin(SOME_USER_NAME)
                .email(SOME_EMAIL)
                .build();

        postAndAssert4xxClientError(invalidBulkUpdateCommand_noPassword);
    }

    @Test
    public void shouldReturnBadRequestIfEmailDoesntHaveProperFormat() throws Exception {

        BulkUpdateCommand invalidBulkUpdateCommand_incorrectEmailFormat =BulkUpdateCommand.builder()
                .gitLogin(SOME_USER_NAME)
                .gitHubOauthToken(SOME_OAUTH_TOKEN)
                .email("some.incorrectEmail.com")
                .build();

        postAndAssert4xxClientError(invalidBulkUpdateCommand_incorrectEmailFormat);

    }

    @Test
    public void shouldReturnBadRequestIfGitHubInteractionIsInvalid() throws Exception {

        BulkUpdateCommand bulkUpdateCommand_invalidgitHubInteraction =baseBatchUpdateCommandBuilder
                //gitHubInteraction is left null
                .resourcesToUpdate(resourcesToUpdate)
                .updateAction(replaceAction)
                .build();

        postAndAssert4xxClientError(bulkUpdateCommand_invalidgitHubInteraction);

    }

    @Test
    public void shouldReturnBadRequestIfUpdateActionIsInvalid() throws Exception {

        BulkUpdateCommand bulkUpdateCommand_invalidUpdateAction =baseBatchUpdateCommandBuilder.gitHubInteractionType(new DirectPushGitHubInteraction())
                .resourcesToUpdate(resourcesToUpdate)
                //updateAction is left null
                .build();

        postAndAssert4xxClientError(bulkUpdateCommand_invalidUpdateAction);

    }

    @Test
    public void shouldReturnBadRequestIfResourceToUpdateIsInvalid() throws Exception {

        BulkUpdateCommand bulkUpdateCommand_invalidResourceToUpdate =baseBatchUpdateCommandBuilder.gitHubInteractionType(new DirectPushGitHubInteraction())
                //resourceToUpdate is left null
                .updateAction(replaceAction)
                .build();

        postAndAssert4xxClientError(bulkUpdateCommand_invalidResourceToUpdate);


        bulkUpdateCommand_invalidResourceToUpdate =baseBatchUpdateCommandBuilder.gitHubInteractionType(new DirectPushGitHubInteraction())
                //resourceToUpdate is empty
                .resourcesToUpdate(new ArrayList<>())
                .updateAction(replaceAction)
                .build();

        postAndAssert4xxClientError(bulkUpdateCommand_invalidResourceToUpdate);

    }

    @Test
    public void shouldReturnBadRequestIfGitHubInteractionTypeIsInvalid() throws Exception {
        String directPushBulkUpdateCommandAsString = readFromInputStream(getClass().getResourceAsStream("/bulkUpdate_pullRequest_noBranch_Command.json"));

        BulkUpdateCommand bulkUpdateCommand_invalidInteraction=objectMapper.readValue(directPushBulkUpdateCommandAsString,BulkUpdateCommand.class);

        postAndAssert4xxClientError(bulkUpdateCommand_invalidInteraction);

    }



    private void postAndAssert4xxClientError(BulkUpdateCommand invalidBulkUpdateCommand) throws Exception {

        mvc.perform(post("/cidroid-actions/bulkUpdates")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidBulkUpdateCommand)))
                .andExpect(status().is4xxClientError());

        verify(ciDroidActionsToPerformChannel, never()).send(any(Message.class));

    }

    private String getAssertionMessage(BulkUpdateCommand actualBulkUpdateCommand) {
        return " doesn't match for " +BulkUpdateCommand.class + " " + actualBulkUpdateCommand;
    }


}