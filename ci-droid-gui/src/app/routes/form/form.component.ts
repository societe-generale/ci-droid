import { Component, OnInit } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { CiDroidService } from '../../shared/services/ci-droid.service';
import Action = shared.types.Action;

@Component({
  selector: 'ci-form',
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.scss']
})
export class FormComponent implements OnInit {
  actions: Action[];

  constructor(private ciDroidService: CiDroidService, private logger: NGXLogger) {}

  public ngOnInit() {
    this.initializeActions();
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
}
