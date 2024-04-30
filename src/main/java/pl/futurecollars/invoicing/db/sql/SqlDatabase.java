package pl.futurecollars.invoicing.db.sql;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Car;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;
import pl.futurecollars.invoicing.model.Vat;

@AllArgsConstructor
@NoArgsConstructor
public class SqlDatabase implements Database {

  private JdbcTemplate jdbcTemplate;

  @Override
  public int save(Invoice invoice) {
    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement("INSERT INTO company "
          + "\t(tax_identification_number, address, name, pension_insurance, health_insurance) "
          + "\tvalues (?, ?, ?, ?, ?);", new String[] {"id"});
      ps.setString(1, invoice.getBuyer().getTaxIdentificationNumber());
      ps.setString(2, invoice.getBuyer().getAddress());
      ps.setString(3, invoice.getBuyer().getName());
      ps.setBigDecimal(4, invoice.getBuyer().getPensionInsurance());
      ps.setBigDecimal(5, invoice.getBuyer().getHealthInsurance());
      return ps;
    }, keyHolder);
    Long buyerId = Objects.requireNonNull(keyHolder.getKey()).longValue();

    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement("INSERT INTO company "
          + "\t(tax_identification_number, address, name, pension_insurance, health_insurance) "
          + "\tvalues (?, ?, ?, ?, ?);", new String[] {"id"});
      ps.setString(1, invoice.getSeller().getTaxIdentificationNumber());
      ps.setString(2, invoice.getSeller().getAddress());
      ps.setString(3, invoice.getSeller().getName());
      ps.setBigDecimal(4, invoice.getSeller().getPensionInsurance());
      ps.setBigDecimal(5, invoice.getSeller().getHealthInsurance());
      return ps;
    }, keyHolder);
    Long sellerId = keyHolder.getKey().longValue();

    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement("INSERT INTO invoice (date, number, buyer, seller) "
          + "\tvalues (?, ?, ?, ?);", new String[] {"id"});
      ps.setDate(1, Date.valueOf(invoice.getDate()));
      ps.setString(2, invoice.getNumber());
      ps.setLong(3, buyerId);
      ps.setLong(4, sellerId);
      return ps;
    }, keyHolder);

    return keyHolder.getKey().intValue();
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
                    .vatRate(Vat.values()[rs.getInt("vat_rate")])
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
