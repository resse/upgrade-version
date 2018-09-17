import {Component} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {environment} from '../environments/environment';
import {timer} from 'rxjs';
import {ThemePalette} from '@angular/material';

export class TicketV1 {
  uuid: string;
  date: number;
  subject: string;
  description: string;
}

export class TicketV2 {
  uuid: string;
  date: number;
  subject: Subject;
  description: string;
}

export class Subject {
  uuid: string;
  subject: string;
}

export interface ChipColor {
  name: string;
  port: string;
  color: string;
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  statesV1: ChipColor[] = [
    {name: 'command-adapter-v1', port: '8181', color: 'primary'},
    {name: 'command-handler-v1', port: '8183', color: 'primary'},
    {name: 'query-adapter-v1', port: '8182', color: 'primary'},
    {name: 'cmd-v2-handler-v1', port: '8184', color: 'primary'}
  ];

  statesV2: ChipColor[] = [
    {name: 'command-adapter-v2', port: '8281', color: 'primary'},
    {name: 'command-handler-v2', port: '8283', color: 'primary'},
    {name: 'query-adapter-v2', port: '8282', color: 'primary'},
    {name: 'cmd-v1-handler-v2', port: '8284', color: 'primary'}
  ];

  updater = timer(1000, 1000);

  displayedColumns = ['uuid', 'subject', 'description'];

  ticketsV1: TicketV1[];
  ticketsV2: TicketV2[];

  ticketV1: TicketV1 = new TicketV1();
  ticketV2: TicketV2 = new TicketV2();

  subjects: Subject[] = [];

  constructor(private http: HttpClient) {
    this.updater.subscribe(time => this.update());
  }

  error: any;

  saveTicketV1() {
    this.http.post(environment.host.replace('4200', '8181') + '/command-adapter-v1/create', this.ticketV1).subscribe(
      data => {
        this.update();
        console.log(data);
      },
      error => this.error = error
    );
  }

  saveTicketV2() {
    this.http.post(environment.host.replace('4200', '8281') + '/command-adapter-v2/create', this.ticketV2).subscribe(
      data => {
        this.update();
        console.log(data);
      },
      error => this.error = error
    );
  }

  update() {
    this.getSubjectsV2();
    this.getTicketsV1();
    this.getTicketsV2();
    this.statesV1.forEach(item => this.health(item));
    this.statesV2.forEach(item => this.health(item));
  }

  getTicketsV1() {
    this.http.get(environment.host.replace('4200', '8182') + '/query-adapter-v1/tickets').subscribe(
      (data: TicketV1[]) => this.ticketsV1 = data,
      error => this.error = error
    );
  }

  getTicketsV2() {
    this.http.get(environment.host.replace('4200', '8282') + '/query-adapter-v2/tickets').subscribe(
      (data: TicketV2[]) => this.ticketsV2 = data,
      error => this.error = error
    );
  }

  getSubjectsV2() {
    this.http.get(environment.host.replace('4200', '8282') + '/query-adapter-v2/subjects').subscribe(
      (data: Subject[]) => {
        if (this.subjects.length !== data.length) {
          this.subjects = data;
        }
      },
      error => this.error = error
    );
  }

  health(item: ChipColor) {
    this.http.get(environment.host.replace('4200', item.port) + '/' + item.name + '/health').subscribe(
      (data: any) => {
        if ('UP' === data.status) {
          item.color = 'primary';
        } else {
          item.color = 'warn';
        }
      },
      error => item.color = 'warn');
  }

}
