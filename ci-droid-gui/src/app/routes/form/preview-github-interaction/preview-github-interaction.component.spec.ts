import { PreviewGithubInteractionComponent } from './preview-github-interaction.component';
import { MatCardModule } from '@angular/material/card';
import { Component } from '@angular/core';
import { initContext, TestContext } from '../../../../testing/test.context';

@Component({
  template: `
    <ci-preview-github-interaction
      [title]="title"
      [githubInteraction]="githubInteraction"
      [branchName]="branchName"
      [commitMessage]="commitMessage"
    ></ci-preview-github-interaction>
  `
})
class TesteePreviewGitHubInteractionComponent {
  githubInteraction = 'Pull request';
  title = 'fix pom for version change';
  branchName = 'fix/pom';
  commitMessage = 'fixed the version change';
}

describe('PreviewGithubInteractionComponent', () => {
  // PreviewGithubInteractionComponent - testedComponent
  // TesteePreviewGitHubInteractionComponent - hostComponent
  type Context = TestContext<PreviewGithubInteractionComponent, TesteePreviewGitHubInteractionComponent>;

  const moduleMetaData = {
    imports: [MatCardModule]
  };

  initContext(PreviewGithubInteractionComponent, TesteePreviewGitHubInteractionComponent, moduleMetaData);

  it('should create', function(this: Context) {
    this.detectChanges();
    expect(this.hostComponent).toBeTruthy();
    expect(this.testedComponent).toBeTruthy();
  });

  it('should have an input for github interaction, commit message, title and branch name', function(this: Context) {
    this.detectChanges();
    expect(this.testedComponent.githubInteraction).toBe('Pull request');
    expect(this.testedComponent.title).toBe('fix pom for version change');
    expect(this.testedComponent.branchName).toBe('fix/pom');
    expect(this.testedComponent.commitMessage).toBe('fixed the version change');
    // change the host
    this.hostComponent.githubInteraction = 'Push';
    this.hostComponent.commitMessage = 'fixed the null pointer issue';
    this.hostComponent.title = undefined;
    this.hostComponent.branchName = undefined;
    this.detectChanges();
    expect(this.testedComponent.githubInteraction).toBe('Push');
    expect(this.testedComponent.commitMessage).toBe('fixed the null pointer issue');
    expect(this.testedComponent.title).toBeUndefined();
    expect(this.testedComponent.branchName).toBeUndefined();
  });
});
