package pl.futurecollars.invoicing.controller.invoice

import pl.futurecollars.invoicing.controller.AbstractControllerTest
import pl.futurecollars.invoicing.helpers.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Shared
import spock.lang.Stepwise
import java.time.LocalDate

@Stepwise
class InvoiceControllerStepwiseTest extends AbstractControllerTest {

    private Invoice originalInvoice = TestHelpers.invoice(1)

    private LocalDate updateDate = LocalDate.of(2024, 03, 30)

    @Shared
    private Long invoiceId

    def "empty array is returned when no invoices were created"() {
        when:
        deleteAllInvoices()
        def response = getAllInvoices()

        then:
        response == Collections.emptyList()
    }

    def "add single invoice"() {
        given:
        deleteAllInvoices()
        def invoice = originalInvoice

        when:
        invoiceId = addInvoiceAndReturnId(invoice)

        then:
        invoiceId > 0
    }

    def "one invoice is returned when getting all invoices"() {
        given:
        def expectedInvoice = originalInvoice
        expectedInvoice.id = 1
        invoiceId = addInvoiceAndReturnId(expectedInvoice)

        when:
        def response = getAllInvoices()

        then:
        response.size() == 1
    }

    def "invoice is returned correctly when getting by id"() {
        given:
        def expectedInvoice = originalInvoice
        expectedInvoice.id = invoiceId
        invoiceId = addInvoiceAndReturnId(expectedInvoice)

        when:
        def response = getInvoiceById(invoiceId)

        then:
        resetIds(response)
        resetIds(expectedInvoice)
        response.date.toString() == expectedInvoice.date.toString()
        response.number.toString() == expectedInvoice.number.toString()
    }

    def "when getting invoice by no exist id"() {
        when:
        def response = getInvoiceById(10)

        then:
        response == null
    }

    def "invoice date can be modified"() {
        given:
        def modifiedInvoice = originalInvoice
        invoiceId = addInvoiceAndReturnId(modifiedInvoice)
        modifiedInvoice.id = invoiceId
        modifiedInvoice.date = updateDate

        when:
        updateInvoiceById(invoiceId, modifiedInvoice)

        then:
        def response = getInvoiceById(invoiceId)
        resetIds(response)
        resetIds(modifiedInvoice)
        response.date.toString() == modifiedInvoice.date.toString()
        response.number.toString() == modifiedInvoice.number.toString()
    }

    def "invoice can be deleted from exist id"() {
        when:
        def response = deleteInvoiceById(1)

        then:
        response == null

        expect:
        def expected = getInvoiceById(1)
        expected == null
    }

    def "invoice can't be deleted from no exist id"() {
        when:
        def response = deleteInvoiceById(10)

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
        return invoice
    }

}
