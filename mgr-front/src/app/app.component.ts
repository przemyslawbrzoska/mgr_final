import {Component, OnInit} from '@angular/core';
import {map, Observable, of} from "rxjs";
import {DataService} from "./data.service";
import { MessageService } from './message.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import {Image} from "./image";
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
  title = 'streaming-data';
  xRange : number = 100;
  yRange : number = 100;
  trace$: Observable<any> = of(null);

  _images: Image[] = [];

  layout :any

  constructor(private dataService: DataService, private messageService: MessageService, private http: HttpClient,) {
  }

  getImages(): Observable<Image[]> {
    return this.http.get<Image[]>('http://localhost:8080/zone/all/plotly')
  }
  ngOnInit() {
    this.layout ={
      yaxis: {
        range: [0, this.xRange],
        showgrid :false,
      },
      xaxis: {
        range: [0, this.xRange],
        showgrid :false,
      },
      autosize: false,
      width: 1000,
      title: "Mapa pozycji",
      height: 1000
    }
    this.getImages().subscribe(images => this.layout.images = images)

    this.trace$ = this.dataService.connectb()
      .pipe(
        map( data => {
            return {
              ...data,
              mode: 'markers',
              type: 'scatter',
              marker: {size: 20,
                color: ["blue", "green", "yellow", "orange", "red"]},
            }
          }
        )
      )

  }


}
