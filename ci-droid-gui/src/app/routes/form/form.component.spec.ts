import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { LoggerTestingModule, NGXLogger, NGXLoggerMock } from 'ngx-logger';
import { of, throwError } from 'rxjs/index';
import { CiDroidService } from '../../shared/services/ci-droid.service';

import { FormComponent } from './form.component';
import Action = shared.types.Action;

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let ciDroidService: CiDroidService;
  let logger: NGXLoggerMock;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, LoggerTestingModule],
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
});
