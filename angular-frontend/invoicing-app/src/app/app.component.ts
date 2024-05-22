import { Component } from '@angular/core';
import { NgModule } from '@angular/core';

import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { Company } from './company';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'invoicing-app';

  companies: Company[] = [
    {
      taxIdentificationNumber: '111-111-11-11',
      address: 'ul. Bukowinska 24d/7 02-703 Warszawa, Polska',
      name: 'First company Ltd.',
      pensionInsurance: 1111.11,
      healthInsurance: 111.11,
    },
    {
      taxIdentificationNumber: '222-222-22-22',
      address: 'ul. Bukowinska 24d/7 02-703 Warszawa, Polska',
      name: 'Second company Ltd.',
      pensionInsurance: 2222.22,
      healthInsurance: 222.22,
    },
  ];
}
