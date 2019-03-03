import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PreviewUploadComponent } from '../../shared/components/preview-upload/preview-upload.component';
import { UploadCsvComponent } from '../../shared/components/upload-csv/upload-csv.component';
import { FormComponent } from './form.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {
  MatButtonModule,
  MatCardModule,
  MatCheckboxModule,
  MatIconModule,
  MatInputModule,
  MatRadioModule,
  MatSelectModule,
  MatStepperModule,
  MatTableModule,
  MatTooltipModule
} from '@angular/material';
import { MdePopoverModule } from '@material-extended/mde';
import { PreviewActionComponent } from './preview-action/preview-action.component';
import { PreviewGithubInteractionComponent } from './preview-github-interaction/preview-github-interaction.component';

@NgModule({
  declarations: [FormComponent, UploadCsvComponent, PreviewUploadComponent, PreviewActionComponent, PreviewGithubInteractionComponent],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatStepperModule,
    MatInputModule,
    MatIconModule,
    MatSelectModule,
    MatRadioModule,
    MatTableModule,
    MatCheckboxModule,
    MatButtonModule,
    MatTooltipModule,
    MdePopoverModule,
    MatCardModule
  ],
  exports: [FormComponent, PreviewActionComponent, PreviewGithubInteractionComponent]
})
export class FormModule {}
