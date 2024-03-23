package pl.futurecollars.invoicing.db.file

import pl.futurecollars.invoicing.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.utils.FilesService
import pl.futurecollars.invoicing.utils.JsonService
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class FileBasedDatabaseTest extends Specification {

    Path idPath = File.createTempFile('ids', '.txt').toPath()
    Path dbPath = File.createTempFile('invoices', '.json').toPath()
    def idService = new IdService(idPath, new FilesService())
    def filesService = new FilesService()
    def jsonService = new JsonService()

    FileBasedDatabase db = new FileBasedDatabase(dbPath, idService, filesService, jsonService, Invoice as Class)

    def invoice1 = TestHelpers.invoice(1)
    def invoice2 = TestHelpers.invoice(2)
    def invoice3 = TestHelpers.invoice(3)

    def "file based database writes invoices to correct file"() {
        given:
        db.save(invoice1)

        expect:
        Files.readAllLines(idPath).size() == 1
        [jsonService.toJson(invoice1)] == Files.readAllLines(dbPath)

        when:
        db.save(invoice2)

        then:
        Files.readAllLines(dbPath).size() == 2
        [jsonService.toJson(invoice1), jsonService.toJson(invoice2)] == Files.readAllLines(dbPath)

        when:
        db.save(invoice3)

        then:
        Files.readAllLines(dbPath).size() == 3
        [jsonService.toJson(invoice1), jsonService.toJson(invoice2), jsonService.toJson(invoice3)] == Files.readAllLines(dbPath)
    }

    def "get by id returns expected invoice"() {
        given:
        db.save(invoice1)
        db.save(invoice2)
        db.save(invoice3)

        when:
        def ids1 = db.getById(1)

        then:
        def expectedInvoice1 = invoice1.toString()
        def invoiceFromDb1 = db.getById(1).get().toString()

        assert invoiceFromDb1 == expectedInvoice1

        when:
        def ids2 = db.getById(2)

        then:
        def expectedInvoice2 = invoice2.toString()
        def invoiceFromDb2 = db.getById(2).get().toString()

        assert invoiceFromDb2 == expectedInvoice2

        when:
        def ids3 = db.getById(3)

        then:
        def expectedInvoice3 = invoice3.toString()
        def invoiceFromDb3 = db.getById(3).get().toString()

        assert invoiceFromDb3 == expectedInvoice3
    }

    def "get by id returns empty optional when there is no invoice with given id"() {
        expect:
        !db.getById(1).isPresent()
    }

    def "get all returns empty collection if there were no invoices"() {
        expect:
        db.getAll().isEmpty()
    }

    def "get all returns all invoices in the database, deleted invoice is not returned"() {
        given:
        db.save(invoice1)
        db.save(invoice2)
        db.save(invoice3)

        expect:
        def invoiceAsString = db.getAll()
        List<Invoice> expectedInvoices = List.of(invoice1, invoice2, invoice3)

        assert invoiceAsString == expectedInvoices
    }

    def "it's possible to update the invoice, original invoice is returned"() {
        given:
        List<Invoice> invoices = (1..12).collect { TestHelpers.invoice(it) };
        def originalInvoice = invoices.get(0)
        originalInvoice.id = db.save(originalInvoice)

        def expectedInvoice = invoices.get(1)
        expectedInvoice.id = originalInvoice.id

        and:
        def invoiceBeforeUpdateAsString = db.getById(1).toString()
        def expectedInvoiceBeforeUpdateAsString = originalInvoice.toString()
        invoiceBeforeUpdateAsString == expectedInvoiceBeforeUpdateAsString

        when:
        db.update(originalInvoice.id, expectedInvoice)

        then:
        def invoiceAfterUpdate = db.getById(originalInvoice.id).get()
        def invoiceAfterUpdateAsString = invoiceAfterUpdate.toString()
        def expectedInvoiceAfterUpdateAsString = expectedInvoice.toString()
        invoiceAfterUpdateAsString == expectedInvoiceAfterUpdateAsString
    }

    def "updating not existing invoice returns throw new IllegalArgumentException"() {
        given:
        def id = 213

        when:
        db.update(id, invoice1)

        then:
        thrown(IllegalArgumentException)
    }

    def "deleted invoice is not returned"() {
        given:
        db.save(invoice1)
        db.save(invoice2)
        db.save(invoice3)
        List<Invoice> expectedInvoices = List.of(invoice1, invoice2, invoice3)

        when:
        def firstInvoiceId = db.getAll().get(0).getId()
        db.delete(firstInvoiceId)

        then:
        db.getAll().size() == expectedInvoices.size() - 1

        when:
        def secondInvoiceId = db.getAll().get(0).getId()
        db.delete(secondInvoiceId)

        then:
        db.getAll().size() == expectedInvoices.size() - 2

        when:
        def thirdInvoiceId = db.getAll().get(0).getId()
        db.delete(thirdInvoiceId)

        then:
        db.getAll().size() == expectedInvoices.size() - 3
    }

    def "deleting not existing invoice returns optional empty"() {
        expect:
        db.delete(123) == null
    }
}
