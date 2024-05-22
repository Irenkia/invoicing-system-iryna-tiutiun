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

  newCompany: Company = new Company("", "", "", 0, 0);

  companies: Company[] = [
      new Company(
          '111-111-11-11',
          'ul. Bukowinska 24d/7 02-703 Warszawa, Polska',
          'First company Ltd.',
          1111.11,
          111.11
      ),
      new Company(
          '222-222-22-22',
          'ul. Bukowinska 24d/7 02-703 Warszawa, Polska',
          'Second company Ltd.',
          2222.22,
          222.22
      )
  ];

  addCompany() {
      this.companies.push(this.newCompany);
      this.newCompany = new Company(0, "", "", "", 0, 0);
  }
}
