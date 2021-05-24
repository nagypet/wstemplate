/*
 * Copyright header
 * The ultimate spring based webservice template project.
 * Author Peter Nagy <nagy.peter.home@gmail.com>
 */

import {Component, OnInit} from '@angular/core';
import {Location} from '@angular/common';

@Component({
  selector: 'app-about',
  templateUrl: './about.component.html',
  styleUrls: ['./about.component.scss']
})
export class AboutComponent implements OnInit {

  constructor(
    private _location: Location
  ) {
  }

  ngOnInit() {
  }

  onOk() {
    this._location.back();
  }
}
