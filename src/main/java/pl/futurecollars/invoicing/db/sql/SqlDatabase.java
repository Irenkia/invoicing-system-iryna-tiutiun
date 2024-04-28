package pl.futurecollars.invoicing.db.sql;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;

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
    return null;
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
