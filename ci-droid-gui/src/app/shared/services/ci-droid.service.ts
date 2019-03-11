import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/index';
import Action = shared.types.Action;
import BulkUpdateRequest = shared.types.BulkUpdateRequest;

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

  sendBulkUpdateAction(req: BulkUpdateRequest): Observable<BulkUpdateRequest> {
    return this.http.post<BulkUpdateRequest>(CiDroidService.url.sendBulkActions, req);
  }
}
