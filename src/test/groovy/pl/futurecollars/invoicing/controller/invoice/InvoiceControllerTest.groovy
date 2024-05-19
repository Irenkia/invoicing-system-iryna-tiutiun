package pl.futurecollars.invoicing.controller.invoice

import pl.futurecollars.invoicing.controller.AbstractControllerTest
import pl.futurecollars.invoicing.helpers.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Shared

import java.time.LocalDate

class InvoiceControllerTest extends AbstractControllerTest {

    @Shared
    private Long invoiceId

    private LocalDate updateDate = LocalDate.of(2024, 03, 30)

    def setup() {
        deleteAllInvoices()
    }

    def "empty array is returned when no invoices were created"() {
        given:
        def invoices = Collections.emptyList()

        when:
        def response = getAllInvoices()

        then:
        response == invoices
    }

    def "add single invoice"() {
        given:
        def invoice = TestHelpers.invoice(1)

        when:
        invoiceId = addInvoiceAndReturnId(invoice)

        then:
        getInvoiceById(invoiceId) != null
    }

    def "one invoice is returned when getting all invoices"(){
        given:
        def expectedInvoice = TestHelpers.invoice(1)
        invoiceId = addInvoiceAndReturnId(expectedInvoice)

        when:
        def response = getAllInvoices()

        then:
        response.size() == List.of(expectedInvoice).size()
    }

    def "invoice is returned correctly when getting by id"() {
        given:
        def expectedInvoice = TestHelpers.invoice(1)
        invoiceId = addInvoiceAndReturnId(expectedInvoice)

        when:
        def response = getInvoiceById(invoiceId)

        then:
        resetIds(response)
        response.date.toString() == expectedInvoice.date.toString()
        response.number.toString() == expectedInvoice.number.toString()
    }

    def "when getting invoice by no exist id"(){
        given:
        invoiceId = 25

        when:
        def response = getInvoiceById(invoiceId)

        then:
        response == null
    }

    def "invoice date can be modified"() {
        given:
        def modifiedInvoice = TestHelpers.invoice(1)
        invoiceId = addInvoiceAndReturnId(modifiedInvoice)
        modifiedInvoice.date = updateDate

        when:
        updateInvoiceById(invoiceId, modifiedInvoice)

        then: "updated invoice is returned correctly when getting by id"
        def response = getInvoiceById(invoiceId)
        response.date == modifiedInvoice.date
    }

    def "when invoice can't be modified by no exist id"() {
        given:
        def modifiedInvoice = TestHelpers.invoice(1)
        modifiedInvoice.date = updateDate
        invoiceId = addInvoiceAndReturnId(modifiedInvoice)
        invoiceId = 25

        when:
        def response = updateInvoiceById(invoiceId, modifiedInvoice)

        then:
        response == null
    }

    def "invoice can be deleted from exist id"() {
        given: "one invoice is returned correctly when getting all invoices"
        def invoice = TestHelpers.invoice(1)
        invoiceId = addInvoiceAndReturnId(invoice)
        def response = getAllInvoices()
        response == List.of(invoice)

        when: "invoice deleted from exist id"
        deleteInvoiceById(invoiceId)

        then:
        def expectedResponse = getAllInvoices()
        expectedResponse == Collections.emptyList()
    }

    def "invoice can't deleted from no exist id"() {
        given:
        def invoice = TestHelpers.invoice(1)
        invoiceId = addInvoiceAndReturnId(invoice)
        invoiceId = 10

        expect:
        deleteInvoiceById(invoiceId) == null
    }

    def "when added 3 invoices, then second can be deleted and 2 invoices left"() {
        given: "added 3 invoices"
        def invoice1 = TestHelpers.invoice(1)
        def invoiceId1 = addInvoiceAndReturnId(invoice1)

        def invoice2 = TestHelpers.invoice(2)
        def invoiceId2 = addInvoiceAndReturnId(invoice2)

        def invoice3 = TestHelpers.invoice(3)
        def invoiceId3 = addInvoiceAndReturnId(invoice3)

        def response = getAllInvoices()
        response.size() == List.of(invoice1, invoice2, invoice3).size()

        when: "second can be deleted"
        deleteInvoiceById(invoiceId2)

        then: "2 invoices left"
        def expectedResponse1 = getAllInvoices()
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
        return invoice
    }

}
