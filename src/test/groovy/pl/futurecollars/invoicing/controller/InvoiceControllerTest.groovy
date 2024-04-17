package pl.futurecollars.invoicing.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import pl.futurecollars.invoicing.helpers.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.utils.JsonService
import spock.lang.Shared
import spock.lang.Specification
import java.time.LocalDate

@AutoConfigureMockMvc
@SpringBootTest
class InvoiceControllerTest extends Specification {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonService jsonService;

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
        response.date == expectedInvoice.date
        response.buyer == expectedInvoice.buyer
        response.seller == expectedInvoice.seller
        response.entries == expectedInvoice.entries
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
        putRequest(invoiceId, invoiceAsJson)

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
        def response = putRequest(invoiceId, invoiceAsJson)

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
        deleteRequest(invoiceId)

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
        deleteRequest(invoiceId) == null
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
        deleteRequest(invoiceId2)

        then: "2 invoices left"
        def expectedResponse1 = getRequest()
        expectedResponse1.size() == List.of(invoice1, invoice3).size()

        deleteAllInvoices()
    }

    private List<Invoice> getRequest(){
        def response = mockMvc
                .perform(MockMvcRequestBuilders.get("/invoices"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .response
                .contentAsString
        return jsonService.toObject(response, Invoice[])
    }

    private int postRequest(String invoiceAsJson){
        def invoiceId = Integer.valueOf(mockMvc.perform(
                MockMvcRequestBuilders.post("/invoices")
                        .content(invoiceAsJson)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .response
                .contentAsString)
        return invoiceId
    }

    private Invoice getRequestById(int id){
        def response = mockMvc
                .perform(MockMvcRequestBuilders.get("/invoices/$id"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .response
                .contentAsString
        return jsonService.toObject(response, Invoice)
    }

    private void putRequest(int id, String invoiceAsJson){
        mockMvc.perform(
                MockMvcRequestBuilders.put("/invoices/$id")
                        .content(invoiceAsJson)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
    }

    private void deleteRequest(int id){
        mockMvc.perform(MockMvcRequestBuilders.delete("/invoices/$id"))
                .andExpect(MockMvcResultMatchers.status().isOk())
    }

    private void deleteAllInvoices(){
        def listInvoices =  getRequest()
        listInvoices.each {invoice -> deleteRequest(invoice.id)}
    }

}
