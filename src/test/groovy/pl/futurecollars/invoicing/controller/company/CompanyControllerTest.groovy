package pl.futurecollars.invoicing.controller.company

import pl.futurecollars.invoicing.controller.AbstractControllerTest
import pl.futurecollars.invoicing.helpers.TestHelpers
import pl.futurecollars.invoicing.model.Company
import spock.lang.Shared

class CompanyControllerTest extends AbstractControllerTest {

    @Shared
    private Long companyId

    def setup() {
        deleteAllInvoices()
    }

    def "empty array is returned when no company were created"() {
        deleteAllInvoices()

        given:
        def companies = Collections.emptyList()

        when:
        def response = getAllCompanies()

        then:
        response == companies
    }

    def "add single company (buyer)"() {
        given:
        def company = TestHelpers.buyer(1)

        when:
        companyId = addCompanyAndReturnId(company)

        then:
        getCompanyById(companyId) != null
        deleteCompanyById(companyId)
    }

    def "one company (buyer) is returned when getting all companies"(){
        given:
        def expectedCompany = TestHelpers.buyer(1)
        companyId = addCompanyAndReturnId(expectedCompany)

        when:
        def response = getAllCompanies()

        then:
        response.size() == List.of(expectedCompany).size()
        deleteCompanyById(companyId)
    }

    def "company (buyer) is returned correctly when getting by id"() {
        given:
        def expectedCompany = TestHelpers.buyer(1)
        companyId = addCompanyAndReturnId(expectedCompany)

        when:
        def response = getCompanyById(companyId)

        then:
        resetIds(response)
        response.address.toString() == expectedCompany.address.toString()
        response.name.toString() == expectedCompany.name.toString()
        deleteCompanyById(companyId)
    }

    def "when getting company (buyer) by no exist id"(){
        given:
        companyId = 25

        when:
        def response = getCompanyById(companyId)

        then:
        response == null
    }

    def "company (buyer) name can be modified"() {
        given:
        def modifiedCompany = TestHelpers.buyer(1)
        companyId = addCompanyAndReturnId(modifiedCompany)
        modifiedCompany.name = "Future Collars"

        when:
        updateCompanyById(companyId, modifiedCompany)

        then: "updated company (buyer) is returned correctly when getting by id"
        def response = getCompanyById(companyId)
        response.name == modifiedCompany.name
        deleteCompanyById(companyId)
    }

    def "when company (buyer) can't be modified by no exist id"() {
        given:
        def modifiedCompany = TestHelpers.buyer(1)
        modifiedCompany.name = "Future Collars"
        companyId = addCompanyAndReturnId(modifiedCompany)
        companyId = 25

        when:
        def response = updateCompanyById(companyId, modifiedCompany)

        then:
        response == null
        deleteCompanyById(companyId)
    }

    def "company (buyer) can't deleted from no exist id"() {
        given:
        companyId = 10

        expect:
        deleteCompanyById(companyId) == null
    }

    // resetting is necessary because database query returns ids while we don't know ids in original company
    def Company resetIds(Company company) {
        company.id = null
        return company
    }

}
