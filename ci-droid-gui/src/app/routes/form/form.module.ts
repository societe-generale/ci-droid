import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PreviewUploadComponent } from '../../shared/components/preview-upload/preview-upload.component';
import { UploadCsvComponent } from '../../shared/components/upload-csv/upload-csv.component';
import { FormComponent } from './form.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {
  MatIconModule,
  MatInputModule,
  MatRadioModule,
  MatSelectModule,
  MatStepperModule,
  MatTableModule,
  MatCheckboxModule
} from '@angular/material';

@NgModule({
  declarations: [FormComponent, UploadCsvComponent, PreviewUploadComponent],
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
    MatCheckboxModule
  ]
})
export class FormModule {}
