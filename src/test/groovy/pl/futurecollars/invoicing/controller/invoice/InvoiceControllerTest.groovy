package pl.futurecollars.invoicing.controller.invoice

import pl.futurecollars.invoicing.controller.Requests
import pl.futurecollars.invoicing.helpers.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Shared

import java.time.LocalDate

class InvoiceControllerTest extends Requests {

    @Shared
    private int invoiceId

    private LocalDate updateDate = LocalDate.of(2024, 03, 30)

    def setup() {
        deleteAllInvoices()
    }

    def "empty array is returned when no invoices were created"() {
        given:
        def invoices = Collections.emptyList()

        when:
        def response = getRequest()

        then:
        response == invoices
    }

    def "add single invoice"() {
        given:
        def invoice = TestHelpers.invoice(1)
        def invoiceAsJson = jsonService.toJson(invoice)

        when:
        invoiceId = postRequest(invoiceAsJson)

        then:
        getRequestById(invoiceId) != null
    }

    def "one invoice is returned when getting all invoices"(){
        given:
        def expectedInvoice = TestHelpers.invoice(1)
        invoiceId = postRequest(jsonService.toJson(expectedInvoice))

        when:
        def response = getRequest()

        then:
        response.size() == List.of(expectedInvoice).size()
    }

    def "invoice is returned correctly when getting by id"() {
        given:
        def expectedInvoice = TestHelpers.invoice(1)
        invoiceId = postRequest(jsonService.toJson(expectedInvoice))

        when:
        def response = getRequestById(invoiceId)

        then:
        resetIds(response)
        response.date.toString() == expectedInvoice.date.toString()
        response.number.toString() == expectedInvoice.number.toString()
    }

    def "when getting invoice by no exist id"(){
        given:
        invoiceId = 25

        when:
        def response = getRequestById(invoiceId)

        then:
        response == null
    }

    def "invoice date can be modified"() {
        given:
        def modifiedInvoice = TestHelpers.invoice(1)
        modifiedInvoice.date = updateDate
        def invoiceAsJson = jsonService.toJson(modifiedInvoice)
        invoiceId = postRequest(invoiceAsJson)

        when:
        putRequestById(invoiceId, invoiceAsJson)

        then: "updated invoice is returned correctly when getting by id"
        def response = getRequestById(invoiceId)
        response.date == modifiedInvoice.date
    }

    def "when invoice can't be modified by no exist id"() {
        given:
        def modifiedInvoice = TestHelpers.invoice(1)
        modifiedInvoice.date = updateDate
        def invoiceAsJson =jsonService.toJson(modifiedInvoice)
        invoiceId = postRequest(invoiceAsJson)
        invoiceId = 25

        when:
        def response = putRequestById(invoiceId, invoiceAsJson)

        then:
        response == null
    }

    def "invoice can be deleted from exist id"() {
        given: "one invoice is returned correctly when getting all invoices"
        def invoice = TestHelpers.invoice(1)
        invoiceId = postRequest(jsonService.toJson(invoice))
        def response = getRequest()
        response == List.of(invoice)

        when: "invoice deleted from exist id"
        deleteRequestById(invoiceId)

        then:
        def expectedResponse = getRequest()
        expectedResponse == Collections.emptyList()
    }

    def "invoice can't deleted from no exist id"() {
        given:
        def invoice = TestHelpers.invoice(1)
        invoiceId = postRequest(jsonService.toJson(invoice))
        invoiceId = 10

        expect:
        deleteRequestById(invoiceId) == null
    }

    def "when added 3 invoices, then second can be deleted and 2 invoices left"() {
        given: "added 3 invoices"
        def invoice1 = TestHelpers.invoice(1)
        def invoiceId1 = postRequest(jsonService.toJson(invoice1))

        def invoice2 = TestHelpers.invoice(2)
        def invoiceId2 = postRequest(jsonService.toJson(invoice2))

        def invoice3 = TestHelpers.invoice(3)
        def invoiceId3 = postRequest(jsonService.toJson(invoice3))

        def response = getRequest()
        response.size() == List.of(invoice1, invoice2, invoice3).size()

        when: "second can be deleted"
        deleteRequestById(invoiceId2)

        then: "2 invoices left"
        def expectedResponse1 = getRequest()
        expectedResponse1.size() == List.of(invoice1, invoice3).size()

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
