import { Component, OnInit } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { CiDroidService } from '../../shared/services/ci-droid.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
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
      action: ['', [Validators.required]],
      prOrPush: this.formBuilder.group({
        option: [GITHUB_INTERACTION.PullRequest, [Validators.required]]
      })
    });
  }
}
