const loadCompanies = async () => {
    const response = await fetch('http://localhost:7777/companies');
    const companies = await response.json();

    const companiesTable = document.getElementById("companiesTable")
    companies.forEach(company => {
        const row = companiesTable.insertRow(-1);

        const taxIdCell = row.insertCell(0);
        taxIdCell.innerText = company.taxIdentificationNumber;

        const addressCell = row.insertCell(1);
        addressCell.innerText = company.address;

        const nameCell = row.insertCell(2);
        nameCell.innerText = company.name;

        const pensionInsuranceCell = row.insertCell(3);
        pensionInsuranceCell.innerText = company.pensionInsurance;

        const healthInsuranceCell = row.insertCell(4);
        healthInsuranceCell.innerText = company.healthInsurance;
    })
}

window.onload = function () {
    loadCompanies();
};