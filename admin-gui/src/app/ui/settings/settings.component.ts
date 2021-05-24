/*
 * Copyright header
 * The ultimate spring based webservice template project.
 * Author Peter Nagy <nagy.peter.home@gmail.com>
 */

import {Component, OnInit} from '@angular/core';
import {AdminService} from '../admin.service';
import {AuthService} from '../auth/auth.service';

export class ServerParameter {
  name: string;
  value: string;
  link: boolean;

  constructor(name, value, link) {
    this.name = name;
    this.value = value;
    this.link = this.link;
  }
}

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss']
})
export class SettingsComponent implements OnInit {
  public settings: Array<ServerParameter> = new Array<ServerParameter>();
  public shutdownIsInProgress: boolean = false;

  constructor(
    public adminService: AdminService,
    public authService: AuthService
  ) {

  }

  ngOnInit() {
    this.adminService.getSettings().subscribe(data => {
      this.settings = data;
    });
  }


  onShutdown() {
    this.adminService.postShutdown().subscribe(data => {
      this.shutdownIsInProgress = true;
    });
  }
}
