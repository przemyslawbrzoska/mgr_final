import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {catchError, tap} from 'rxjs/operators';
import {Zone} from "./zone";

@Injectable({
  providedIn: 'root'
})
export class ZonesService {
  private zonesUrl = 'http://localhost:8080/zone';

  constructor(
    private http: HttpClient) {
  }
  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };
  getZones(): Observable<Zone[]> {
    const url = `${this.zonesUrl}/all`;
    return this.http.get<Zone[]>(url);
  }

  addZone(zone: Zone): Observable<Zone> {
    const url = `${this.zonesUrl}/save`;
    return this.http.post<Zone>(url, zone, this.httpOptions)
  }
  deleteZone(code: string): Observable<Zone> {
    const url = `${this.zonesUrl}/${code}`;
    return this.http.delete<Zone>(url, this.httpOptions);
  }
}
