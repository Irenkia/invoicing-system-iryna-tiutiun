package pl.futurecollars.invoicing.helpers

import pl.futurecollars.invoicing.model.Car
import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.model.InvoiceEntry
import pl.futurecollars.invoicing.model.Vat
import spock.lang.Specification
import java.time.LocalDate

class TestHelpers extends Specification {

    static buyer(Long id) { //costs
        Company.builder()
                .taxIdentificationNumber("$id")
                .address("ul. Bukowinska 24d/$id 02-703 Warszawa, Polska")
                .name("iCode Trust $id Sp. z o.o")
                .pensionInsurance(BigDecimal.valueOf(id * 626.51))
                .healthInsurance(BigDecimal.valueOf(id * 387.0))
                .build()
    }

    static seller(Long id) { // income
        Company.builder()
                .taxIdentificationNumber("$id")
                .address("ul. Bukowinska 24d/$id 02-703 Warszawa, Polska")
                .name("iCode Trust $id Sp. z o.o")
                .pensionInsurance(BigDecimal.valueOf(id * 626.51))
                .healthInsurance(BigDecimal.valueOf(id * 387.0))
                .build()
    }
    static product(Long id) {
        InvoiceEntry.builder()
                .description("Programming course $id")
                .quantity(1.0)
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

    static invoice(Long id) {
        Invoice.builder()
                .date(LocalDate.now())
                .number("123/4242/43221/$id")
                .buyer(buyer(id))
                .seller(seller(id))
                .entries((1..id).collect({ product(it)}))
                .build()
    }
}
