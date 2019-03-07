import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { CiDroidService } from './ci-droid.service';
import Action = shared.types.Action;
import CiDroidRequest = shared.CiDroidRequest;

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

  it('should be able to send bulk updates to ci-droid', () => {
    const ciDroidRequest: CiDroidRequest = {
      gitHubOauthToken: 'token',
      email: 'paul58914080@gmail.com',
      commitMessage: 'testing my commit',
      updateAction: {},
      gitHubInteractionType: {
        '@c': shared.GITHUB_INTERACTION.PullRequest,
        branchNameToCreate: 'fix/bug',
        pullRequestTitle: 'fix the issue on ci-droid'
      },
      resourcesToUpdate: []
    };
    ciDroidService.sendBulkUpdateAction(ciDroidRequest).subscribe();
    const req = httpMock.expectOne('/cidroid-actions/bulkUpdates');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toBe(ciDroidRequest);
    req.flush({});
  });
});
