import {
  browser,
  by,
  element,
  ElementFinder,
  ElementArrayFinder,
  WebElement,
} from 'protractor';

export class CompanyPage {
  async navigateTo(): Promise<unknown> {
    return browser.get(browser.baseUrl);
  }

  async taxIdHeaderValue(): Promise<string> {
    return element(by.id('taxIdHeader')).getText();
  }

  async nameHeaderValue(): Promise<string> {
    return element(by.id('nameHeader')).getText();
  }

  async addressHeaderValue(): Promise<string> {
    return element(by.id('addressHeader')).getText();
  }

  async pensionInsuranceHeaderValue(): Promise<string> {
    return element(by.id('pensionInsuranceHeader')).getText();
  }

  async healthInsuranceHeaderValue(): Promise<string> {
    return element(by.id('healthInsuranceHeader')).getText();
  }

  companyRows(): ElementArrayFinder {
    return element.all(by.css('.companyRow'));
  }

  anyCompanyRow(): ElementFinder {
    return element(by.css('.companyRow'));
  }

  deleteBtn(row: ElementFinder): WebElement {
    return row.element(by.css('.btn-danger'));
  }
}
