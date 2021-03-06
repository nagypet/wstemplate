/*
 * Copyright header
 * The ultimate spring based webservice template project.
 * Author Peter Nagy <nagy.peter.home@gmail.com>
 */

import {Injectable} from '@angular/core';
import {HttpBackend, HttpClient, HttpUrlEncodingCodec} from '@angular/common/http';
import {Observable} from 'rxjs';
import {CertificateFile} from '../modell/keystore';


@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private httpSilent: HttpClient;

  constructor(
    private http: HttpClient,
    private handler: HttpBackend
  ) {
    this.httpSilent = new HttpClient(handler);
  }

  public static getServiceUrl(path: string): string {
    const host = window.location.hostname;
    const protocol = window.location.protocol;
    const port = window.location.port;

    let url = '';
    if (port === '4200') {
      // running on dev environment
      url = 'http://localhost:4200' + path;
    } else {
      url = protocol + '//' + host + ':' + port + path;
    }

    console.log('Connecting to \'' + url + '\'');
    return url;
  }


  private removeWhitespacesFromString(input: string): string {
    const codec = new HttpUrlEncodingCodec();
    return codec.encodeValue(input);
    // input.replace(' ', '%20');
  }


  public getVersionInfo(): Observable<any> {

    return this.http.get(AdminService.getServiceUrl('/admin/version'));
  }

  public getSettings(): Observable<any> {
    return this.http.get(AdminService.getServiceUrl('/admin/settings'));
  }

  public postShutdown(): Observable<any> {
    return this.http.post(AdminService.getServiceUrl('/admin/shutdown'), '');
  }

  public getKeystore(): Observable<any> {
    return this.http.get(AdminService.getServiceUrl('/keystore'));
  }


  public saveKeystore(): Observable<any> {
    return this.http.post(AdminService.getServiceUrl('/keystore'), null);
  }


  public getEntriesFromCert(certFile: CertificateFile): Observable<any> {
    return this.http.post(AdminService.getServiceUrl('/keystore/certificates'), certFile);
  }

  public importCertificateIntoKeystore(certFile: CertificateFile, alias: string): Observable<any> {
    return this.http.post(AdminService.getServiceUrl('/keystore/privatekey'), {certificateFile: certFile, alias: alias});
  }

  public removeCertificateFromKeystore(alias: string): Observable<any> {
    // we have to remove white spaces from the alias name
    return this.http.delete(AdminService.getServiceUrl('/keystore/privatekey/' + this.removeWhitespacesFromString(alias)));
  }

  public getTruststore(): Observable<any> {
    return this.http.get(AdminService.getServiceUrl('/truststore'));
  }

  public importCertificateIntoTruststore(certFile: CertificateFile, alias: string): Observable<any> {
    return this.http.post(AdminService.getServiceUrl('/truststore/certificate'), {certificateFile: certFile, alias: alias});
  }

  public removeCertificateFromTruststore(alias: string): Observable<any> {
    // we have to remove white spaces from the alias name
    return this.http.delete(AdminService.getServiceUrl('/truststore/certificate/' + this.removeWhitespacesFromString(alias)));
  }
}
