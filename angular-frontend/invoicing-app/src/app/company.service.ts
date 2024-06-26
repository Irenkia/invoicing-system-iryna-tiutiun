import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../environments/environment';
import { Company } from './company';

const PATH = 'companies';

@Injectable({
  providedIn: 'root',
})
export class CompanyService {
  constructor(private http: HttpClient) {}

  private options = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
    withCredentials: true,
  };

  private apiUrl(service: string, id: number = 0): string {
    const idInUrl = id !== 0 ? '/' + id : '';

    return environment.apiUrl + '/' + service + idInUrl;
  }

  private toCompanyRequest(company: Company) {
    return {
      taxIdentificationNumber: company.taxIdentificationNumber,
      name: company.name,
      address: company.address,
      pensionInsurance: company.pensionInsurance,
      healthInsurance: company.healthInsurance,
    };
  }

  getCompanies(): Observable<Company[]> {
    return this.http.get<Company[]>(this.apiUrl(PATH));
  }

  addCompany(company: Company): Observable<any> {
    return this.http.post<any>(
      this.apiUrl(PATH),
      this.toCompanyRequest(company),
      this.options
    );
  }

  editCompany(company: Company): Observable<any> {
    return this.http.put<Company>(
      this.apiUrl(PATH, company.id),
      this.toCompanyRequest(company),
      this.options
    );
  }

  deleteCompany(id: number): Observable<any> {
    return this.http.delete<any>(this.apiUrl(PATH, id));
  }
}
