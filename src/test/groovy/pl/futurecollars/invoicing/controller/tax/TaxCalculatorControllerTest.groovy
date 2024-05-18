package pl.futurecollars.invoicing.controller.tax

import pl.futurecollars.invoicing.controller.AbstractControllerTest
import pl.futurecollars.invoicing.helpers.TestHelpers

class TaxCalculatorControllerTest extends AbstractControllerTest {

    def setup() {
        deleteAllInvoices()
    }

    def "zeros are returned when there are no invoices in the system -> for Buyer"() {
        when:
        def taxCalculatorResponse = calculateTaxes(TestHelpers.buyer(0))

        then:
        taxCalculatorResponse.income == 0
        taxCalculatorResponse.costs == 0

        taxCalculatorResponse.incomeMinusCosts == 0
        taxCalculatorResponse.pensionInsurance == 0
        taxCalculatorResponse.incomeMinusCostsMinusPensionInsurance == 0
        taxCalculatorResponse.incomeMinusCostsMinusPensionInsuranceRounded == 0
        taxCalculatorResponse.incomeTax == 0

        taxCalculatorResponse.healthInsurancePaid == 0
        taxCalculatorResponse.healthInsuranceToSubtract == 0
        taxCalculatorResponse.incomeTaxMinusHealthInsurance == 0
        taxCalculatorResponse.finalIncomeTax == 0

        taxCalculatorResponse.collectedVat == 0
        taxCalculatorResponse.paidVat == 0
        taxCalculatorResponse.vatToReturn == 0
    }

    def "zeros are returned when there are no invoices in the system -> for Seller"() {
        when:
        def taxCalculatorResponse = calculateTaxes(TestHelpers.seller(0))

        then:
        taxCalculatorResponse.income == 0
        taxCalculatorResponse.costs == 0

        taxCalculatorResponse.incomeMinusCosts == 0
        taxCalculatorResponse.pensionInsurance == 0
        taxCalculatorResponse.incomeMinusCostsMinusPensionInsurance == 0
        taxCalculatorResponse.incomeMinusCostsMinusPensionInsuranceRounded == 0
        taxCalculatorResponse.incomeTax == 0

        taxCalculatorResponse.healthInsurancePaid == 0
        taxCalculatorResponse.healthInsuranceToSubtract == 0
        taxCalculatorResponse.incomeTaxMinusHealthInsurance == 0
        taxCalculatorResponse.finalIncomeTax == 0

        taxCalculatorResponse.collectedVat == 0
        taxCalculatorResponse.paidVat == 0
        taxCalculatorResponse.vatToReturn == 0
    }

    def "zeros are returned when tax id is not matching -> for Buyer"() {
        when:
        def taxCalculatorResponse = calculateTaxes(TestHelpers.buyer(-1))

        then:
        taxCalculatorResponse.income == 0
        taxCalculatorResponse.costs == 0

        taxCalculatorResponse.incomeMinusCosts == 0

        taxCalculatorResponse.collectedVat == 0
        taxCalculatorResponse.paidVat == 0
        taxCalculatorResponse.vatToReturn == 0
    }

    def "zeros are returned when tax id is not matching -> for Seller"() {
        when:
        def taxCalculatorResponse = calculateTaxes(TestHelpers.seller(-1))

        then:
        taxCalculatorResponse.income == 0
        taxCalculatorResponse.costs == 0

        taxCalculatorResponse.incomeMinusCosts == 0

        taxCalculatorResponse.collectedVat == 0
        taxCalculatorResponse.paidVat == 0
        taxCalculatorResponse.vatToReturn == 0
    }

