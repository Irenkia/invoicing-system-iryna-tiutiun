package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Car {

  @ApiModelProperty(value = "Car registration number", required = true, example = "XY 12345")
  private String registrationNumber;

  @ApiModelProperty(value = "If the company is using the car also for the personal reasons", required = true, example = "true")
  private boolean personalUse;
}
