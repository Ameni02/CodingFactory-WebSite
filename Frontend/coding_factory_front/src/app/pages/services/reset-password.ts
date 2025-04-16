import { HttpClient, HttpContext } from '@angular/common/http';
import { Observable } from 'rxjs';
import {StrictHttpResponse} from "../../services/strict-http-response";

export interface ResetPassword$Params {
  token: string;
  newPassword: string;
}

export function resetPassword(
  http: HttpClient,
  rootUrl: string,
  params: ResetPassword$Params,
  context?: HttpContext
): Observable<StrictHttpResponse<void>> {
  return http.post<void>(`${rootUrl}/auth/reset-password`, params, {
    observe: 'response',
    context
  });
}
