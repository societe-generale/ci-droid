import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { PreviewUploadComponent } from './preview-upload/preview-upload.component';
import { UploadCsvComponent } from './upload-csv/upload-csv.component';

@NgModule({
  imports: [CommonModule, MatTableModule, MatCheckboxModule, MatIconModule],
  declarations: [UploadCsvComponent, PreviewUploadComponent],
  exports: [UploadCsvComponent, PreviewUploadComponent]
})
export class FileUploadModule {}
