import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { Router } from '@angular/router';

import { HomePageComponent } from './home-page.component';

describe('HomePageComponent', () => {
  let component: HomePageComponent;
  let fixture: ComponentFixture<HomePageComponent>;
  let routerStub;

  beforeEach(async(() => {
    routerStub = {
      navigate: jasmine.createSpy('navigate')
    };
    TestBed.configureTestingModule({
      declarations: [HomePageComponent],
      providers: [{ provide: Router, useValue: routerStub }]
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

  it('should navigate to form on clicking getting started button', () => {
    spyOn(component, 'navigateToForm').and.callThrough();
    const input = fixture.debugElement.query(By.css('button')).nativeElement;
    input.dispatchEvent(new Event('click'));
    expect(routerStub.navigate).toHaveBeenCalledWith(['/form']);
  });
});
