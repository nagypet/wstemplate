/*
 * Copyright header
 * The ultimate spring based webservice template project.
 * Author Peter Nagy <nagy.peter.home@gmail.com>
 */

import {Component, OnInit} from '@angular/core';
import {AdminService} from '../admin.service';
import {GlobalService} from '../global.service';

export class ServerParameter
{
  name: string;
  value: string;
  link: boolean;

  constructor(name, value, link)
  {
    this.name = name;
    this.value = value;
    this.link = this.link;
  }
}

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent implements OnInit
{
  public settings: Array<ServerParameter> = new Array<ServerParameter>();
  public shutdownIsInProgress: boolean = false;

  constructor(
    public adminService: AdminService,
    public globalService: GlobalService
  )
  {

  }

  ngOnInit()
  {
    this.adminService.getSettingsSilently().subscribe(data => {
      console.log("Authentication is not required, settings can be displayed!");
      this.globalService.setSettingsAvailable(true);
      this.settings = data;
    }, error => {
      console.log(error.status + " authentication is required, no settings can be displayed!");
      this.globalService.setSettingsAvailable(false);
    });
  }

  onShutdown()
  {
    this.adminService.postShutdown().subscribe(data =>
    {
      console.log(data);
      this.shutdownIsInProgress = true;
    });
  }
}