    def "sum of all products is returned when tax id is matching and when car is used for personal purposes"() {
        given:
        def invoice1 = TestHelpers.invoice(1)
        def id1 = addInvoiceAndReturnId(invoice1)

        when:
        def taxCalculatorResponse1 = calculateTaxes(TestHelpers.buyer(1))

        then:
        taxCalculatorResponse1.income == 8100.0
        taxCalculatorResponse1.costs == 9050.0

        taxCalculatorResponse1.incomeMinusCosts == -950.0
        taxCalculatorResponse1.pensionInsurance == 626.51
        taxCalculatorResponse1.incomeMinusCostsMinusPensionInsurance == -1576.51
        taxCalculatorResponse1.incomeMinusCostsMinusPensionInsuranceRounded == -1577
        taxCalculatorResponse1.incomeTax == -299.63

        taxCalculatorResponse1.healthInsurancePaid == 387.0
        taxCalculatorResponse1.healthInsuranceToSubtract == 333.25
        taxCalculatorResponse1.incomeTaxMinusHealthInsurance == -632.88
        taxCalculatorResponse1.finalIncomeTax == -632

        taxCalculatorResponse1.collectedVat == 1900.0
        taxCalculatorResponse1.paidVat == 950.0
        taxCalculatorResponse1.vatToReturn == 0.0

        when:
        def taxCalculatorResponse2 = calculateTaxes(TestHelpers.seller(1))

        then:
        taxCalculatorResponse2.income == 8100.0
        taxCalculatorResponse2.costs == 9050.0

        taxCalculatorResponse2.incomeMinusCosts == -950.0
        taxCalculatorResponse2.pensionInsurance == 626.51
        taxCalculatorResponse2.incomeMinusCostsMinusPensionInsurance == -1576.51
        taxCalculatorResponse2.incomeMinusCostsMinusPensionInsuranceRounded == -1577
        taxCalculatorResponse2.incomeTax == -299.63

        taxCalculatorResponse2.healthInsurancePaid == 387.0
        taxCalculatorResponse2.healthInsuranceToSubtract == 333.25
        taxCalculatorResponse2.incomeTaxMinusHealthInsurance == -632.88
        taxCalculatorResponse2.finalIncomeTax == -632

        taxCalculatorResponse2.collectedVat == 1900.0
        taxCalculatorResponse2.paidVat == 950.0
        taxCalculatorResponse2.vatToReturn == 0.0
    }

    def "correct values are returned when company was buyer and seller and when car is used for personal purposes"() {
        given: "added 3 invoices"// sellers: 1-3, buyers: 1-3, 1-3 overlapping
        def invoice1 = TestHelpers.invoice(1)
        def id1 = addInvoiceAndReturnId(invoice1)

        def invoice2 = TestHelpers.invoice(2)
        def id2 = addInvoiceAndReturnId(invoice2)

        def invoice3 = TestHelpers.invoice(3)
        def id3 = addInvoiceAndReturnId(invoice3)

        when:
        def taxCalculatorResponse1 = calculateTaxes(TestHelpers.buyer(3))

        then:
        taxCalculatorResponse1.income == 48600.0
        taxCalculatorResponse1.costs == 54300.0

        taxCalculatorResponse1.incomeMinusCosts == -5700.0
        taxCalculatorResponse1.pensionInsurance == 1879.53
        taxCalculatorResponse1.incomeMinusCostsMinusPensionInsurance == -7579.53
        taxCalculatorResponse1.incomeMinusCostsMinusPensionInsuranceRounded == -7580
        taxCalculatorResponse1.incomeTax == -1440.20

        taxCalculatorResponse1.healthInsurancePaid == 1161.0
        taxCalculatorResponse1.healthInsuranceToSubtract == 999.75
        taxCalculatorResponse1.incomeTaxMinusHealthInsurance == -2439.95
        taxCalculatorResponse1.finalIncomeTax == -2439

        taxCalculatorResponse1.collectedVat == 11400
        taxCalculatorResponse1.paidVat == 5700.00
        taxCalculatorResponse1.vatToReturn == 0.0

        when:
        def taxCalculatorResponse2 = calculateTaxes(TestHelpers.seller(3))

        then:
        taxCalculatorResponse2.income == 48600.0
        taxCalculatorResponse2.costs == 54300.0

        taxCalculatorResponse2.incomeMinusCosts == -5700.0
        taxCalculatorResponse2.pensionInsurance == 1879.53
        taxCalculatorResponse2.incomeMinusCostsMinusPensionInsurance == -7579.53
        taxCalculatorResponse2.incomeMinusCostsMinusPensionInsuranceRounded == -7580
        taxCalculatorResponse2.incomeTax == -1440.20

        taxCalculatorResponse2.healthInsurancePaid == 1161.0
        taxCalculatorResponse2.healthInsuranceToSubtract == 999.75
        taxCalculatorResponse2.incomeTaxMinusHealthInsurance == -2439.95
        taxCalculatorResponse2.finalIncomeTax == -2439

        taxCalculatorResponse2.collectedVat == 11400
        taxCalculatorResponse2.paidVat == 5700.00
        taxCalculatorResponse2.vatToReturn == 0.0
    }
}
