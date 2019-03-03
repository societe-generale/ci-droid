import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PreviewActionComponent } from './preview-action.component';
import { MatCardModule } from '@angular/material';

describe('PreviewActionComponent', () => {
  let component: PreviewActionComponent;
  let fixture: ComponentFixture<PreviewActionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [MatCardModule],
      declarations: [PreviewActionComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PreviewActionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have an input for the selected action and the content of the action to be performed ', () => {
    expect(component.selectedAction).toBeUndefined();
    expect(component.content).toBeUndefined();
  });
});
