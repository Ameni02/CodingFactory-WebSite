import { HttpClient, HttpContext } from '@angular/common/http';
import { Observable } from 'rxjs';
import {StrictHttpResponse} from "../../services/strict-http-response";

export interface RequestPasswordReset$Params {
  email: string;
}

export function requestPasswordReset(
  http: HttpClient,
  rootUrl: string,
  params: RequestPasswordReset$Params,
  context?: HttpContext
): Observable<StrictHttpResponse<void>> {
  return http.post<void>(`${rootUrl}/auth/request-reset-password`, params, {
    observe: 'response',
    context
  });
}
