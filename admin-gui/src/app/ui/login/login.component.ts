/*
 * Copyright header
 * The ultimate spring based webservice template project.
 * Author Peter Nagy <nagy.peter.home@gmail.com>
 */

import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {AuthService} from '../auth.service';
import {Location} from '@angular/common';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  @ViewChild('usernameInput', {static: true}) usernameInput: ElementRef;
  @ViewChild('passwordInput', {static: true}) passwordInput: ElementRef;

  returnUrl: string;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {
  }

  ngOnInit() {
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
  }

  onLogin() {
    let userName = this.usernameInput.nativeElement.value;
    let password = this.passwordInput.nativeElement.value;

    this.authService.authenticateWithUserNamePassword(userName, password).subscribe(data => {
      this.router.navigateByUrl(this.returnUrl);
    });

  }

  onCancel() {
    this.authService.logout().subscribe(res => {
      //location.reload();
    });

    this.router.navigateByUrl('/');
  }


  onKeyPress(event: KeyboardEvent) {
    //console.log("onKeyPress " + event.key);
    if (event.key === 'Enter') {
      this.onLogin();
    } else if (event.key === 'Escape') {
      this.onCancel();
    }
  }
}
