import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import * as PlotlyJS from 'plotly.js-dist-min';
import {PlotlyModule} from 'angular-plotly.js';
import {AppComponent} from './app.component';
import {CommonModule} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {HttpClientModule} from '@angular/common/http';
import {FileUploadComponent} from './file-upload/file-upload.component';
import {MessagesComponent} from './messages/messages.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {ScrollingModule} from "@angular/cdk/scrolling";
import {ZonesComponent} from './zones/zones.component';
import {MatInputModule} from "@angular/material/input";
import {MatRadioModule} from "@angular/material/radio";
import {MatSlideToggleModule} from "@angular/material/slide-toggle";

PlotlyModule.plotlyjs = PlotlyJS;

@NgModule({
  declarations: [
    AppComponent,
    FileUploadComponent,
    MessagesComponent,
    ZonesComponent
  ],
  imports: [
    BrowserModule,
    CommonModule,
    PlotlyModule,
    FormsModule,
    HttpClientModule,
    ScrollingModule,
    BrowserAnimationsModule,
    MatInputModule,
    MatRadioModule,
    MatSlideToggleModule
  ],

  bootstrap: [AppComponent]
})
export class AppModule {
}
