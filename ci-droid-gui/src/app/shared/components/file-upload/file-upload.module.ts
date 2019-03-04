import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MatCheckboxModule, MatIconModule, MatTableModule } from '@angular/material';
import { PreviewUploadComponent } from './preview-upload/preview-upload.component';
import { UploadCsvComponent } from './upload-csv/upload-csv.component';

@NgModule({
  imports: [CommonModule, MatTableModule, MatCheckboxModule, MatIconModule],
  declarations: [UploadCsvComponent, PreviewUploadComponent],
  exports: [UploadCsvComponent, PreviewUploadComponent]
})
export class FileUploadModule {}
