package pl.futurecollars.invoicing.db

import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Specification
import static pl.futurecollars.invoicing.helpers.TestHelpers.invoice

abstract class AbstractDatabaseTest extends Specification {

    List<Invoice> invoices = (1..12).collect { invoice(it) }

    abstract Database getDatabaseInstance()
    Database database

    def setup() {
        database = getDatabaseInstance()
        database.reset()

        assert database.getAll().isEmpty()
    }

    def "should save invoices returning sequential id, invoice should have id set to correct value, get by id returns saved invoice"() {
        when:
        def ids = invoices.collect { it.id = database.save(it) }

        then:
        ids == (1L..invoices.size()).collect()
        ids.forEach { assert database.getById(it).isPresent() }
        ids.forEach { assert database.getById(it).get().getId() == it }
    }

    def "get by id returns empty optional when there is no invoice with given id"() {
        expect:
        !database.getById(1).isPresent()
    }

    def "get all returns empty collection if there were no invoices"() {
        expect:
        database.getAll().isEmpty()
    }

    def "get all returns all invoices in the database, deleted invoice is not returned"() {
        given:
        invoices.forEach { it.id = database.save(it) }

        expect:
        database.getAll().size() == invoices.size()

        when:
        database.delete(1)

        then:
        database.getAll().size() == invoices.size() - 1
        database.getAll().forEach { assert it.getId() != 1 }
    }

    def "can delete all invoices"() {
        given:
        invoices.forEach { it.id = database.save(it) }

        when:
        invoices.forEach { database.delete(it.getId()) }

        then:
        database.getAll().isEmpty()
    }

    def "deleting not existing invoice returns optional empty"() {
        expect:
        database.delete(123) == Optional.empty()
    }

    def "it's possible to update the invoice, original invoice is returned"() {
        given:
        def originalInvoice = invoices.get(0)
        originalInvoice.id = database.save(originalInvoice)

        def expectedInvoice = invoices.get(1)
        expectedInvoice.id = originalInvoice.id

        when:
        def result = database.update(originalInvoice.id, expectedInvoice)

        then:
        def invoiceAfterUpdate = database.getById(originalInvoice.id).get()
    }

    def "updating not existing invoice returns Optional.empty()"() {
        expect:
        database.update(213, invoices.get(1)) == Optional.empty()
    }

}

