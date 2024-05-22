export class Company{

  constructor(
    public taxIdentificationNumber: string,
    public address: string,
    public name: string,
    public healthInsurance: number,
    public pensionInsurance: number
  ){
  }

}

