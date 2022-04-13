import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { RequestStreamComponent } from './request-stream/request-stream.component';
import { RequestResponseComponent } from './request-response/request-response.component';
import { FireAndForgetComponent } from './fire-and-forget/fire-and-forget.component';
import { HomeComponent } from './home/home.component';

@NgModule({
  declarations: [
    AppComponent,
    RequestStreamComponent,
    RequestResponseComponent,
    FireAndForgetComponent,
    HomeComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgbModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
