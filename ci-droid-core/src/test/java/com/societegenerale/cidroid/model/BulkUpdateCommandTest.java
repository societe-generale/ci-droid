package com.societegenerale.cidroid.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.societegenerale.cidroid.TestUtils;
import com.societegenerale.cidroid.api.gitHubInteractions.DirectPushGitHubInteraction;
import com.societegenerale.cidroid.api.gitHubInteractions.PullRequestGitHubInteraction;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BulkUpdateCommandTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void canDeserializeDirectPush() throws IOException {

        String directPushBulkUpdateCommandAsString = TestUtils.readFromInputStream(getClass().getResourceAsStream("/bulkUpdate_directPush_Command.json"));

        BulkUpdateCommand command = objectMapper.readValue(directPushBulkUpdateCommandAsString, BulkUpdateCommand.class);

        assertThat(command.getGitHubInteractionType()).isInstanceOf(DirectPushGitHubInteraction.class);
    }

    @Test
    void canDeserializePullRequest() throws IOException {

        String directPushBulkUpdateCommandAsString = TestUtils.readFromInputStream(getClass().getResourceAsStream("/bulkUpdate_pullRequest_Command.json"));

        BulkUpdateCommand command = objectMapper.readValue(directPushBulkUpdateCommandAsString, BulkUpdateCommand.class);

        assertThat(command.getGitHubInteractionType()).isInstanceOf(PullRequestGitHubInteraction.class);
        assertThat(((PullRequestGitHubInteraction) command.getGitHubInteractionType()).getBranchNameToCreate()).isEqualTo("branchForPr");
    }

    @Test
    void canDeserializePullRequestWithNoBranch_butInvalid() throws IOException {

        String directPushBulkUpdateCommandAsString = TestUtils.readFromInputStream(getClass().getResourceAsStream("/bulkUpdate_pullRequest_noBranch_Command.json"));

        BulkUpdateCommand command = objectMapper.readValue(directPushBulkUpdateCommandAsString, BulkUpdateCommand.class);

        assertThat(command.getGitHubInteractionType()).isInstanceOf(PullRequestGitHubInteraction.class);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        PullRequestGitHubInteraction interaction = (PullRequestGitHubInteraction) command.getGitHubInteractionType();

        Set<ConstraintViolation<PullRequestGitHubInteraction>> violations = validator.validate(interaction);
        assertThat(violations).hasSize(1);

    }

}