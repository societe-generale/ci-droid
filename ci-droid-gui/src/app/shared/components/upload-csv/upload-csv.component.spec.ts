import { NO_ERRORS_SCHEMA } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCheckboxModule, MatTableModule } from '@angular/material';
import { PreviewUploadComponent } from '../preview-upload/preview-upload.component';
import { UploadCsvComponent } from './upload-csv.component';

describe('UploadCsvComponent', () => {
  let component: UploadCsvComponent;
  let fixture: ComponentFixture<UploadCsvComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [MatTableModule, MatCheckboxModule],
      declarations: [UploadCsvComponent, PreviewUploadComponent],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UploadCsvComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should erase the file on file reset', () => {
    component.fileName = 'test-file';
    expect(component.fileName.length).toBeGreaterThan(0);
    component.resetFile();
    expect(component.fileName.length).toEqual(0);
    expect(component.resourcesToUpdate.length).toEqual(0);
  });

  it('should convert CSV to JSON', () => {
    let sampleCSV;
    sampleCSV = 'societe-generale/ci-droid;JenkinsFile;master';
    const event = {
      target: {
        result: sampleCSV
      }
    };
    component.onReaderLoad(event);
    fixture.detectChanges();
    expect(component.resourcesToUpdate.length).toEqual(1);
  });

  it('should get the file content on change in the file', () => {
    const blob = new Blob(['data'], { type: 'text/csv' });
    const file1 = new File([blob], 'test-file1', null);
    const file2 = new File([blob], 'test-file2', null);
    const event = {
      target: {
        files: [file1]
      },
      dataTransfer: {
        files: [file2]
      },
      type: 'drop'
    };
    component.onFileChange(event);
    expect(component.fileName).toBe('test-file1');
    component.handleDragAndDrop(event);
    expect(component.fileName).toBe('test-file2');
  });
});
