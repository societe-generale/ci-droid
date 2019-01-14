import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { CiDroidService } from './ci-droid.service';
import Action = shared.types.Action;

describe('CiDroidService', () => {
  let httpMock: HttpTestingController;
  let ciDroidService: CiDroidService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CiDroidService]
    });
    ciDroidService = TestBed.get(CiDroidService);
    httpMock = TestBed.get(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(ciDroidService).toBeTruthy();
  });

  it('should be able to get ci-droid actions', () => {
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
      },
      {
        expectedFields: [
          {
            name: 'templatedContent',
            label: 'template to use',
            fieldType: 'textArea'
          }
        ],
        actionClassToSend: 'templateBasedContentAction',
        label: 'overwrite a file with a template based content'
      },
      {
        expectedFields: [
          {
            name: 'profileName',
            label: 'profile name, to replace',
            fieldType: 'textField'
          },
          {
            name: 'newProfileContent',
            label: 'new profile, starting with profile XML element',
            fieldType: 'textField'
          }
        ],
        actionClassToSend: 'replaceMavenProfileAction',
        label: "replace and existing Maven profile (or creates, if it doesn't exist) "
      }
    ];
    ciDroidService.getActions().subscribe((actualActions: Action[]) => {
      expect(actualActions).toEqual(expectedActions);
    });
    const request = httpMock.expectOne(`/cidroid-actions/availableActions`);
    request.flush(expectedActions);
    expect(request.request.method).toBe('GET');
  });
});
