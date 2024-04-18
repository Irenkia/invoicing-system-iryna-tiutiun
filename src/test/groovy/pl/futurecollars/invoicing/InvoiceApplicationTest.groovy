package pl.futurecollars.invoicing

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pl.futurecollars.invoicing.service.InvoiceService
import pl.futurecollars.invoicing.service.TaxCalculatorService
import spock.lang.Specification

@SpringBootTest
class InvoiceApplicationTest extends Specification {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    TaxCalculatorService taxCalculatorService

    def "invoice service id created"() {
        expect:
        invoiceService
    }

    def "tax calculator service id created"() {
        expect:
        taxCalculatorService
    }
}
