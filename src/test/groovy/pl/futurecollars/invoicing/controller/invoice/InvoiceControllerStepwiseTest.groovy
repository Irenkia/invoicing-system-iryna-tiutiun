package pl.futurecollars.invoicing.controller.invoice

import pl.futurecollars.invoicing.controller.Requests
import pl.futurecollars.invoicing.helpers.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Shared
import spock.lang.Stepwise
import java.time.LocalDate

@Stepwise
class InvoiceControllerStepwiseTest extends Requests {

    private Invoice originalInvoice = TestHelpers.invoice(1)

    private LocalDate updateDate = LocalDate.of(2024, 03, 30)

    @Shared
    private int invoiceId

    def "empty array is returned when no invoices were created"() {
        when:
        deleteAllInvoices()
        def response = getRequest()

        then:
        response == Collections.emptyList()
    }

    def "add single invoice"() {
        given:
        deleteAllInvoices()
        def invoice = originalInvoice
        def invoiceAsJson = jsonService.toJson(invoice)

        when:
        invoiceId = postRequest(invoiceAsJson)

        then:
        invoiceId == 1
    }

    def "one invoice is returned when getting all invoices"() {
        given:
        def expectedInvoice = originalInvoice
        expectedInvoice.id = 1

        when:
        def response = getRequest()
        def invoiceAsJson = jsonService.toJson(response)
        def invoices = jsonService.toObject(invoiceAsJson, Invoice[])

        then:
        invoices.size() == 1
        invoices[0] == expectedInvoice
    }

    def "invoice is returned correctly when getting by id"() {
        given:
        def expectedInvoice = originalInvoice
        expectedInvoice.id = 1

        when:
        def response = getRequestById(1)
        def invoiceAsJson = jsonService.toJson(response)
        def invoice = jsonService.toObject(invoiceAsJson, Invoice)

        then:
        invoice == expectedInvoice
    }

    def "when getting invoice by no exist id"() {
        when:
        def response = getRequestById(10)

        then:
        response == null
    }

    def "invoice date can be modified"() {
        given:
        def modifiedInvoice = originalInvoice
        modifiedInvoice.id = 1
        modifiedInvoice.date = updateDate

        when:
        putRequestById(1, jsonService.toJson(modifiedInvoice))

        then:
        def response = getRequestById(1)
        def invoiceAsJson = jsonService.toJson(response)
        def invoice = jsonService.toObject(invoiceAsJson, Invoice)

        invoice == modifiedInvoice
    }

    def "updated invoice is returned correctly when getting by id"() {
        given:
        def expectedInvoice = originalInvoice
        expectedInvoice.id = 1
        expectedInvoice.date = updateDate

        when:
        def response = getRequestById(1)
        def invoiceAsJson = jsonService.toJson(response)
        def invoice = jsonService.toObject(invoiceAsJson, Invoice)

        then:
        invoice == expectedInvoice
    }

    def "invoice can be deleted from exist id"() {
        when:
        def response = deleteRequestById(1)

        then:
        response == null

        expect:
        def expected = getRequestById(1)
        expected == null
    }

    def "invoice can be deleted from no exist id"() {
        when:
        def response = deleteRequestById(10)

        then:
        response == null

        deleteAllInvoices()
    }

}
