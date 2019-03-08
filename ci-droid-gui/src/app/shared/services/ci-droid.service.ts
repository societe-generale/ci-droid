import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs/index';
import Action = shared.types.Action;
import BulkUpdateRequest = shared.types.BulkUpdateRequest;
import Action = shared.types.Action;
import CiDroidRequest = shared.CiDroidRequest;

@Injectable({
  providedIn: 'root'
})
export class CiDroidService {
  private static readonly url = {
    availableActions: '/cidroid-actions/availableActions',
    sendBulkActions: '/cidroid-actions/bulkUpdates'
  };

  constructor(private http: HttpClient) {}

  getActions(): Observable<Action[]> {
    return this.http.get<Action[]>(CiDroidService.url.availableActions);
  }

  performBulkUpdate(bulkUpdateRequest: BulkUpdateRequest): Observable<any> {
    return of(true);
  }

  sendBulkUpdateAction(req: CiDroidRequest): Observable<CiDroidRequest> {
    return this.http.post<CiDroidRequest>(CiDroidService.url.sendBulkActions, req);
  }
}
