import { HttpClient, HttpHeaders, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { getUserById } from '../fn/user-controller/get-user-by-id';
import { GetUserById$Params } from '../fn/user-controller/get-user-by-id';
import { updateUser } from '../fn/user-controller/update-user';
import { UpdateUser$Params } from '../fn/user-controller/update-user';
import { uploadProfilePicture } from '../fn/user-controller/upload-profile-picture';
import { UploadProfilePicture$Params } from '../fn/user-controller/upload-profile-picture';
import { User } from '../models/user';

@Injectable({ providedIn: 'root' })
export class UserControllerService extends BaseService {
  constructor(config: ApiConfiguration, override http: HttpClient) {
    super(config, http);
  }

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('authToken');
    if (!token) {
      console.error('No token found in localStorage!');
    }
    return new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });
  }

  getUserById(params: GetUserById$Params, context?: HttpContext): Observable<User> {
    return this.http
      .get<User>(`${this.rootUrl}/users/${params.id}`, { headers: this.getAuthHeaders() })
      .pipe(map((r) => r));
  }

  updateUser(params: UpdateUser$Params, context?: HttpContext): Observable<User> {
    return this.http
      .put<User>(`${this.rootUrl}/users/${params.id}`, params.body, { headers: this.getAuthHeaders() })
      .pipe(map((r) => r));
  }

  uploadProfilePicture(params: UploadProfilePicture$Params, context?: HttpContext): Observable<string> {
    if (!params.body || !params.body.file) {
      throw new Error('File is required for uploading profile picture');
    }

    const formData = new FormData();
    formData.append('file', params.body.file);

    return this.http.post<string>(`${this.rootUrl}/users/${params.id}/upload`, formData, { 
      headers: this.getAuthHeaders().delete('Content-Type'), // ✅ Ensure 'Content-Type' is removed
      reportProgress: true,
      observe: 'body'
    }).pipe(
      map((response) => {
        console.log("✅ Profile picture uploaded:", response);
        return response;
      })
    );
  }
}
