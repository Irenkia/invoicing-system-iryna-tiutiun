package pl.futurecollars.invoicing.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.futurecollars.invoicing.db.file.FileBasedDatabase;
import pl.futurecollars.invoicing.db.file.IdService;
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase;
import pl.futurecollars.invoicing.utils.FilesService;
import pl.futurecollars.invoicing.utils.JsonService;

@Configuration
public class DatabaseConfiguration {

  public static final String DATABASE_LOCATION = "db";
  public static final String ID_FILE_NAME = "id.txt";
  public static final String INVOICES_FILE_NAME = "invoices.txt";

  @Bean
  public IdService idService(FilesService filesService) throws IOException {
    Path idFilePath = Files.createTempFile(DATABASE_LOCATION, ID_FILE_NAME);
    return new IdService(idFilePath, filesService);
  }

  @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "file")
  @Bean
  public Database fileBasedDatabase(IdService idService, FilesService filesService, JsonService jsonService) throws IOException {
    Path databaseFilePath = Files.createTempFile(DATABASE_LOCATION, INVOICES_FILE_NAME);
    return new FileBasedDatabase(databaseFilePath, idService, filesService, jsonService);
  }

  @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "memory")
  @Bean
  public Database inMemoryDatabase() {
    return new InMemoryDatabase();
  }

}
