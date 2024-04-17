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
import spock.lang.Stepwise
import java.time.LocalDate

@AutoConfigureMockMvc
@SpringBootTest
@Stepwise
class InvoiceControllerStepwiseTest extends Specification {

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private JsonService jsonService;

    private Invoice originalInvoice = TestHelpers.invoice(1)

    private LocalDate updateDate = LocalDate.of(2024, 03, 30)

    @Shared
    private int invoiceId

    def "empty array is returned when no invoices were created"() {
        when:
        def response = mockMvc.perform (MockMvcRequestBuilders.get("/invoices"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .response
                .contentAsString

        then:
        response == "[]"
    }

    def "add single invoice"() {
        given:
        def invoice = originalInvoice
        def invoiceAsJson = jsonService.toJson(invoice)

        when:
        invoiceId = Integer.valueOf(mockMvc.perform(
                MockMvcRequestBuilders.post("/invoices")
                        .content(invoiceAsJson)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .response
                .contentAsString)

        then:
        invoiceId == 1
    }

    def "one invoice is returned when getting all invoices"() {
        given:
        def expectedInvoice = originalInvoice
        expectedInvoice.id = 1

        when:
        def response = mockMvc.perform(
                MockMvcRequestBuilders.get("/invoices"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .response
                .contentAsString

        def invoices = jsonService.toObject(response, Invoice[])
        then:
        invoices.size() == 1
        invoices[0] == expectedInvoice
    }

    def "invoice is returned correctly when getting by id"() {
        given:
        def expectedInvoice = originalInvoice
        expectedInvoice.id = 1

        when:
        def response = mockMvc.perform(
                MockMvcRequestBuilders.get("/invoices/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .response
                .contentAsString

        def invoice = jsonService.toObject(response, Invoice)
        then:
        invoice == expectedInvoice
    }

    def "when getting invoice by no exist id"() {
        expect:
        mockMvc.perform(
                MockMvcRequestBuilders.get("/invoices/10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .response
                .contentAsString
    }

    def "invoice date can be modified"() {
        given:
        def modifiedInvoice = originalInvoice
        modifiedInvoice.date = updateDate

        def invoiceAsJson = jsonService.toJson(modifiedInvoice)

        expect:
        mockMvc.perform(
                MockMvcRequestBuilders.put("/invoices/1")
                        .content(invoiceAsJson)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())

    }

    def "updated invoice is returned correctly when getting by id"() {
        given:
        def expectedInvoice = originalInvoice
        expectedInvoice.id = 1
        expectedInvoice.date = updateDate

        when:
        def response = mockMvc.perform(MockMvcRequestBuilders.get("/invoices/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .response
                .contentAsString

        def invoices = jsonService.toObject(response, Invoice)
        then:
        invoices == expectedInvoice
    }

    def "invoice can be deleted from exist id"() {
        expect:
        mockMvc.perform(MockMvcRequestBuilders.delete("/invoices/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
    }

    def "invoice can be deleted from no exist id"() {
        expect:
        mockMvc.perform(MockMvcRequestBuilders.delete("/invoices/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
    }

}
