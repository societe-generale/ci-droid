declare namespace shared.types {
  interface Field {
    name: string;
    label: string;
    fieldType: string;
    '@class': string;
  }

  interface Action {
    expectedFields: Field[];
    actionClassToSend: string;
    label: string;
  }
}

declare namespace shared {
  export const enum GITHUB_INTERACTION {
    Push = '.DirectPushGitHubInteraction',
    PullRequest = '.PullRequestGitHubInteraction'
  }
}
