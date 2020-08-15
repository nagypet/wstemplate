/*
 * Copyright header
 * The ultimate spring based webservice template project.
 * Author Peter Nagy <nagy.peter.home@gmail.com>
 */

import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpUrlEncodingCodec, HttpBackend} from '@angular/common/http';
import {Observable} from 'rxjs';
import {GlobalService} from './global.service';
import {CertificateFile} from '../modell/keystore';


@Injectable({
  providedIn: 'root'
})
export class AdminService
{
  private httpSilent: HttpClient;

  constructor(
    private http: HttpClient,
    private globalService: GlobalService,
    private handler: HttpBackend
  )
  {
    this.httpSilent = new HttpClient(handler);
  }

  private getServiceUrl(path: string): string
  {
    let host = window.location.hostname;
    let protocol = window.location.protocol;
    let port = window.location.port;

    let url = '';
    if (port === '4200')
    {
      // running on dev environment
       url = 'https://localhost:8400' + path;
    }
    else
    {
      url =  protocol + "//" + host + ":" + port + path;
    }

    console.log("Connecting to '" + url + "'");
    return url;
  }


  private removeWhitespacesFromString(input: string): string
  {
    let codec = new HttpUrlEncodingCodec();
    return codec.encodeValue(input);
    //input.replace(' ', '%20');
  }


  public getVersionInfo(): Observable<any> {

    return this.http.get(this.getServiceUrl("/admin/version"));
  }

  public getSettings(): Observable<any> {
    if (this.globalService.isLoggedIn())
    {
      return this.http.get(this.getServiceUrl("/admin/settings"), this.getAuthHeader());
    }
    else
    {
      return this.http.get(this.getServiceUrl("/admin/settings"));
    }
  }

  public getSettingsSilently(): Observable<any> {
    if (this.globalService.isLoggedIn())
    {
      return this.httpSilent.get(this.getServiceUrl("/admin/settings"), this.getAuthHeader());
    }
    else
    {
      return this.httpSilent.get(this.getServiceUrl("/admin/settings"));
    }
  }

  public postShutdown(): Observable<any>
  {
    return this.http.post(this.getServiceUrl("/admin/shutdown"), '', this.getAuthHeader());
  }

  public getAuthenticationToken(userName: string, password: string): Observable<any>
  {
    return this.http.get(this.getServiceUrl("/authenticate"), this.getAuthHeader(userName, password));
  }

  public logout(): Observable<any>
  {
    return this.http.get(this.getServiceUrl("/logout"));
  }

  public getKeystore(): Observable<any> {
    return this.http.get(this.getServiceUrl("/keystore"), this.getAuthHeader());
  }


  public saveKeystore(): Observable<any> {
    return this.http.post(this.getServiceUrl("/keystore"), null, this.getAuthHeader());
  }


  public getEntriesFromCert(certFile: CertificateFile): Observable<any> {
    return this.http.post(this.getServiceUrl("/keystore/certificates"), certFile, this.getAuthHeader());
  }

  public importCertificateIntoKeystore(certFile: CertificateFile, alias: string): Observable<any> {
    return this.http.post(this.getServiceUrl("/keystore/privatekey"), {certificateFile: certFile, alias: alias}, this.getAuthHeader());
  }

  public removeCertificateFromKeystore(alias: string): Observable<any>
  {
    // we have to remove white spaces from the alias name
    return this.http.delete (this.getServiceUrl("/keystore/privatekey/" + this.removeWhitespacesFromString(alias)), this.getAuthHeader());
  }

  public getTruststore(): Observable<any> {
    return this.http.get(this.getServiceUrl("/truststore"), this.getAuthHeader());
  }

  public importCertificateIntoTruststore(certFile: CertificateFile, alias: string): Observable<any> {
    return this.http.post(this.getServiceUrl("/truststore/certificate"), {certificateFile: certFile, alias: alias}, this.getAuthHeader());
  }

  public removeCertificateFromTruststore(alias: string): Observable<any> {
    // we have to remove white spaces from the alias name
    return this.http.delete (this.getServiceUrl("/truststore/certificate/" + this.removeWhitespacesFromString(alias)), this.getAuthHeader());
  }



  private getAuthHeader(userName?, password?) : {headers: HttpHeaders}
  {
    let authorizationHeader = "";
    if (userName !== undefined && password !== undefined)
    {
      const credentials = userName + ':' + password;
      authorizationHeader = 'Basic ' + btoa(credentials);
    }
    else
    {
      const credentials = this.globalService.getToken();
      authorizationHeader = 'Bearer ' + credentials;
    }

    //console.log(credentials);

    return {
      headers: new HttpHeaders({
        'Content-Type':  'application/json',
        'Authorization': authorizationHeader
      })};
  }
}
