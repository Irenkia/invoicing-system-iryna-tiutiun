package pl.futurecollars.invoicing.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.futurecollars.invoicing.db.file.FileBasedDatabase;
import pl.futurecollars.invoicing.db.file.IdService;
import pl.futurecollars.invoicing.utils.FilesService;
import pl.futurecollars.invoicing.utils.JsonService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class Config {
    public static final String DATABASE_LOCATION = "db";
    public static final String ID_FILE_NAME = "id.txt";
    public static final String INVOICES_FILE_NAME = "invoices.txt";

    @Bean
    public FilesService filesService() {
        return new FilesService();
    }

    @Bean
    public JsonService jsonService() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new JsonService();
    }

    @Bean
    public IdService idService(FilesService filesService) throws IOException {
        Path idFilePath = Files.createTempFile(DATABASE_LOCATION,ID_FILE_NAME);
        return new IdService(idFilePath,filesService);
    }

    @Bean
    public FileBasedDatabase fileBasedDatabase(IdService idService, FilesService filesService, JsonService jsonService) throws IOException {
        Path databaseFilePath = Files.createTempFile(DATABASE_LOCATION,INVOICES_FILE_NAME);
        return new FileBasedDatabase(databaseFilePath, idService,filesService, jsonService);
    }

}
