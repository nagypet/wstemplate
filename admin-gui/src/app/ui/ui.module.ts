/*
 * Copyright header
 * The ultimate spring based webservice template project.
 * Author Peter Nagy <nagy.peter.home@gmail.com>
 */

import {APP_INITIALIZER, NgModule} from '@angular/core';
import {CommonModule, HashLocationStrategy, LocationStrategy} from '@angular/common';
import {LayoutComponent} from './layout/layout.component';
import {HeaderComponent} from './header/header.component';
import {FooterComponent} from './footer/footer.component';
import {SettingsComponent} from './settings/settings.component';
import {CertificatesComponent} from './certificates/certificates.component';
import {RouterModule, Routes} from '@angular/router';
import {AdminService} from './admin.service';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {AuthInterceptor} from './AuthInterceptor';
import {ToastrModule} from 'ngx-toastr';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {LoginComponent} from './login/login.component';
import {BrowserModule} from '@angular/platform-browser';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {KeystoreComponent, NgbdModalContent} from './certificates/keystore/keystore.component';
import {AboutComponent} from './about/about.component';
import {TabSetComponent} from './tab-set/tab-set.component';
import {AuthGuard} from './auth.guard';
import {AuthService} from './auth.service';
import {getAuthentication, tryGetSettings} from './auth/init/auth-init.factory';


export const routes: Routes = [
  {path: '', redirectTo: 'admin-gui/settings', pathMatch: 'full'},
  {path: 'admin-gui', redirectTo: 'admin-gui/settings', pathMatch: 'full'},
  {
    path: 'admin-gui', component: TabSetComponent,
    children: [
      {path: 'settings', component: SettingsComponent},
      {path: 'keystore', component: CertificatesComponent, canActivate: [AuthGuard]},
      {path: 'truststore', component: CertificatesComponent, canActivate: [AuthGuard]},
    ],
  },
  {path: 'admin-gui/login', component: LoginComponent},
  {path: 'admin-gui/about', component: AboutComponent},
];

@NgModule({
  declarations: [
    LayoutComponent,
    HeaderComponent,
    FooterComponent,
    SettingsComponent,
    CertificatesComponent,
    LoginComponent,
    KeystoreComponent,
    NgbdModalContent,
    AboutComponent,
    TabSetComponent],
  imports: [
    CommonModule,
    BrowserModule,
    NgbModule,
    RouterModule.forRoot(routes, {relativeLinkResolution: 'legacy'}),
    HttpClientModule,
    ToastrModule.forRoot(),
    BrowserAnimationsModule,
  ],
  providers: [
    AdminService,
    AuthService,
    AuthGuard,
    {provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true},
    {provide: LocationStrategy, useClass: HashLocationStrategy},
    {
      provide: APP_INITIALIZER,
      useFactory: getAuthentication,
      multi: true,
      deps: [AuthService],
    },
    {
      provide: APP_INITIALIZER,
      useFactory: tryGetSettings,
      multi: true,
      deps: [AuthService],
    }
  ],
  exports: [LayoutComponent],
  entryComponents: [NgbdModalContent]
})
export class UiModule {
}
