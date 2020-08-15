/*
 * Copyright header
 * The ultimate spring based webservice template project.
 * Author Peter Nagy <nagy.peter.home@gmail.com>
 */

import {Injectable} from '@angular/core';
import {HttpErrorResponse, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {catchError} from 'rxjs/operators';
import {throwError} from 'rxjs';
import {ToastrService} from 'ngx-toastr';
import {GlobalService} from './global.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor
{

  constructor(
    private toastr: ToastrService,
    private globalService: GlobalService
  ) {}

  intercept(req: HttpRequest<any>, next: HttpHandler)
  {
    /*
    // Authorization header will be inserted in AdminService
    const token = this.globalService.getToken();
    const authorizationHeader = 'Bearer ' + token;
    console.log('Authorization header: ' + authorizationHeader);

    const modifiedReq = req.clone({
      headers: req.headers.set('Authorization', authorizationHeader)
    });
    */

    return next.handle(req).pipe(
      catchError((err: HttpErrorResponse) => {
        if (err.status != 200)
        {
          let message = "Error";
          if (err.error !== null)
          {
            message = err.error.message;
          }
          this.toastr.error(message, "HTTP Error: " + err.status, {
            timeOut: 3000,
            progressBar: true,
            progressAnimation: 'increasing'
          });
          console.log('Unathorized access');
        }
        return throwError(err);
      })
    );
  }

}
