import { NO_ERRORS_SCHEMA } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { SelectionModel } from '@angular/cdk/collections';
import { MatCheckboxModule, MatTableModule } from '@angular/material';
import { PreviewUploadComponent } from './preview-upload.component';

import ResourcesToUpdate = shared.types.ResourcesToUpdate;

describe('PreviewUploadComponent', () => {
  let component: PreviewUploadComponent;
  let fixture: ComponentFixture<PreviewUploadComponent>;
  const resource = [
    {
      repoFullName: 'societe-generale/ci-droid',
      filePathOnRepo: 'Jenkinsfile',
      branchName: 'master'
    }
  ];

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

  it('should select all checked resources', () => {
    expect(component).toBeTruthy();
    component.selectedRows = resource;
    component.selectCheckedResources();
    expect(component.selectedResources.selected.length).toEqual(1);
  });

  describe('should test the selection', () => {
    it('should clear the selection when all are resources are selected', () => {
      component.dataSource.data = resource;
      component.selectedResources = new SelectionModel<ResourcesToUpdate>(true, resource);
      component.selectAll();
      expect(component.selectedResources.selected.length).toBe(0);
    });

    it('should select all resources when none is selected', () => {
      component.dataSource.data = resource;
      component.selectAll();
      expect(component.selectedResources.selected.length).toBe(1);
    });
  });
});
