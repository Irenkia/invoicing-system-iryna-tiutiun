package pl.futurecollars.invoicing

import spock.lang.Specification

class InvoiceApplicationTest extends Specification {

    def "must test to cover main"() {
        setup:
        def app = new InvoiceApplication()

        and:
        app.main()
    }
}
