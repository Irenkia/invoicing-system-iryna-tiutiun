package pl.futurecollars.invoicing.controller.tax

import pl.futurecollars.invoicing.controller.Requests
import pl.futurecollars.invoicing.helpers.TestHelpers

class TaxCalculatorControllerTest extends Requests {

    def setup() {
        deleteAllInvoices()
    }

    def "zeros are returned when there are no invoices in the system"() {
        when:
        def taxCalculatorResponse = calculateTaxes("0")

        then:
        taxCalculatorResponse.income == 0
        taxCalculatorResponse.costs == 0

        taxCalculatorResponse.collectedVat == 0
        taxCalculatorResponse.paidVat == 0
        taxCalculatorResponse.vatToReturn == 0
    }

    def "zeros are returned when tax id is not matching"() {
        when:
        def taxCalculatorResponse = calculateTaxes("no_match")

        then:
        taxCalculatorResponse.income == 0
        taxCalculatorResponse.costs == 0

        taxCalculatorResponse.collectedVat == 0
        taxCalculatorResponse.paidVat == 0
        taxCalculatorResponse.vatToReturn == 0
    }

    def "sum of all products is returned when tax id is matching"() {
        given: "added 3 invoices"
        def invoice1 = TestHelpers.invoice(1)
        def id1 = postRequest(jsonService.toJson(invoice1))

        def invoice2 = TestHelpers.invoice(2)
        def id2 = postRequest(jsonService.toJson(invoice2))

        def invoice3 = TestHelpers.invoice(3)
        def id3 = postRequest(jsonService.toJson(invoice3))

        when:
        def taxCalculatorResponse1 = calculateTaxes("1")

        then:
        taxCalculatorResponse1.income == 1000.0
        taxCalculatorResponse1.costs == 1000.0

        taxCalculatorResponse1.collectedVat == 80.0
        taxCalculatorResponse1.paidVat == 80.0
        taxCalculatorResponse1.vatToReturn == 0.0

        when:
        def taxCalculatorResponse2 = calculateTaxes("2")

        then:
        taxCalculatorResponse2.income == 1000.0 + 2000.0
        taxCalculatorResponse2.costs == 1000.0 + 2000.0

        taxCalculatorResponse2.collectedVat == 80.0 + 160.0
        taxCalculatorResponse2.paidVat == 80.0 + 160.0
        taxCalculatorResponse2.vatToReturn == 0.0

        when:
        def taxCalculatorResponse3 = calculateTaxes("3")

        then:
        taxCalculatorResponse3.income == (1000.0 + 2000.0) + 3000.0
        taxCalculatorResponse3.costs == (1000.0 + 2000.0) + 3000.0

        taxCalculatorResponse3.collectedVat == (80.0 + 160.0) + 240.0
        taxCalculatorResponse3.paidVat == (80.0 + 160.0) + 240.0
        taxCalculatorResponse3.vatToReturn == 0.0
    }

    def "correct values are returned when company was buyer and seller"() {
        given: "added 3 invoices"// sellers: 1-3, buyers: 1-3, 1-3 overlapping
        def invoice1 = TestHelpers.invoice(1)
        def id1 = postRequest(jsonService.toJson(invoice1))

        def invoice2 = TestHelpers.invoice(2)
        def id2 = postRequest(jsonService.toJson(invoice2))

        def invoice3 = TestHelpers.invoice(3)
        def id3 = postRequest(jsonService.toJson(invoice3))

        when:
        def taxCalculatorResponse = calculateTaxes("3")

        then:
        taxCalculatorResponse.income == 6000
        taxCalculatorResponse.costs == 6000

        taxCalculatorResponse.collectedVat == 480.0
        taxCalculatorResponse.paidVat == 480.0
        taxCalculatorResponse.vatToReturn == 0.0
    }
}
