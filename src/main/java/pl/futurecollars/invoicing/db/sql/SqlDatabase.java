package pl.futurecollars.invoicing.db.sql;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
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

@AllArgsConstructor
@NoArgsConstructor
public class SqlDatabase implements Database {

  private JdbcTemplate jdbcTemplate;

  private Map<Vat, Integer> vatToId = new HashMap<>();
  private Map<Integer, Vat> idToVat = new HashMap<>();

  public SqlDatabase(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @PostConstruct
  private void initVatRateMap() { // default so it can be called from SqlDatabaseIntegrationTest
    jdbcTemplate.query("SELECT * FROM vat", resultSet -> {
      Vat vat = Vat.valueOf("VAT_" + resultSet.getString("name"));
      int id = resultSet.getInt("id");
      vatToId.put(vat, id);
      idToVat.put(id, vat);
    });
  }

  private int insertCompany(Company company) {
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
    int companyId = (int) Objects.requireNonNull(keyHolder.getKey()).longValue();
    return companyId;
  }

  private int insertInvoice(Invoice invoice, int buyerId, int sellerId) {
    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement("INSERT INTO invoice "
          + "\t(date, number, buyer, seller) "
          + "\tvalues (?, ?, ?, ?);", new String[] {"id"});
      ps.setDate(1, Date.valueOf(invoice.getDate()));
      ps.setString(2, invoice.getNumber());
      ps.setInt(3, buyerId);
      ps.setInt(4, sellerId);
      return ps;
    }, keyHolder);
    int invoiceId = keyHolder.getKey().intValue();
    return invoiceId;
  }

  private int insertInvoiceEntry(Invoice invoice) {
    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
    invoice.getEntries().forEach(invoiceEntry -> {
      jdbcTemplate.update(connection -> {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO invoice_entry "
            + "\t(description, quantity, net_price, vat_value, vat_rate, expense_related_to_car) "
            + "\tvalues (?, ?, ?, ?, ?, ?);", new String[] {"id"});
        ps.setString(1, invoiceEntry.getDescription());
        ps.setInt(2, invoiceEntry.getQuantity());
        ps.setBigDecimal(3, invoiceEntry.getNetPrice());
        ps.setBigDecimal(4, invoiceEntry.getVatValue());
        ps.setInt(5, vatToId.get(invoiceEntry.getVatRate()));
        ps.setObject(6, insertCarAndGetItId(invoiceEntry.getExpenseRelatedToCar()));
        return ps;
      }, keyHolder);
    });
    int invoiceEntryId = keyHolder.getKey().intValue();
    return invoiceEntryId;
  }

  private void insertInvoiceInvoiceEntry(int invoiceId, int invoiceEntryId) {
    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement("INSERT INTO invoice_invoice_entry "
          + "\t(invoice_id, invoice_entry_id) "
          + "\tvalues (?, ?);");
      ps.setInt(1, invoiceId);
      ps.setInt(2, invoiceEntryId);
      return ps;
    });
  }

  private Integer insertCarAndGetItId(Car car) {
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

    return Objects.requireNonNull(keyHolder.getKey()).intValue();
  }

  @Override
  @Transactional
  public int save(Invoice invoice) {
    int buyerId = insertCompany(invoice.getBuyer());
    int sellerId = insertCompany(invoice.getSeller());
    int invoiceId = insertInvoice(invoice, buyerId, sellerId);
    int invoiceEntryId = insertInvoiceEntry(invoice);
    insertInvoiceInvoiceEntry(invoiceId, invoiceEntryId);
    return invoiceId;
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
        + "\tINNER JOIN company c1 on i.buyer = c1.id"
        + "\tINNER JOIN company c2 on i.seller = c2.id;", (resultSet, rowNumber) -> {
          int invoiceId = resultSet.getInt("invoice_id");

        List<InvoiceEntry> invoiceEntries = jdbcTemplate.query("SELECT * FROM invoice_invoice_entry iie"
            + "\tINNER JOIN invoice_entry ie on iie.invoice_entry_id = ie.id"
            + "\tLEFT OUTER JOIN car c on ie.expense_related_to_car = c.id "
            + "\tWHERE invoice_id = " + invoiceId + ";", new RowMapper<InvoiceEntry>() {

              @Override
              public InvoiceEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
                return InvoiceEntry.builder()
                    .description(rs.getString("description"))
                    .quantity(rs.getInt("quantity"))
                    .netPrice(rs.getBigDecimal("net_price"))
                    .vatValue(rs.getBigDecimal("vat_value"))
                    .vatRate(idToVat.get(rs.getInt("vat_rate")))
                    .expenseRelatedToCar(rs.getObject("registration_number") != null
                        ? Car.builder()
                        .registrationNumber(rs.getString("registration_number"))
                        .personalUse(rs.getBoolean("personal_use"))
                        .build() : null)
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
