package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceEntry {

  @ApiModelProperty(value = "Product/service description", required = true, example = "Dell X12 v3")
  private String description;

  @ApiModelProperty(value = "Number of items", required = true, example = "5")
  private int quantity;

  @ApiModelProperty(value = "Product/service net price", required = true, example = "1000.00")
  private BigDecimal price;

  @ApiModelProperty(value = "Product/service tax value", required = true, example = "80.00")
  private BigDecimal vatValue;

  @ApiModelProperty(value = "Tax rate", required = true, example = "VAT_8")
  private Vat vatRate;

}
