package pl.futurecollars.invoicing.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.service.TaxCalculatorResult
import pl.futurecollars.invoicing.utils.JsonService
import spock.lang.Specification

@AutoConfigureMockMvc
@SpringBootTest
class Requests extends Specification {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JsonService jsonService;

    List<Invoice> getRequest(){
        def response = mockMvc
                .perform(MockMvcRequestBuilders.get("/invoices"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .response
                .contentAsString
        return jsonService.toObject(response, Invoice[])
    }

    int postRequest(String invoiceAsJson){
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

    Invoice getRequestById(int id){
        def response = mockMvc
                .perform(MockMvcRequestBuilders.get("/invoices/$id"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .response
                .contentAsString
        return jsonService.toObject(response, Invoice)
    }

    void putRequestById(int id, String invoiceAsJson){
        mockMvc.perform(
                MockMvcRequestBuilders.put("/invoices/$id")
                        .content(invoiceAsJson)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
    }

    void deleteRequestById(int id){
        mockMvc.perform(MockMvcRequestBuilders.delete("/invoices/$id"))
                .andExpect(MockMvcResultMatchers.status().isOk())
    }

    void deleteAllInvoices(){
        def listInvoices =  getRequest()
        listInvoices.each {invoice -> deleteRequestById(invoice.id)}
    }

    TaxCalculatorResult calculateTaxes(String taxIdentificationNumber) {
        def response = mockMvc
                .perform(MockMvcRequestBuilders.get("/tax/$taxIdentificationNumber"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .response
                .contentAsString
        return jsonService.toObject(response, TaxCalculatorResult.class)
    }
}
