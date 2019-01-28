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
            '@class': 'com.societegenerale.cidroid.api.actionToReplicate.fields.TextArea',
            name: 'staticContent',
            label: 'content to overwrite/create',
            fieldType: 'textArea'
          }
        ],
        actionClassToSend: 'com.societegenerale.cidroid.api.actionToReplicate.OverwriteStaticFileAction',
        label: 'overwrite/create a file with given content'
      },
      {
        expectedFields: [
          {
            '@class': 'com.societegenerale.cidroid.api.actionToReplicate.fields.TextField',
            name: 'initialValue',
            label: 'old value, to replace',
            fieldType: 'textField'
          },
          {
            '@class': 'com.societegenerale.cidroid.api.actionToReplicate.fields.TextField',
            name: 'newValue',
            label: 'new value',
            fieldType: 'textField'
          }
        ],
        actionClassToSend: 'com.societegenerale.cidroid.api.actionToReplicate.SimpleReplaceAction',
        label: 'simple replace in the file'
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
