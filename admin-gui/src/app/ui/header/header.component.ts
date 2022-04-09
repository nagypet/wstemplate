/*
 * Copyright header
 * The ultimate spring based webservice template project.
 * Author Peter Nagy <nagy.peter.home@gmail.com>
 */

import {Component, OnInit} from '@angular/core';
import {AdminService} from '../admin.service';
import {AuthService} from '../auth/auth.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  public title: string = '';
  public version: string = '';

  constructor
  (
    private adminService: AdminService,
    public authService: AuthService,
    private router: Router,
  ) {

  }

  ngOnInit() {
    this.adminService.getVersionInfo().subscribe(data => {
      console.log(data);
      this.title = data.Title;
      this.version = data.Version;
    });
  }

  onLogout() {
    this.authService.logout().subscribe().add(() => {
      this.authService.tryGetSettings().subscribe().add(() => {
        this.router.navigateByUrl('/');
      });
    });
  }
}
