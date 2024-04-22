package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvoiceEntry {

  @ApiModelProperty(value = "Product/service description", required = true, example = "Dell X12 v3")
  private String description;

  @ApiModelProperty(value = "Number of items", required = true, example = "1")
  private int quantity;

  @ApiModelProperty(value = "Product/service net price", required = true, example = "138.72")
  private BigDecimal netPrice; // example = "138.72" | 154.68 (138.72 + 15.95)-> to reduce the VAT to be paid

  @ApiModelProperty(value = "Product/service tax value", required = true, example = "31.91")
  private BigDecimal vatValue; // example = "31.91" | then 15.95 goes to VAT and 15.96 to costs

  @ApiModelProperty(value = "Tax rate", required = true, example = "VAT_19")
  private Vat vatRate;

  @ApiModelProperty(value = "A car to which expense are added when using it for personal purposes")
  private Car expenseRelatedToCar;
}
