package pl.futurecollars.invoicing.db.sql;

import java.sql.PreparedStatement;
import java.util.Objects;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import pl.futurecollars.invoicing.model.Company;

public class AbstractCompanySqlDatabase {

  protected final JdbcTemplate jdbcTemplate;

  public AbstractCompanySqlDatabase(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  protected Long insertCompany(Company company) {
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

  protected void updateCompany(Company originalCompany, Company updateCompany) {
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

  protected void deleteCompany(long id) {
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement("DELETE FROM company "
          + "\tWHERE id = ?;");
      ps.setLong(1, id);
      return ps;
    });
  }

}
