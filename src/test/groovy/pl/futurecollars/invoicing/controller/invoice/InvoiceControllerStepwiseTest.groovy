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
        invoiceId > 0
    }

    def "one invoice is returned when getting all invoices"() {
        given:
        def expectedInvoice = originalInvoice
        expectedInvoice.id = 1
        def invoiceAsJson = jsonService.toJson(expectedInvoice)
        invoiceId = postRequest(invoiceAsJson)

        when:
        def response = getRequest()
        def invoiceAsJsonResponse = jsonService.toJson(response)
        def invoices = jsonService.toObject(invoiceAsJsonResponse, Invoice[])

        then:
        invoices.size() == 1
    }

    def "invoice is returned correctly when getting by id"() {
        given:
        def expectedInvoice = originalInvoice
        expectedInvoice.id = invoiceId
        def invoiceAsJson = jsonService.toJson(expectedInvoice)
        invoiceId = postRequest(invoiceAsJson)

        when:
        def response = getRequestById(invoiceId)
        def invoiceAsJsonResponse = jsonService.toJson(response)
        def invoice = jsonService.toObject(invoiceAsJsonResponse, Invoice)

        then:
        resetIds(invoice)
        resetIds(expectedInvoice)
        response.date.toString() == expectedInvoice.date.toString()
        response.number.toString() == expectedInvoice.number.toString()
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
        modifiedInvoice.id = invoiceId
        def invoiceAsJson = jsonService.toJson(modifiedInvoice)
        invoiceId = postRequest(invoiceAsJson)
        modifiedInvoice.date = updateDate

        when:
        putRequestById(invoiceId, jsonService.toJson(modifiedInvoice))

        then:
        def response = getRequestById(invoiceId)
        def invoiceAsJsonResponse = jsonService.toJson(response)
        def invoice = jsonService.toObject(invoiceAsJsonResponse, Invoice)

        resetIds(invoice)
        resetIds(modifiedInvoice)
        invoice.date.toString() == modifiedInvoice.date.toString()
        invoice.number.toString() == modifiedInvoice.number.toString()
    }

    def "updated invoice is returned correctly when getting by id"() {
        given:
        def expectedInvoice = originalInvoice
        expectedInvoice.id = invoiceId
        def invoiceAsJson = jsonService.toJson(expectedInvoice)
        invoiceId = postRequest(invoiceAsJson)
        expectedInvoice.date = updateDate

        when:
        def response = getRequestById(invoiceId)
        def invoiceAsJsonResponse = jsonService.toJson(response)
        def invoice = jsonService.toObject(invoiceAsJsonResponse, Invoice)

        then:
        resetIds(invoice)
        resetIds(expectedInvoice)
        response.number.toString() == expectedInvoice.number.toString()
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

    def "invoice can't be deleted from no exist id"() {
        when:
        def response = deleteRequestById(10)

        then:
        response == null

        deleteAllInvoices()
    }

    // resetting is necessary because database query returns ids while we don't know ids in original invoice
    def Invoice resetIds(Invoice invoice) {
        invoice.id = null
        invoice.getBuyer().id = null
        invoice.getSeller().id = null
        invoice.entries.forEach {
            it.id = null
        }
        invoice
    }

}
