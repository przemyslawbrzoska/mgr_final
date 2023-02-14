import {Injectable, Input, OnInit} from '@angular/core';
import {interval, map, Observable, of, switchMap} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {Position} from "./position";
import {MessageService} from "./message.service";
import {Image} from "./image";
@Injectable({
  providedIn: 'root'
})
export class DataService  {

  _x: number[] = [];
  _y: number[] = [];
  _labels: string[] = [];

  data$: Observable<{ x: number[], y: number[], text: string[]} | null> = of(null);
  constructor(private http: HttpClient, private messageService: MessageService) {
  }
  pollingData: any;

  connectb() {
    const status$ = this.http.get('http://localhost:8080/position/allpositions');

    this.pollingData = interval(1000)
      .pipe(switchMap((_: number) => status$))
      .subscribe(
        (data: any) => {
          this._x = data.posX;
          this._y = data.posY;
          this._labels = data.labels;
          this.messageService.add(data.message);
        },
        (error: any) => console.log(error)
      );

    // Reformat to x/y data to plot.
    this.data$ = interval(1000)
      .pipe(
        map(() => {
          return {
            x: this._x,
            y: this._y,
            text: this._labels
          };
        })
      );
    return this.data$;
  }


}
