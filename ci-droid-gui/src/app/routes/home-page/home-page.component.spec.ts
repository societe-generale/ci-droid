import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HomePageComponent } from './home-page.component';

describe('HomePageComponent', () => {
  let component: HomePageComponent;
  let fixture: ComponentFixture<HomePageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [HomePageComponent]
    }).compileComponents();
  }));

  beforeEach(
    (): any => {
      fixture = TestBed.createComponent(HomePageComponent);
      component = fixture.componentInstance;
    }
  );

  it('should be created', () => {
    expect(component).toBeTruthy();
    const element: HTMLElement = fixture.debugElement.nativeElement;
    expect(element.querySelector('div.title').textContent).toBe('CI - DROID');
  });
});
