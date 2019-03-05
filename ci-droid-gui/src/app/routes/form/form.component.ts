import { Component, OnInit, ViewChild } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { CiDroidService } from '../../shared/services/ci-droid.service';
import { AbstractControl, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatRadioChange, MatSelectChange, MatStepper } from '@angular/material';
import Action = shared.types.Action;
import GITHUB_INTERACTION = shared.GITHUB_INTERACTION;
import Field = shared.types.Field;

@Component({
  selector: 'ci-form',
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.scss']
})
export class FormComponent implements OnInit {
  actions: Action[];
  ciDroidForm: FormGroup;
  hide = false;
  fields: Field[];
  showStatus = false;
  @ViewChild('stepper') stepper: MatStepper;

  readonly pullRequest = shared.GITHUB_INTERACTION.PullRequest;
  readonly push = shared.GITHUB_INTERACTION.Push;

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

  showIcons() {
    this.showStatus = !this.showStatus;
    setTimeout(() => {
      this.showStatus = !this.showStatus;
    }, 5000);
  }

  private createForm() {
    this.ciDroidForm = this.formBuilder.group({
      gitHubCredentials: this.formBuilder.group({
        gitHubOauthToken: ['', [Validators.required]]
      }),
      email: ['', [Validators.required, Validators.email]],
      action: this.formBuilder.group({
        default: ['', [Validators.required]]
      }),
      githubInteraction: this.formBuilder.group({
        option: [GITHUB_INTERACTION.PullRequest, []],
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
      this.fields = selectedAction.expectedFields;
      selectedAction.expectedFields.forEach(field => {
        const validation = field.label.includes('optional') ? [] : [Validators.required];
        actionFormGroup.registerControl(field.name, new FormControl('', validation));
      });
    } else {
      const actionFormGroup = this.ciDroidForm.get('action') as FormGroup;
      actionFormGroup.registerControl('default', new FormControl('', [Validators.required]));
    }
  }

  private clearActionFormControls(): void {
    const actionFormGroup = this.ciDroidForm.get('action') as FormGroup;
    Object.keys(actionFormGroup.controls).forEach(key => {
      if (key !== 'default') {
        actionFormGroup.controls[key].setValue(null);
        actionFormGroup.controls[key].setValidators([]);
        actionFormGroup.controls[key].updateValueAndValidity();
      }
    });
  }

  githubInteractionChanged(matSelectedGithubInteraction: MatRadioChange) {
    this.clearGithubInteractionControls();
    const githubIntegrationGroup = this.ciDroidForm.get('githubInteraction') as FormGroup;
    if (matSelectedGithubInteraction.value === GITHUB_INTERACTION.Push) {
      this.setRequiredValidator(githubIntegrationGroup.controls['commitMessage']);
    } else {
      this.setRequiredValidator(githubIntegrationGroup.controls['pullRequestTitle']);
      this.setRequiredValidator(githubIntegrationGroup.controls['branchName']);
      this.setRequiredValidator(githubIntegrationGroup.controls['commitMessage']);
    }
  }

  private clearGithubInteractionControls() {
    const githubInteractionForm = this.ciDroidForm.get('githubInteraction') as FormGroup;
    Object.keys(githubInteractionForm.controls).forEach(key => {
      if (key !== 'option') {
        githubInteractionForm.controls[key].setValue(null);
        githubInteractionForm.controls[key].setValidators([]);
        githubInteractionForm.controls[key].updateValueAndValidity();
      }
    });
  }

  private setRequiredValidator(control: AbstractControl): void {
    control.setValidators([Validators.required]);
    control.updateValueAndValidity();
  }
}
