import {Injectable} from '@angular/core';

export interface OAuthConfig
{
  baseUrl: string;
  tokenEndpoint: string;
  clientId?: string;
  clientSecret?: string;
  scope?: string;
}

@Injectable({providedIn: 'root'})
export class OAuthConfigService
{
  private _config: OAuthConfig | null = null;


  configure(cfg: OAuthConfig): void
  {
    this._config = {...cfg};
  }


  get config(): OAuthConfig
  {
    if (!this._config)
    {
      throw new Error('OAuthConfigService is not configured. Call OAuthConfigService.configure(...) during app initialization.');
    }
    return this._config;
  }


  // Helper to check if configured without throwing
  get isConfigured(): boolean
  {
    return !!this._config;
  }
}
