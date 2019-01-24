import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { LoggerTestingModule, NGXLogger, NGXLoggerMock } from 'ngx-logger';
import { of, throwError } from 'rxjs';
import { CiDroidService } from '../../shared/services/ci-droid.service';

import { FormComponent } from './form.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import Action = shared.types.Action;

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let ciDroidService: CiDroidService;
  let logger: NGXLoggerMock;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, LoggerTestingModule, FormsModule, ReactiveFormsModule],
      declarations: [FormComponent],
      providers: [CiDroidService]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    ciDroidService = fixture.debugElement.injector.get(CiDroidService);
    logger = fixture.debugElement.injector.get(NGXLogger);
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('should get Actions when it is initialized', () => {
    const expectedActions: Action[] = [
      {
        expectedFields: [
          {
            name: 'initialValue',
            label: 'old value, to replace',
            fieldType: 'textField'
          },
          {
            name: 'newValue',
            label: 'new value',
            fieldType: 'textField'
          }
        ],
        actionClassToSend: 'simpleReplaceAction',
        label: 'simple replace in the file'
      },
      {
        expectedFields: [
          {
            name: 'staticContent',
            label: 'content to overwrite/create',
            fieldType: 'textArea'
          }
        ],
        actionClassToSend: 'overWriteStaticContentAction',
        label: 'overwrite/create a file with given content'
      }
    ];
    spyOn(ciDroidService, 'getActions').and.returnValue(of(expectedActions));
    expect(component.actions).toBeUndefined();
    component.ngOnInit();
    expect(ciDroidService.getActions).toHaveBeenCalled();
    expect(component.actions.length).toBe(2);
  });

  it('should throw error when it fails to fetch actions', () => {
    const error = new Error('Unable to handle');
    spyOn(ciDroidService, 'getActions').and.returnValue(throwError(error));
    spyOn(logger, 'error').and.callThrough();
    expect(component.actions).toBeUndefined();
    component.ngOnInit();
    expect(ciDroidService.getActions).toHaveBeenCalled();
    expect(component.actions.length).toBe(0);
    expect(logger.error).toHaveBeenCalledWith('Unable to fetch Actions', error);
  });

  it('should have a valid form', () => {
    expect(component.ciDroidForm).toBeDefined();
  });

  describe('github credential section', () => {
    it('should have username field with required validation', () => {
      const usernameCtrl = component.ciDroidForm.get('gitHubCredentials.username');
      expect(usernameCtrl).toBeDefined();
      expect(usernameCtrl).not.toBeNull();
      const errors = usernameCtrl.errors || {};
      expect(usernameCtrl.valid).toBeFalsy();
      expect(errors['required']).toBeTruthy();
    });

    it('should have password field with required validation', () => {
      const passwordCtrl = component.ciDroidForm.get('gitHubCredentials.password');
      expect(passwordCtrl).toBeDefined();
      expect(passwordCtrl).not.toBeNull();
      const errors = passwordCtrl.errors || {};
      expect(passwordCtrl.valid).toBeFalsy();
      expect(errors['required']).toBeTruthy();
    });
  });

  describe('email for notification section', () => {
    it('should have email field with required and email validation', () => {
      const emailCtrl = component.ciDroidForm.get('email');
      expect(emailCtrl).toBeDefined();
      expect(emailCtrl).not.toBeNull();
      let errors = emailCtrl.errors || {};
      expect(emailCtrl.valid).toBeFalsy();
      expect(errors['required']).toBeTruthy();
      emailCtrl.setValue('invalid');
      errors = emailCtrl.errors || {};
      expect(errors['email']).toBeTruthy();
    });
  });

  describe('action to be performed section', () => {
    it('should have action field with required validation', () => {
      const actionCtrl = component.ciDroidForm.get('action');
      expect(actionCtrl).toBeDefined();
      expect(actionCtrl).not.toBeNull();
      const errors = actionCtrl.errors || {};
      expect(actionCtrl.valid).toBeFalsy();
      expect(errors['required']).toBeTruthy();
    });
  });

  describe('pull request or push details section', () => {
    it('should have option field to select Push or Pull Request default being pull request', () => {
      const prOrPushCtrl = component.ciDroidForm.get('prOrPush.option');
      expect(prOrPushCtrl).toBeDefined();
      expect(prOrPushCtrl).not.toBeNull();
      expect(prOrPushCtrl.value).toBe('.PullRequestGitHubInteraction');
      const errors = prOrPushCtrl.errors || {};
      expect(prOrPushCtrl.valid).toBeTruthy();
      expect(errors['required']).toBeFalsy();
    });
  });
});
