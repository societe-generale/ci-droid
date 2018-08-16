package com.societegenerale.cidroid.controllers;

import com.societegenerale.cidroid.api.ResourceToUpdate;
import com.societegenerale.cidroid.api.actionToReplicate.ActionToReplicate;
import com.societegenerale.cidroid.api.actionToReplicate.fields.ExpectedField;
import com.societegenerale.cidroid.model.BulkUpdateCommand;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/cidroid-actions")
public class CiDroidActionsController {

    private MessageChannel ciDroidActionsChannel;

    private List<ActionToReplicate> availableActions;

    private static List<AvailableActionForUI> availableActionsForUI;

    public CiDroidActionsController(@Qualifier("actions-to-perform") MessageChannel ciDroidActionsChannel, List<ActionToReplicate> availableActions) {
        this.ciDroidActionsChannel = ciDroidActionsChannel;
        this.availableActions = availableActions;
    }

    @PostConstruct
    protected void init() {
        availableActionsForUI = availableActions.stream()
                .map(a -> AvailableActionForUI.fromAvailableAction(a))
                .collect(Collectors.toList());
    }

    @PostMapping(path = "bulkUpdates")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> onBulkUpdateRequest(@RequestBody @Valid BulkUpdateCommand bulkUpdateCommand) {

        log.info("received a bulkUpdateCommand {}", bulkUpdateCommand);

        for (ResourceToUpdate resourceToUpdate : bulkUpdateCommand.getResourcesToUpdate()) {

            BulkUpdateCommand singleResourceUpdateCommand = BulkUpdateCommand.builder().gitLogin(bulkUpdateCommand.getGitLogin())
                    .gitHubOauthToken(bulkUpdateCommand.getGitHubOauthToken())
                    .email(bulkUpdateCommand.getEmail())
                    .gitHubInteractionType(bulkUpdateCommand.getGitHubInteractionType())
                    .updateAction(bulkUpdateCommand.getUpdateAction())
                    .commitMessage(bulkUpdateCommand.getCommitMessage())
                    // creating SINGLE resource update command
                    .resourcesToUpdate(Arrays.asList(resourceToUpdate))
                    .build();

            log.info("sending singleResourceUpdateCommand for processing {}...", singleResourceUpdateCommand);
            ciDroidActionsChannel.send(MessageBuilder.withPayload(singleResourceUpdateCommand).build());

        }

        return ResponseEntity.accepted().build();
    }

    @GetMapping(path = "availableActions")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    ResponseEntity<List<AvailableActionForUI>> fetchAvailableActions() {

        log.debug("received a request for available actions");

        return new ResponseEntity<>(availableActionsForUI, HttpStatus.OK);
    }

    @Data
    @AllArgsConstructor
    protected static class AvailableActionForUI {

        private final List<ExpectedField> expectedFields;

        private final String actionClassToSend;

        private final String label;

        public static AvailableActionForUI fromAvailableAction(ActionToReplicate a) {

            return new AvailableActionForUI(a.getExpectedUIFields(), a.getClass().getName(), a.getDescriptionForUI());
        }
    }
}