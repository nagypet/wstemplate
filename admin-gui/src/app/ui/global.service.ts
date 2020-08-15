/*
 * Copyright header
 * The ultimate spring based webservice template project.
 * Author Peter Nagy <nagy.peter.home@gmail.com>
 */

import { Injectable } from '@angular/core';
import {AdminService} from './admin.service';

@Injectable({
  providedIn: 'root'
})
export class GlobalService
{
  private loggedIn: boolean = false;
  private userName: string = '';
  private token: string = '';
  private settingsAreAvailable = false;

  constructor(
  ) {}

  public login(userName: string, token: string)
  {
    this.userName = userName;
    this.token = token;
    this.loggedIn = true;
    this.settingsAreAvailable = true;
  }


  public logout()
  {
    this.userName = '';
    this.token = '';
    this.loggedIn = false;
  }


  public getUserName(): string
  {
    return this.userName;
  }

  public getToken(): string
  {
    return this.token;
  }

  public isLoggedIn(): boolean
  {
    return this.loggedIn;
  }

  public setSettingsAvailable(f)
  {
    this.settingsAreAvailable = f;
  }

  public areSettingsAvailable(): boolean
  {
    return this.settingsAreAvailable;
  }

}
