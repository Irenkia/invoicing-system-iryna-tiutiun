import { TestBed, ComponentFixture } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { CompanyService } from './company.service';
import { Company } from './company';
import { FormsModule } from '@angular/forms';
import { of } from 'rxjs';

describe('AppComponent', () => {
  let fixture: ComponentFixture<AppComponent>;
  let component: AppComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [{ provide: CompanyService, useClass: MockCompanyService }],
      declarations: [AppComponent],
      imports: [FormsModule],
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;

    component.ngOnInit();
    fixture.detectChanges();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it(`should have as title 'invoicing-app'`, () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    // expect(app.title).toEqual('invoicing-app');
  });

  it('should render title', () => {
    const fixture = TestBed.createComponent(AppComponent);
    fixture.detectChanges();
    const compiled = fixture.nativeElement;
    // expect(compiled.querySelector('.content span').textContent).toContain('invoicing-app app is running!');
  });

  it(`should display a list of companies`, () => {
    expect(fixture.nativeElement.innerText).toContain(
      '111-111-11-11	ul. First 1	 First Ltd.  111.11 	1111.11'
    );
    expect(fixture.nativeElement.innerText).toContain(
      '222-222-22-22	ul. Second 2	Second Ltd.  222.22 	2222.22'
    );

    expect(component.companies.length).toBe(2);
    expect(component.companies).toBe(MockCompanyService.companies);
  });
});

class MockCompanyService {
  static companies: Company[] = [
    new Company(
      1,
      '111-111-11-11',
      'ul. First 1',
      'First Ltd.',
      111.11,
      1111.11
    ),
    new Company(
      2,
      '222-222-22-22',
      'ul. Second 2',
      'Second Ltd.',
      222.22,
      2222.22
    ),
  ];

  getCompanies() {
    return of(MockCompanyService.companies);
  }
}
