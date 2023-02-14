import {Component, OnInit} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {Zone} from "../zone";
import { ZonesService } from '../zones.service';
@Component({
  selector: 'app-zones',
  templateUrl: './zones.component.html',
  styleUrls: ['./zones.component.css']
})
export class ZonesComponent implements OnInit {

  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };

  zoneTypes = [
    {type: "rect", name: "prostokąt"},
    {type: "circ", name: "koło"}
  ];
  constructor(private http: HttpClient, private zonesService : ZonesService) {
  }

  zones : Zone[] = [];

  getZones(): void{
    this.zonesService.getZones()
      .subscribe(zones => this.zones = zones);
    console.log(this.zones)
  }
  addRect(code: string, posX: number, posY: number, zoneHeight:number, zoneWidth:number, zoneType: string): void {
    code = code.trim();
    if (!code) { return; }
    this.zonesService.addZone({code, posX, posY, zoneHeight, zoneWidth, zoneType} as unknown as Zone)
      .subscribe(zone => {
        this.zones.push(zone);
      });
  }
  addCirc(code: string, posX: number, posY: number, zoneWidth:number, zoneType: string): void {
    code = code.trim();
    if (!code) { return; }
    var n = null;
    this.zonesService.addZone({code, posX, posY, n, zoneWidth, zoneType} as unknown as Zone)
      .subscribe(zone => {
        this.zones.push(zone);
      });
  }



  refresh(): void {
    window.location.reload();
  }

  delete(zone: Zone): void {
    this.zones = this.zones.filter(h => h !== zone);
    this.zonesService.deleteZone(zone.code).subscribe();
  }

  ngOnInit(): void {
    this.getZones();
  }

  Number(value: string) {
    return Number(value);
  }
}
