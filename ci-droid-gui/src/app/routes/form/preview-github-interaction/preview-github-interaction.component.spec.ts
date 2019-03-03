import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PreviewGithubInteractionComponent } from './preview-github-interaction.component';
import { MatCardModule } from '@angular/material';

describe('PreviewGithubInteractionComponent', () => {
  let component: PreviewGithubInteractionComponent;
  let fixture: ComponentFixture<PreviewGithubInteractionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [MatCardModule],
      declarations: [PreviewGithubInteractionComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PreviewGithubInteractionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
