import { CompanyPage } from './company.po';
import { browser, ExpectedConditions } from 'protractor';

describe('Company page E2E test', () => {
  let page: CompanyPage;

  beforeEach(async () => {
    page = new CompanyPage();

    await page.navigateTo();

    browser.wait(
      ExpectedConditions.not(
        ExpectedConditions.presenceOf(page.anyCompanyRow())
      )
    );

    expect(await page.companyRows()).toEqual([]);
  });

  it('should display correct values for table headers', async () => {
    expect(await page.taxIdHeaderValue()).toEqual('Tax identification number');
    expect(await page.nameHeaderValue()).toEqual('Name');
    expect(await page.addressHeaderValue()).toEqual('Address');
    expect(await page.pensionInsuranceHeaderValue()).toEqual(
      'Pension insurance'
    );
    expect(await page.healthInsuranceHeaderValue()).toEqual('Health insurance');
  });
});
