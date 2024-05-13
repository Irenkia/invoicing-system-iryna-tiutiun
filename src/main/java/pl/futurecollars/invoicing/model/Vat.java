package pl.futurecollars.invoicing.model;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public enum Vat {

  VAT_23(23), // VAT ( 23% )
  VAT_20(19.52), // Pension contribution to salary 4300 -> 839.36 ( 19.52% )
  VAT_19(19), // VAT for our project ( 19% )
  VAT_14(14.57), // Insurance contributions from salary 4300 -> 626.51 ( 14.57% )
  VAT_9(9), // Medical insurance contribution from salary 4300 -> 387.00 ( 9% )
  VAT_8(8),
  VAT_7(7.75), // Medical insurance contribution from salary 4300 -> 333.25 ( 7.75% )
  VAT_5(5),
  VAT_08(0.8),
  VAT_0(0),
  VAT_ZW(0);

  private final BigDecimal rate;

  Vat(int rate) {
    this.rate = BigDecimal.valueOf(rate);
  }

  Vat(double rate) {
    this.rate = BigDecimal.valueOf(rate);
  }
}
