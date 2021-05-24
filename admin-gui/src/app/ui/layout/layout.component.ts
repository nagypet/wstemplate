/*
 * Copyright header
 * The ultimate spring based webservice template project.
 * Author Peter Nagy <nagy.peter.home@gmail.com>
 */

import { Component, OnInit } from '@angular/core';
import {AdminService} from '../admin.service';

@Component({
  selector: 'app-layout',
  templateUrl: './layout.component.html',
  styleUrls: ['./layout.component.scss']
})
export class LayoutComponent implements OnInit
{
  public version: string = '';
  public build: string = '';

  constructor(
    public adminService: AdminService,
  ) { }

  ngOnInit()
  {
    this.adminService.getVersionInfo().subscribe(data  =>
    {
      console.log(data);
      this.version = data.Version;
      this.build = data.Build;
    });
  }


}
