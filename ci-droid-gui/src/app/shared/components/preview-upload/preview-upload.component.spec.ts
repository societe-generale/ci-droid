import { NO_ERRORS_SCHEMA } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCheckboxModule, MatTableModule } from '@angular/material';
import { By } from '@angular/platform-browser';

import { PreviewUploadComponent } from './preview-upload.component';

describe('PreviewUploadComponent', () => {
  let component: PreviewUploadComponent;
  let fixture: ComponentFixture<PreviewUploadComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [MatTableModule, MatCheckboxModule],
      declarations: [PreviewUploadComponent],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PreviewUploadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('should select all resources', () => {
    expect(component).toBeTruthy();
    component.dataSource.data = [
      {
        repoFullName: 'societe-generale/ci-droid',
        filePathOnRepo: 'Jenkinsfile',
        branchName: 'master'
      }
    ];
    component.selectAll();
    expect(component.selectedResources.selected.length).toEqual(1);
  });
});
