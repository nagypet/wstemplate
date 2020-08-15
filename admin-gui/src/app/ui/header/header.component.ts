/*
 * Copyright header
 * The ultimate spring based webservice template project.
 * Author Peter Nagy <nagy.peter.home@gmail.com>
 */

import { Component, OnInit } from '@angular/core';
import {AdminService} from '../admin.service';
import {GlobalService} from '../global.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit
{
  public title: string = '';
  public version: string = '';

  constructor
  (
    public adminService: AdminService,
    public globalService: GlobalService
  )
  {

  }

  ngOnInit()
  {
    this.adminService.getVersionInfo().subscribe(data  =>
    {
      console.log(data);
      this.title = data.Title;
      this.version = data.Version;
    });

  }

  onLogout()
  {
    this.globalService.logout();

    this.adminService.logout().subscribe(res => {
      location.reload();
    },err => {
      console.log(err);
    });

    this.adminService.getSettingsSilently().subscribe(data => {
      console.log("Authentication is not required, settings can be displayed!");
      this.globalService.setSettingsAvailable(true);
    }, error => {
      console.log(error.status + " authentication is required, no settings can be displayed!");
      this.globalService.setSettingsAvailable(false);
    });
  }
}
