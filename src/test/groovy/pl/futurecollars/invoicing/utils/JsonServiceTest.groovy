package pl.futurecollars.invoicing.utils

import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Specification
import pl.futurecollars.invoicing.helpers.TestHelpers

class JsonServiceTest extends Specification {

    def "can convert object to json and read it back"() {
        given:
        def jsonService = new JsonService()
        def invoice = TestHelpers.invoice(12)

        when:
        def invoiceAsString = jsonService.toJson(invoice)

        and:
        def invoiceFromJson = jsonService.toObject(invoiceAsString, Invoice.class)

        then:
        invoice == invoiceFromJson
    }
}
