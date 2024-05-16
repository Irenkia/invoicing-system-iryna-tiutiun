package pl.futurecollars.invoicing.model;

import static javax.persistence.CascadeType.ALL;

import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Invoice implements WithId {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @ApiModelProperty(value = "Invoice id (generated by application)", required = true, example = "1")
  private Long id;

  @ApiModelProperty(value = "Date invoice was created", required = true)
  private LocalDate date;

  @ApiModelProperty(value = "Invoice number (assigned by user)", required = true, example = "2024/04/25/0001")
  private String number;

  @JoinColumn(name = "buyer")
  @OneToOne(cascade = ALL)
  @ApiModelProperty(value = "Company who bought the product/service", required = true)
  private Company buyer;

  @JoinColumn(name = "seller")
  @OneToOne(cascade = ALL)
  @ApiModelProperty(value = "Company who is selling the product/service", required = true)
  private Company seller;

  @JoinTable(name = "invoice_invoice_entry", inverseJoinColumns = @JoinColumn(name = "invoice_entry_id"))
  @OneToMany(cascade = ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  @ApiModelProperty(value = "List of products/services", required = true)
  private List<InvoiceEntry> entries;

  public Invoice(LocalDate date, Company buyer, Company seller, List<InvoiceEntry> entries) {
    this.date = date;
    this.buyer = buyer;
    this.seller = seller;
    this.entries = entries;
  }

}
