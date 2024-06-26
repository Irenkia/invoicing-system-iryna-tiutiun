package pl.futurecollars.invoicing.db.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.utils.FilesService;
import pl.futurecollars.invoicing.utils.JsonService;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "invoicing-system.database", havingValue = "file")
public class FileDatabaseConfiguration {

  @Bean
  public IdService idService(FilesService filesService,
                             @Value("${invoicing-system.database.directory}") String databaseDirectory,
                             @Value("${invoicing-system.database.id.file}") String idFile) throws IOException {
    Path idFilePath = Files.createTempFile(databaseDirectory, idFile);
    return new IdService(idFilePath, filesService);
  }

  @Bean
  public Database<Invoice> invoiceFileBasedDatabase(IdService idService, FilesService filesService, JsonService jsonService,
                                                    @Value("${invoicing-system.database.directory}") String databaseDirectory,
                                                    @Value("${invoicing-system.database.invoices.file}") String invoicesFile) throws IOException {
    Path databaseFilePath = Files.createTempFile(databaseDirectory, invoicesFile);
    log.info("Creating in-file<Invoice> database = info");
    return new FileBasedDatabase<>(databaseFilePath, idService, filesService, jsonService, Invoice.class);
  }

  @Bean
  public Database<Company> companyFileBasedDatabase(IdService idService, FilesService filesService, JsonService jsonService,
                                                    @Value("${invoicing-system.database.directory}") String databaseDirectory,
                                                    @Value("${invoicing-system.database.invoices.file}") String invoicesFile) throws IOException {
    Path databaseFilePath = Files.createTempFile(databaseDirectory, invoicesFile);
    log.info("Creating in-file<Company> database = info");
    return new FileBasedDatabase<>(databaseFilePath, idService, filesService, jsonService, Company.class);
  }

}
