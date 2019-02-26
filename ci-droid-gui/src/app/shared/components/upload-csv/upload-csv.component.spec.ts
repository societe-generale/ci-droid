import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UploadCsvComponent } from './upload-csv.component';

describe('UploadCsvComponent', () => {
  let component: UploadCsvComponent;
  let fixture: ComponentFixture<UploadCsvComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [UploadCsvComponent]
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
});
