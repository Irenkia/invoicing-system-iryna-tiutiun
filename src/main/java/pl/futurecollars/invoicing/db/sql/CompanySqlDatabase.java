package pl.futurecollars.invoicing.db.sql;

import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Company;

public class CompanySqlDatabase extends AbstractCompanySqlDatabase implements Database<Company> {

  public static final String SELECT_FROM_Company = "SELECT * FROM company";

  public CompanySqlDatabase(JdbcTemplate jdbcTemplate) {
    super(jdbcTemplate);
  }

  private RowMapper<Company> selectFromCompany() {
    return (resultSet, rowNumber) ->
        Company.builder()
            .id(resultSet.getLong("id"))
            .taxIdentificationNumber(resultSet.getString("tax_identification_number"))
            .address(resultSet.getString("address"))
            .name(resultSet.getString("name"))
            .pensionInsurance(resultSet.getBigDecimal("pension_insurance"))
            .healthInsurance(resultSet.getBigDecimal("health_insurance"))
            .build();

  }

  @Override
  @Transactional
  public Long save(Company company) {
    return insertCompany(company);
  }

  @Override
  public Optional<Company> getById(Long id) {
    List<Company> companies = jdbcTemplate.query(SELECT_FROM_Company + " WHERE id = " + id + ";", selectFromCompany());
    return companies.isEmpty() ? Optional.empty() : Optional.of(companies.get(0));
  }

  @Override
  public List<Company> getAll() {
    return jdbcTemplate.query(SELECT_FROM_Company, selectFromCompany());
  }

  @Override
  @Transactional
  public Optional<Company> update(Long id, Company updateCompany) {
    Optional<Company> companyOptional = getById(id);
    if (companyOptional.isPresent()){
      Company originalCompany = companyOptional.get();
      updateCompany(originalCompany, updateCompany);
    }
    return companyOptional;
  }

  @Override
  @Transactional
  public Optional<Company> delete(Long id) {
    Optional<Company> companyOptional = getById(id);
    if (companyOptional.isPresent()){
      deleteCompany(id);
    }
    return companyOptional;
  }

}
