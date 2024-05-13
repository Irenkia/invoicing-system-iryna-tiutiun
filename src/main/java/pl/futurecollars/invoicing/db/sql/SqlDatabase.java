package pl.futurecollars.invoicing.db.sql;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.transaction.annotation.Transactional;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Car;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;
import pl.futurecollars.invoicing.model.Vat;

@NoArgsConstructor
public class SqlDatabase implements Database {

  private static final String SELECT_FROM_INVOICE = "SELECT "
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
      + "\tINNER JOIN company c1 on i.buyer = c1.id"
      + "\tINNER JOIN company c2 on i.seller = c2.id";

  private JdbcTemplate jdbcTemplate;

  public SqlDatabase(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private Long insertCompany(Company company) {
    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement("INSERT INTO company "
          + "\t(tax_identification_number, address, name, pension_insurance, health_insurance) "
          + "\tvalues (?, ?, ?, ?, ?);", new String[] {"id"});
      ps.setString(1, company.getTaxIdentificationNumber());
      ps.setString(2, company.getAddress());
      ps.setString(3, company.getName());
      ps.setBigDecimal(4, company.getPensionInsurance());
      ps.setBigDecimal(5, company.getHealthInsurance());
      return ps;
    }, keyHolder);
    Long companyId = Objects.requireNonNull(keyHolder.getKey()).longValue();
    return companyId;
  }

  private Long insertInvoice(Invoice invoice, Long buyerId, Long sellerId) {
    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement("INSERT INTO invoice "
          + "\t(date, number, buyer, seller) "
          + "\tvalues (?, ?, ?, ?);", new String[] {"id"});
      ps.setDate(1, Date.valueOf(invoice.getDate()));
      ps.setString(2, invoice.getNumber());
      ps.setLong(3, buyerId);
      ps.setLong(4, sellerId);
      return ps;
    }, keyHolder);
    Long invoiceId = Long.valueOf(keyHolder.getKey().intValue());
    return invoiceId;
  }

  private Long insertCarAndGetItId(Car car) {
    if (car == null) {
      return null;
    }
    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement("INSERT INTO car "
          + "\t(registration_number, personal_use) values (?, ?);", new String[] {"id"});
      ps.setString(1, car.getRegistrationNumber());
      ps.setBoolean(2, car.isPersonalUse());
      return ps;
    }, keyHolder);
    return (long) Objects.requireNonNull(keyHolder.getKey()).intValue();
  }

  private Long insertInvoiceEntry(Invoice invoice) {
    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
    invoice.getEntries().forEach(invoiceEntry -> {
      jdbcTemplate.update(connection -> {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO invoice_entry "
            + "\t(description, quantity, net_price, vat_value, vat_rate, expense_related_to_car) "
            + "\tvalues (?, ?, ?, ?, ?, ?);", new String[] {"id"});
        ps.setString(1, invoiceEntry.getDescription());
        ps.setBigDecimal(2, invoiceEntry.getQuantity());
        ps.setBigDecimal(3, invoiceEntry.getNetPrice());
        ps.setBigDecimal(4, invoiceEntry.getVatValue());
        ps.setString(5, invoiceEntry.getVatRate().name());
        ps.setObject(6, insertCarAndGetItId(invoiceEntry.getExpenseRelatedToCar()));
        return ps;
      }, keyHolder);
    });
    Long invoiceEntryId = (long) keyHolder.getKey().intValue();
    return invoiceEntryId;
  }

  private void insertInvoiceInvoiceEntry(Long invoiceId, Long invoiceEntryId) {
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement("INSERT INTO invoice_invoice_entry "
          + "\t(invoice_id, invoice_entry_id) "
          + "\tvalues (?, ?);");
      ps.setLong(1, invoiceId);
      ps.setLong(2, invoiceEntryId);
      return ps;
    });
  }

  private List<InvoiceEntry> selectFromInvoiceInvoiceEntry(Long invoiceId) {
    return jdbcTemplate.query("SELECT * FROM invoice_invoice_entry iie"
        + "\tINNER JOIN invoice_entry ie on iie.invoice_entry_id = ie.id"
        + "\tLEFT OUTER JOIN car c on ie.expense_related_to_car = c.id "
        + "\tWHERE invoice_id = " + invoiceId + ";", new RowMapper<InvoiceEntry>() {

          @Override
          public InvoiceEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
            return InvoiceEntry.builder()
                .description(rs.getString("description"))
                .quantity(rs.getBigDecimal("quantity"))
                .netPrice(rs.getBigDecimal("net_price"))
                .vatValue(rs.getBigDecimal("vat_value"))
                .vatRate(Vat.valueOf(rs.getString("vat_rate")))
                .expenseRelatedToCar(rs.getObject("registration_number") != null
                    ? Car.builder()
                    .registrationNumber(rs.getString("registration_number"))
                    .personalUse(rs.getBoolean("personal_use"))
                    .build() : null)
                .build();
          }
        });
  }

  private RowMapper<Invoice> selectFromInvoice() {
    return (resultSet, rowNumber) -> {
      Long invoiceId = resultSet.getLong("invoice_id");

      List<InvoiceEntry> invoiceEntries = selectFromInvoiceInvoiceEntry(invoiceId);

      return Invoice.builder()
          .id(resultSet.getLong("invoice_id"))
          .date(resultSet.getDate("date").toLocalDate())
          .number(resultSet.getString("number"))
          .buyer(Company.builder()
              .id(resultSet.getLong("buyer_id"))
              .taxIdentificationNumber(resultSet.getString("buyer_tax_identification_number"))
              .address(resultSet.getString("buyer_address"))
              .name(resultSet.getString("buyer_name"))
              .pensionInsurance(resultSet.getBigDecimal("buyer_pension_insurance"))
              .healthInsurance(resultSet.getBigDecimal("buyer_health_insurance"))
              .build())
          .seller(Company.builder()
              .id(resultSet.getLong("seller_id"))
              .taxIdentificationNumber(resultSet.getString("seller_tax_identification_number"))
              .address(resultSet.getString("seller_address"))
              .name(resultSet.getString("seller_name"))
              .pensionInsurance(resultSet.getBigDecimal("seller_pension_insurance"))
              .healthInsurance(resultSet.getBigDecimal("seller_health_insurance"))
              .build())
          .entries(invoiceEntries)
          .build();
    };
  }

  private void updateCompany(Company originalCompany, Company updateCompany) {
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement("UPDATE company "
          + "\tSET tax_identification_number=?, "
          + "\taddress=?, "
          + "\tname=?, "
          + "\tpension_insurance=?, "
          + "\thealth_insurance=? "
          + "\tWHERE id = ?;");
      ps.setString(1, updateCompany.getTaxIdentificationNumber());
      ps.setString(2, updateCompany.getAddress());
      ps.setString(3, updateCompany.getName());
      ps.setBigDecimal(4, updateCompany.getPensionInsurance());
      ps.setBigDecimal(5, updateCompany.getHealthInsurance());
      ps.setLong(6, originalCompany.getId());
      return ps;
    });
  }

  private void deleteInvoiceInvoiceEntry(Long id) {
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement("DELETE FROM invoice_invoice_entry "
          + "\tWHERE invoice_id = ?;");
      ps.setLong(1, id);
      return ps;
    });
  }

  private void deleteInvoiceEntry(Long id) {
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement("DELETE FROM invoice_entry "
          + "\tWHERE id = ?;");
      ps.setLong(1, id);
      return ps;
    });
  }

  private void deleteCar(Long id) {
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement("DELETE FROM car "
          + "\tWHERE id = ?;");
      ps.setLong(1, id);
      return ps;
    });
  }

  private void deleteCompany(Invoice invoice) {
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement("DELETE FROM company "
          + "\tWHERE id in (?, ?);");
      ps.setLong(1, invoice.getBuyer().getId());
      ps.setLong(2, invoice.getSeller().getId());
      return ps;
    });
  }

  @Override
  @Transactional
  public Long save(Invoice invoice) {
    Long buyerId = insertCompany(invoice.getBuyer());
    Long sellerId = insertCompany(invoice.getSeller());
    Long invoiceId = insertInvoice(invoice, buyerId, sellerId);
    Long invoiceEntryId = insertInvoiceEntry(invoice);
    insertInvoiceInvoiceEntry(invoiceId, invoiceEntryId);
    return invoiceId;
  }

  @Override
  public Optional<Invoice> getById(Long id) {
    List<Invoice> invoices = jdbcTemplate.query(SELECT_FROM_INVOICE + " WHERE i.id = " + id + ";", selectFromInvoice());
    return invoices.isEmpty() ? Optional.empty() : Optional.of(invoices.get(0));
  }

  @Override
  public List<Invoice> getAll() {
    return jdbcTemplate.query(SELECT_FROM_INVOICE + ";", selectFromInvoice());
  }

  @Override
  public Optional<Invoice> update(Long id, Invoice updatedInvoice) {
    Optional<Invoice> originalInvoice = getById(id);
    if (originalInvoice.isPresent()) {
      updateCompany(originalInvoice.get().getBuyer(), updatedInvoice.getBuyer());
      updateCompany(originalInvoice.get().getSeller(), updatedInvoice.getSeller());
      jdbcTemplate.update(connection -> {
        PreparedStatement ps = connection.prepareStatement("UPDATE invoice "
            + "\tSET date=?, "
            + "\tnumber=? "
            + "\tWHERE id = ?;");
        ps.setDate(1, Date.valueOf(updatedInvoice.getDate()));
        ps.setString(2, updatedInvoice.getNumber());
        ps.setLong(3, id);
        return ps;
      });
      deleteInvoiceInvoiceEntry(id);
      deleteInvoiceEntry(id);
      deleteCar(id);
      Long invoiceEntryId = insertInvoiceEntry(updatedInvoice);
      insertInvoiceInvoiceEntry(id, invoiceEntryId);
    }
    return originalInvoice;
  }

  @Override
  public Optional<Invoice> delete(Long id) {
    Optional<Invoice> invoiceOptional = getById(id);
    if (invoiceOptional.isPresent()) {
      deleteInvoiceInvoiceEntry(id);
      deleteInvoiceEntry(id);
      deleteCar(id);
      jdbcTemplate.update(connection -> {
        PreparedStatement ps = connection.prepareStatement("DELETE FROM invoice "
            + "\tWHERE id = ?;");
        ps.setLong(1, id);
        return ps;
      });
      Invoice invoice = invoiceOptional.get();
      deleteCompany(invoice);
    }
    return invoiceOptional;
  }

}
