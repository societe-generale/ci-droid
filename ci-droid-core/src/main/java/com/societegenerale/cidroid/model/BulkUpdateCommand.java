package com.societegenerale.cidroid.model;

import com.societegenerale.cidroid.api.ResourceToUpdate;
import com.societegenerale.cidroid.api.actionToReplicate.ActionToReplicate;
import com.societegenerale.cidroid.api.gitHubInteractions.AbstractGitHubInteraction;
import lombok.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "gitHubOauthToken")
public class BulkUpdateCommand {

    @NotEmpty
    private String gitLogin;

    @NotEmpty
    private String gitHubOauthToken;

    @Email
    private String email;

    @NotEmpty
    private String commitMessage;

    @NotNull
    private ActionToReplicate updateAction;

    @Valid
    private AbstractGitHubInteraction gitHubInteractionType;

    @NotEmpty
    private List<ResourceToUpdate> resourcesToUpdate;

}
