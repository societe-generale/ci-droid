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

    @ApiModelProperty(position = 1, value="the git login to use when we'll commit the change", required = true)
    @NotEmpty
    private String gitLogin;

    @ApiModelProperty(position = 2, required = true)
    @NotEmpty
    private String gitHubOauthToken;

    @ApiModelProperty(position = 3, required = true)
    @Email
    private String email;

    @ApiModelProperty(position = 4, required = true)
    @NotEmpty
    private String commitMessage;

    @ApiModelProperty(position = 5, required = true)
    @NotNull
    private ActionToReplicate updateAction;

    @ApiModelProperty(position = 6, required = true)
    @Valid
    private AbstractGitHubInteraction gitHubInteractionType;

    @ApiModelProperty(position = 7, required = true)
    @NotEmpty
    private List<ResourceToUpdate> resourcesToUpdate;

}
