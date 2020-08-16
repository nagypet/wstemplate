/*
 * Copyright header
 * The ultimate spring based webservice template project.
 * Author Peter Nagy <nagy.peter.home@gmail.com>
 */

import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {AdminService} from '../admin.service';
import {GlobalService} from '../global.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit
{
  @ViewChild('usernameInput', { static: true }) usernameInput: ElementRef;
  @ViewChild('passwordInput', { static: true }) passwordInput: ElementRef;

  constructor(
    private router: Router, private route: ActivatedRoute,
    private adminService: AdminService,
    private globalService: GlobalService
) { }

  ngOnInit() {
  }

  onLogin()
  {
    let userName = this.usernameInput.nativeElement.value;
    let password = this.passwordInput.nativeElement.value;

    this.adminService.getAuthenticationToken(userName, password).subscribe(data  =>
    {
      console.log(data);
      this.globalService.login(userName, data.jwt);

      this.router.navigate(['/']);
    });

  }

  onCancel()
  {
    this.adminService.logout().subscribe(res => {
      location.reload();
    },err => {
      console.log(err);
    });

    this.globalService.logout();

    this.router.navigate(['/']);
  }


  onKeyPress(event: KeyboardEvent)
  {
    //console.log("onKeyPress " + event.key);
    if (event.key === 'Enter')
    {
      this.onLogin();
    }
    else if (event.key === 'Escape')
    {
      this.onCancel();
    }
  }
}
