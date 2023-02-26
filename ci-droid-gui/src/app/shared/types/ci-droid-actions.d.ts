declare namespace shared.types {
  export interface Field {
    name: string;
    label: string;
    fieldType: string;
    '@class': string;
  }

  export interface Action {
    expectedFields: Field[];
    actionClassToSend: string;
    label: string;
  }

  export interface ResourcesToUpdate {
    repoFullName: string;
    filePathOnRepo: string;
    branchName: string;
  }

  export interface SelectedField {
    label: string;
    value: string;
  }

  export interface AbstractGitHubInteraction {
    '@c': string;
    branchNameToCreate?: string;
    pullRequestTitle?: string;
  }

  export interface ActionToReplicate {
    '@class': string;
    [key: string]: string;
  }

  export interface BulkUpdateRequest {
    gitHubOauthToken: string;
    gitLogin: string;
    email: string;
    commitMessage: string;
    updateAction: ActionToReplicate;
    gitHubInteractionType: AbstractGitHubInteraction;
    resourcesToUpdate: ResourcesToUpdate[];
  }
}

declare namespace shared {
  export const enum GITHUB_INTERACTION {
    Push = '.DirectPushGitHubInteraction',
    PullRequest = '.PullRequestGitHubInteraction'
  }
}
