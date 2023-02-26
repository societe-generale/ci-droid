package com.societegenerale.cidroid.model;

import com.societegenerale.cidroid.api.ResourceToUpdate;
import com.societegenerale.cidroid.api.actionToReplicate.ActionToReplicate;
import com.societegenerale.cidroid.api.gitHubInteractions.AbstractGitHubInteraction;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "gitHubOauthToken")
public class BulkUpdateCommand {

    @ApiModelProperty(position = 1, value="the GitHub Oauth token you've generated, to be able to commit", example = "abcdefghijklmnop", required = true)
    @NotEmpty
    private String gitHubOauthToken;

    @ApiModelProperty(position = 2, value="the Git username", example = "abcdefghijklmnop", required = true)
    @NotEmpty
    private String gitLogin;

    @ApiModelProperty(position = 3, value="your email ID, receive the notification for each resource updated", example = "firstname.lastname@gmail.com", required = true)
    @Email
    private String email;

    @ApiModelProperty(position = 4, value="the commit message we'll use for all updated resources",
                                    notes = "it will be suffixed by a CI-droid specific message, to identify clearly commits performed by CI-droid",
                                    example = "change performed in bulk",
                                    required = true)
    @NotEmpty
    private String commitMessage;


    @ApiModelProperty(position = 5, value="one of the available actions, provided in the GET /cidroid-actions/availableActions endpoint", required = true)
    @NotNull
    private ActionToReplicate updateAction;

    @ApiModelProperty(position = 6, value="do you want to commit directly or make a pull request ?", allowableValues = ".DirectPushGitHubInteraction, .PullRequestGitHubInteraction", required = true)
    @Valid
    private AbstractGitHubInteraction gitHubInteractionType;

    @ApiModelProperty(position = 7, required = true)
    @NotEmpty
    private List<ResourceToUpdate> resourcesToUpdate;

}
