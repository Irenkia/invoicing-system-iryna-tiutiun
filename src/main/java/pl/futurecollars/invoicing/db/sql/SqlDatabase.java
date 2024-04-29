package pl.futurecollars.invoicing.db.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Car;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;

@AllArgsConstructor
@NoArgsConstructor
public class SqlDatabase implements Database {

  private JdbcTemplate jdbcTemplate;

  @Override
  public int save(Invoice invoice) {
    jdbcTemplate.update("insert into company (tax_identification_number, address, name, pension_insurance, health_insurance) "
        + "\tvalues ('1111111111', 'Washington Post 3','International Transporters', '626.51', '387.00');");
    jdbcTemplate.update("insert into company (tax_identification_number, address, name, pension_insurance, health_insurance) "
        + "\tvalues ('2222222222', 'Washington Post 3','International Transporters', '626.51', '387.00');");
    jdbcTemplate.update("insert into invoice (date, number, buyer, seller) values ('2021-03-05', '2021/03/05/0001', 1, 2);");
    return 0;
  }

  @Override
  public Optional<Invoice> getById(int id) {
    return Optional.empty();
  }

  @Override
  public List<Invoice> getAll() {
    return jdbcTemplate.query("SELECT "
              + "\ti.id as invoice_id,"
              + "\ti.date, i.number,"
              + "\tc1.id as buyer_id,"
              + "\tc1.tax_identification_number as buyer_tax_identification_number,"
              + "\tc1.address as buyer_address, c1.name as buyer_name,"
              + "\tc1.pension_insurance as buyer_pension_insurance,"
              + "\tc1.health_insurance as buyer_health_insurance,"
              + "\tc2.id as seller_id,"
              + "\tc2.tax_identification_number as seller_tax_identification_number,"
              + "\tc2.address as seller_address, c1.name as seller_name,"
              + "\tc2.pension_insurance as seller_pension_insurance,"
              + "\tc2.health_insurance as seller_health_insurance"
              + "\tFROM invoice i"
              + "\tinner join company c1 on i.buyer = c1.id"
              + "\tinner join company c2 on i.seller = c2.id;", (resultSet, rowNumber) -> {

          List<InvoiceEntry> invoiceEntries = jdbcTemplate.query("SELECT * FROM invoice_invoice_entry iie"
              + "\tinner join invoice_entry ie on iie.invoice_entry_id = ie.id"
              + "\tWHERE invoice_id = 2;", new RowMapper<InvoiceEntry>() {

                @Override
                public InvoiceEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return InvoiceEntry.builder()
                        .description(rs.getString("description"))
                        .quantity(rs.getInt("quantity"))
                        .netPrice(rs.getBigDecimal("net_price"))
                        .vatValue(rs.getBigDecimal("vat_value"))
                        // .vatRate(rs.getInt("vat_rate"))
                        .expenseRelatedToCar(Car.builder()
                            .registrationNumber(rs.getString("registration_number"))
                            .personalUse(rs.getBoolean("personal_user"))
                            .build())
                        .build();
                }
              });

        return Invoice.builder()
            .id(resultSet.getInt("invoice_id"))
            .date(resultSet.getDate("date").toLocalDate())
            .number(resultSet.getString("number"))
            .buyer(Company.builder()
                .id(resultSet.getInt("buyer_id"))
                .taxIdentificationNumber(resultSet.getString("buyer_tax_identification_number"))
                .address(resultSet.getString("buyer_address"))
                .name(resultSet.getString("buyer_name"))
                .pensionInsurance(resultSet.getBigDecimal("buyer_pension_insurance"))
                .healthInsurance(resultSet.getBigDecimal("buyer_health_insurance"))
                .build())
            .seller(Company.builder()
                .id(resultSet.getInt("seller_id"))
                .taxIdentificationNumber(resultSet.getString("seller_tax_identification_number"))
                .address(resultSet.getString("seller_address"))
                .name(resultSet.getString("seller_name"))
                .pensionInsurance(resultSet.getBigDecimal("seller_pension_insurance"))
                .healthInsurance(resultSet.getBigDecimal("seller_health_insurance"))
                .build())
            .entries(invoiceEntries)
            .build();
      });
  }

  @Override
  public Optional<Invoice> update(int id, Invoice updatedInvoice) {
    return Optional.empty();
  }

  @Override
  public Optional<Invoice> delete(int id) {
    return Optional.empty();
  }
}
