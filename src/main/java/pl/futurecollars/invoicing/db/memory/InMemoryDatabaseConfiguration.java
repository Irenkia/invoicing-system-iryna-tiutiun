package pl.futurecollars.invoicing.db.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "invoicing-system.database", havingValue = "memory")
public class InMemoryDatabaseConfiguration {

  @Bean
  public Database<Invoice> invoiceInMemoryDatabase() {
    log.info("Creating in-memory<Invoice> database");
    return new InMemoryDatabase<>();
  }

  @Bean
  public Database<Company> companyInMemoryDatabase() {
    log.info("Creating in-memory<Company> database");
    return new InMemoryDatabase<>();
  }

}
