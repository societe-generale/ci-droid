import { Component, OnInit } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { CiDroidService } from '../../shared/services/ci-droid.service';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatRadioChange, MatSelectChange } from '@angular/material';
import Action = shared.types.Action;
import GITHUB_INTERACTION = shared.GITHUB_INTERACTION;

@Component({
  selector: 'ci-form',
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.scss']
})
export class FormComponent implements OnInit {
  actions: Action[];
  ciDroidForm: FormGroup;

  constructor(private ciDroidService: CiDroidService, private logger: NGXLogger, private formBuilder: FormBuilder) {}

  public ngOnInit() {
    this.initializeActions();
    this.createForm();
  }

  public initializeActions() {
    this.ciDroidService.getActions().subscribe(
      (response: Action[]) => {
        this.actions = response;
      },
      error => {
        this.actions = [];
        this.logger.error('Unable to fetch Actions', error);
      }
    );
  }

  private createForm() {
    this.ciDroidForm = this.formBuilder.group({
      gitHubCredentials: this.formBuilder.group({
        username: ['', [Validators.required]],
        password: ['', [Validators.required]]
      }),
      email: ['', [Validators.required, Validators.email]],
      action: this.formBuilder.group({
        default: ['', [Validators.required]]
      }),
      githubInteraction: this.formBuilder.group({
        option: [GITHUB_INTERACTION.PullRequest, [Validators.required]],
        pullRequestTitle: ['', [Validators.required]],
        branchName: ['', [Validators.required]],
        commitMessage: ['', [Validators.required]]
      })
    });
  }

  onActionChanged(matSelectedAction: MatSelectChange): void {
    const selectedAction = this.actions.find(action => action.actionClassToSend === matSelectedAction.value);
    this.clearActionFormControls();
    if (selectedAction) {
      const actionFormGroup = this.ciDroidForm.get('action') as FormGroup;
      selectedAction.expectedFields.forEach(field => {
        actionFormGroup.registerControl(field.name, new FormControl('', [Validators.required]));
      });
    } else {
      const actionFormGroup = this.ciDroidForm.get('action') as FormGroup;
      actionFormGroup.registerControl('default', new FormControl('', [Validators.required]));
    }
  }

  private clearActionFormControls(): void {
    const actionFormGroup = this.ciDroidForm.get('action') as FormGroup;
    Object.keys(actionFormGroup.controls).forEach(key => {
      actionFormGroup.removeControl(key);
    });
  }

  githubInteractionChanged(matSelectedGithubInteraction: MatRadioChange) {
    this.clearGithubInteractionControls();
    const githubIntegrationGroup = this.ciDroidForm.get('githubInteraction') as FormGroup;
    if (matSelectedGithubInteraction.value === GITHUB_INTERACTION.Push) {
      githubIntegrationGroup.registerControl('commitMessage', new FormControl('', [Validators.required]));
    } else {
      githubIntegrationGroup.registerControl('pullRequestTitle', new FormControl('', [Validators.required]));
      githubIntegrationGroup.registerControl('branchName', new FormControl('', [Validators.required]));
      githubIntegrationGroup.registerControl('commitMessage', new FormControl('', [Validators.required]));
    }
  }

  private clearGithubInteractionControls() {
    const githubInteractionForm = this.ciDroidForm.get('githubInteraction') as FormGroup;
    Object.keys(githubInteractionForm.controls).forEach(key => {
      if (key !== 'option') {
        githubInteractionForm.removeControl(key);
      }
    });
  }
}
