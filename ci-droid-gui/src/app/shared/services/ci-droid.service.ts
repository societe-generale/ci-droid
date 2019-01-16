import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/index';
import Action = shared.types.Action;

@Injectable({
  providedIn: 'root'
})
export class CiDroidService {
  public static readonly url = {
    availableActions: '/cidroid-actions/availableActions'
  };

  constructor(private http: HttpClient) {}

  getActions(): Observable<Action[]> {
    return this.http.get<Action[]>(CiDroidService.url.availableActions);
  }
}
