package pl.futurecollars.invoicing.helpers

import pl.futurecollars.invoicing.model.Car
import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.model.InvoiceEntry
import pl.futurecollars.invoicing.model.Vat
import spock.lang.Specification
import java.time.LocalDate

class TestHelpers extends Specification {

    static buyer(int id) { //costs
        Company.builder()
                .taxIdentificationNumber("$id")
                .address("ul. Bukowinska 24d/$id 02-703 Warszawa, Polska")
                .name("iCode Trust $id Sp. z o.o")
                .pensionInsurance(BigDecimal.valueOf(id * 626.51))
                .healthInsurance(BigDecimal.valueOf(id * 387.00))
                .build()
    }

    static seller(int id) { // income
        Company.builder()
                .taxIdentificationNumber("$id")
                .address("ul. Bukowinska 24d/$id 02-703 Warszawa, Polska")
                .name("iCode Trust $id Sp. z o.o")
                .pensionInsurance(BigDecimal.valueOf(id * 626.51))
                .healthInsurance(BigDecimal.valueOf(id * 387.00))
                .build()
    }
    static product(int id) {
        InvoiceEntry.builder()
                .description("Programming course $id")
                .quantity(1)
                .netPrice(BigDecimal.valueOf(id * 8100))
                .vatValue(BigDecimal.valueOf(id * 1900))
                .vatRate(Vat.VAT_19)
                .expenseRelatedToCar(
                        Car.builder()
                        .registrationNumber("XY 000$id")
                        .personalUse(true)
                        .build()
                )
                .build()
    }

    static invoice(int id) {
        Invoice.builder()
                .date(LocalDate.now())
                .buyer(buyer(id))
                .seller(seller(id))
                .entries((1..id).collect({ product(it)}))
                .build()
    }
}
