import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs/index';
import Action = shared.types.Action;
import BulkUpdateRequest = shared.types.BulkUpdateRequest;

@Injectable({
  providedIn: 'root'
})
export class CiDroidService {
  public static readonly url = {
    availableActions: '/cidroid-actions/availableActions',
    performBulkUpdate: '/cidroid-actions/bulkUpdates'
  };

  constructor(private http: HttpClient) {}

  getActions(): Observable<Action[]> {
    return this.http.get<Action[]>(CiDroidService.url.availableActions);
  }

  performBulkUpdate(bulkUpdateRequest: BulkUpdateRequest): Observable<any> {
    return of(true);
  }
}
