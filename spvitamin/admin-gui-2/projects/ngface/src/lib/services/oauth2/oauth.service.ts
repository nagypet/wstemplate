import {HttpClient, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {BehaviorSubject, mapTo, mergeMap, Observable, of, Subject, throwError, timer} from 'rxjs';
import {catchError, finalize, first, map, switchMap, tap} from 'rxjs/operators';
import {OAuthTokenStoreService} from './oauth-token-store.service';
import {OAuthTokenResponse, OAuthUserInfo, StoredToken} from './oauth-models';
import {CookieService} from 'ngx-cookie-service';
import {OAuthConfigService} from './oauth-config.service';

@Injectable({providedIn: 'root'})
export class OAuthService
{
  private get cfg()
  {
    return this.oAuthConfigService.config;
  }


  private token$ = new BehaviorSubject<StoredToken | null>(null);
  private userInfo$ = new BehaviorSubject<OAuthUserInfo | null>(null);
  private refreshing = false;
  private refreshQueue$ = new Subject<void>();
  private refreshTimerSub: any;

  displayName$ = this.userInfo$.pipe(map(u => u?.preferred_username ?? u?.name));


  constructor(
    private httpClient: HttpClient,
    private tokenStoreService: OAuthTokenStoreService,
    private cookieService: CookieService,
    private oAuthConfigService: OAuthConfigService,
  )
  {
    const saved = this.tokenStoreService.getToken();
    if (saved)
    {
      this.token$.next(saved);
      this.scheduleRefresh(saved);
    }
  }


  get accessToken(): string | null
  {
    const t = this.token$.value;
    return t && Date.now() < t.expiresAt ? t.accessToken : null;
  }


  get isLoggedIn(): boolean
  {
    return this.accessToken !== null;
  }


  login(username: string, password: string): Observable<void>
  {
    const body = this.buildForm({
      grant_type: 'password',
      username,
      password,
      client_id: this.cfg.clientId,
      client_secret: this.cfg.clientSecret,
      scope: this.cfg.scope,
    });

    return this.httpClient.post<OAuthTokenResponse>(`${this.cfg.baseUrl}${this.cfg.tokenEndpoint}`, body.toString(), {
      headers: {'Content-Type': 'application/x-www-form-urlencoded'}
    }).pipe(
      tap(res =>
      {
        this.onTokenResponse(res);
        this.getProfile().subscribe();
      }),
      map(() => void 0)
    );
  }


  refreshToken(): Observable<void>
  {
    console.log('refreshToken()');

    if (this.refreshing)
    {
      return this.refreshQueue$.pipe(first(), map(() => void 0));
    }

    this.refreshing = true;
    const body = this.buildForm({
      grant_type: 'refresh_token',
      client_id: this.cfg.clientId,
      client_secret: this.cfg.clientSecret
    });

    return this.httpClient.post<OAuthTokenResponse>(`${this.cfg.baseUrl}${this.cfg.tokenEndpoint}`, body.toString(), {
      headers: {'Content-Type': 'application/x-www-form-urlencoded'}
    }).pipe(
      tap(res =>
      {
        this.onTokenResponse(res);
        this.getProfile().subscribe();
      }),
      finalize(() =>
      {
        this.refreshing = false;
        this.refreshQueue$.next();
      }),
      map(() => void 0),
      catchError(err =>
        {
          // VISSZAADJUK a logout Observable-ját, hogy tényleg lefusson a hívás
          return this.logout().pipe(
            // ha a logout is hibázik, azt lenyeljük
            catchError(() => of(void 0)),
            // majd az eredeti hibát továbbdobjuk a hívónak
            mergeMap(() => throwError(() => err))
          );
        }
      )
    );
  }


  /**
   * logout
   */
  logout(): Observable<void>
  {
    console.log('logout');

    return this.httpClient.post<void>(`${this.cfg.baseUrl}/api/spvitamin/logout`, {}).pipe(
      tap(() =>
      {
        console.log('logout successful');
      }),
      finalize(() =>
      {
        this.cleanUpSessionStorage();
      })
    );
  }


  /**
   * This checks if the session is already authenticated
   */
  getProfile(): Observable<OAuthUserInfo>
  {
    console.log('getProfile()');
    return new Observable<OAuthUserInfo>(observer =>
    {
      this.httpClient.get<OAuthUserInfo>(`${this.cfg.baseUrl}/api/spvitamin/oauth2/userinfo`).subscribe({
        next: userInfo =>
        {
          // OK
          console.log(`getProfile successful for ${userInfo.sub}`);
          this.userInfo$.next(userInfo);
          observer.next(userInfo);
        }, error: err =>
        {
          // error
          console.log('getProfile failed', err);
          observer.error(err);
        }
      });
    });
  }


  private cleanUpSessionStorage(): void
  {
    console.log('cleanUpSessionStorage()');
    this.clearRefreshTimer();
    this.tokenStoreService.clear();
    this.cookieService.deleteAll();
    this.token$.next(null);
    this.userInfo$.next(null);
  }


  private onTokenResponse(res: OAuthTokenResponse): void
  {
    const expiresAt = Date.now() + (res.expires_in - 30) * 1000; // 30 mp ráhagyás
    const stored: StoredToken = {
      accessToken: res.access_token,
      expiresAt
    };
    this.tokenStoreService.setToken(stored);
    this.token$.next(stored);
    this.scheduleRefresh(stored);
  }


  private scheduleRefresh(tokens: StoredToken): void
  {
    this.clearRefreshTimer();
    const delayMs = Math.max(0, tokens.expiresAt - Date.now() - 5000); // még 5 mp ráhagyás
    this.refreshTimerSub = timer(delayMs).pipe(
      switchMap(() => this.refreshToken())
    ).subscribe({
      error: () => this.logout()
    });
  }


  private clearRefreshTimer(): void
  {
    if (this.refreshTimerSub)
    {
      this.refreshTimerSub.unsubscribe?.();
      this.refreshTimerSub = null;
    }
  }


  private buildForm(params: Record<string, string | undefined>): HttpParams
  {
    let form = new HttpParams();
    Object.entries(params).forEach(([k, v]) =>
    {
      if (v !== undefined)
      {
        form = form.set(k, v);
      }
    });
    return form;
  }
}
