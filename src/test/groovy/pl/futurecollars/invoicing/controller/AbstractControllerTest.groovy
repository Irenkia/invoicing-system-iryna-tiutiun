package pl.futurecollars.invoicing.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.service.tax.TaxCalculatorResult
import pl.futurecollars.invoicing.utils.JsonService
import spock.lang.Specification

@AutoConfigureMockMvc
@SpringBootTest
class AbstractControllerTest extends Specification {

    static final String INVOICE_ENDPOINT = "/invoices"
    static final String COMPANY_ENDPOINT = "/companies"
    static final String TAX_CALCULATOR_ENDPOINT = "/tax"

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JsonService jsonService;

    def setup() {
        deleteAllInvoices()
    }

    protected  <T> T getAll(Class<T> clazz, String endpoint) {
        def response = mockMvc
                .perform(MockMvcRequestBuilders.get(endpoint))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .response
                .contentAsString
        return jsonService.toObject(response, clazz)
    }

    List<Invoice> getAllInvoices() {
        getAll(Invoice[], INVOICE_ENDPOINT)
    }

    List<Company> getAllCompanies() {
        getAll(Company[], COMPANY_ENDPOINT)
    }


    protected <T> Long addAndReturnId(T item, String endpoint) {
        def id = Long.valueOf(mockMvc.perform(
                MockMvcRequestBuilders.post(endpoint)
                        .content(jsonService.toJson(item))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .response
                .contentAsString)
        return id
    }

    Long addInvoiceAndReturnId(Invoice invoice) {
        addAndReturnId(invoice, INVOICE_ENDPOINT)
    }

    Long addCompanyAndReturnId(Company company) {
        addAndReturnId(company, COMPANY_ENDPOINT)
    }


    protected <T> T getById(long id, Class<T> clazz, String endpoint) {
        def response = mockMvc
                .perform(MockMvcRequestBuilders.get("$endpoint/$id"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .response
                .contentAsString
        return jsonService.toObject(response, clazz)
    }

    Invoice getInvoiceById(long id) {
        getById(id, Invoice, INVOICE_ENDPOINT)
    }

    Company getCompanyById(long id) {
        getById(id, Company, COMPANY_ENDPOINT)
    }


    protected <T> void updateById(Long id, T item, String endpoint){
        mockMvc.perform(
                MockMvcRequestBuilders.put("$endpoint/$id")
                        .content(jsonService.toJson(item))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
    }

    void updateInvoiceById(long id, Invoice invoice) {
        updateById(id, invoice, INVOICE_ENDPOINT)
    }

    void updateCompanyById(long id, Company company) {
        updateById(id, company, COMPANY_ENDPOINT)
    }


    protected <T> void deleteById(Long id, String endpoint){
        mockMvc.perform(MockMvcRequestBuilders.delete("$endpoint/$id"))
                .andExpect(MockMvcResultMatchers.status().isOk())
    }

    void deleteInvoiceById(long id) {
        deleteById(id, INVOICE_ENDPOINT)
    }

    void deleteCompanyById(long id) {
        deleteById(id, COMPANY_ENDPOINT)
    }

    void deleteAllInvoices(){
        def listInvoices =  getAllInvoices()
        listInvoices.each {invoice -> deleteInvoiceById(invoice.id)}
    }


    TaxCalculatorResult calculateTaxes(Company company) {
        def response = mockMvc.perform(
                MockMvcRequestBuilders.post("$TAX_CALCULATOR_ENDPOINT")
                        .content(jsonService.toJson(company))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .response
                .contentAsString
        return jsonService.toObject(response, TaxCalculatorResult.class)
    }

}


