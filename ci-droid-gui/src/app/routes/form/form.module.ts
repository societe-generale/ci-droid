import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UploadCsvComponent } from '../../shared/components/upload-csv/upload-csv.component';
import { FormComponent } from './form.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatIconModule, MatInputModule, MatRadioModule, MatSelectModule, MatStepperModule } from '@angular/material';

@NgModule({
  declarations: [FormComponent, UploadCsvComponent],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatStepperModule,
    MatInputModule,
    MatIconModule,
    MatSelectModule,
    MatRadioModule
  ]
})
export class FormModule {}
