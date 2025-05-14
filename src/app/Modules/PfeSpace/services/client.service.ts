import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Consultation } from '../models/Consulting';

export interface Client {
  id?: number;
  fullName: string;
  email: string;
}
@Injectable({
  providedIn: 'root'
})
export class ClientService {
  private baseUrl = 'http://localhost:8080/pfespace/api/clients';

  constructor(private http: HttpClient) {}

  registerClient(client: Client): Observable<Client> {
    return this.http.post<Client>(this.baseUrl, client);
  }

  getAllClients(): Observable<Client[]> {
    return this.http.get<Client[]>(this.baseUrl);
  }

  getClientById(id: number): Observable<Client> {
    return this.http.get<Client>(`${this.baseUrl}/${id}`);
  }

  updateClient(id: number, client: Client): Observable<Client> {
    return this.http.put<Client>(`${this.baseUrl}/${id}`, client);
  }

  deleteClient(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
  requestConsultation(clientId: number, specialty: string, startTime: string, endTime: string) {
    const params = new HttpParams()
      .set('specialty', specialty)
      .set('startTime', startTime)
      .set('endTime', endTime);

    return this.http.post<Consultation>(`${this.baseUrl}/${clientId}/request-consultation`, null, { params });
  }

  viewMyConsultations(clientId: number) {
    return this.http.get(`${this.baseUrl}/${clientId}/consultations`);
  }
}
