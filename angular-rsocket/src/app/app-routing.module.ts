import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {HomeComponent} from "./home/home.component";
import {RequestResponseComponent} from "./request-response/request-response.component";
import {RequestStreamComponent} from "./request-stream/request-stream.component";
import {FireAndForgetComponent} from "./fire-and-forget/fire-and-forget.component";

const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'home', component: HomeComponent },
  { path: 'request-response', component: RequestResponseComponent },
  { path: 'request-stream', component: RequestStreamComponent },
  { path: 'fire-and-forget', component: FireAndForgetComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule { }
