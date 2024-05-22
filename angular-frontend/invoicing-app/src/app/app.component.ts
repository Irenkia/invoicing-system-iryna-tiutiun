import { Component } from '@angular/core';
import { Company } from './company';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  title = 'invoicing-app';

  newCompany: Company = new Company(0, '', '', '', 0, 0);

  companies: Company[] = [
    new Company(
      1,
      '111-111-11-11',
      'ul. Bukowinska 24d/7 02-703 Warszawa, Polska',
      'First company Ltd.',
      1111.11,
      111.11
    ),
    new Company(
      2,
      '222-222-22-22',
      'ul. Bukowinska 24d/7 02-703 Warszawa, Polska',
      'Second company Ltd.',
      2222.22,
      222.22
    ),
  ];

  addCompany() {
    this.companies.push(this.newCompany);
    this.newCompany = new Company(0, '', '', '', 0, 0);
  }

  triggerUpdate(company: Company) {
    company.editedCompany = new Company(
      company.id,
      company.taxIdentificationNumber,
      company.address,
      company.name,
      company.healthInsurance,
      company.pensionInsurance
    );
    company.editMode = true;
  }

  cancelCompanyUpdate(company: Company) {
    company.editMode = false;
  }

  updateCompany(updatedCompany: Company) {
    updatedCompany.taxIdentificationNumber =
      updatedCompany.editedCompany.taxIdentificationNumber;
    updatedCompany.address = updatedCompany.editedCompany.address;
    updatedCompany.name = updatedCompany.editedCompany.name;
    updatedCompany.healthInsurance =
      updatedCompany.editedCompany.healthInsurance;
    updatedCompany.pensionInsurance =
      updatedCompany.editedCompany.pensionInsurance;

    updatedCompany.editMode = false;
  }

  deleteCompany(companyToDelete: Company) {
    this.companies = this.companies.filter(
      (company) => company !== companyToDelete
    );
  }
}
