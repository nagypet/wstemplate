import {Observable} from 'rxjs';

export abstract class AbstractAuthService
{
  abstract get displayName$(): Observable<string | undefined>
  abstract get isLoggedIn(): boolean
  abstract get accessToken(): string | null
  abstract login(username: string, password: string): Observable<void>
  abstract logout(): Observable<void>
}
