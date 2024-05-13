package pl.futurecollars.invoicing.db.file;

import java.nio.file.Path;
import pl.futurecollars.invoicing.utils.FilesService;

public class IdService {

  private final Path idFilePath;
  private final FilesService filesService;
  private Long nextId = 1L;

  public IdService(Path idFilePath, FilesService filesService) {
    this.idFilePath = idFilePath;
    this.filesService = filesService;
  }

  public Long getNextIdAndIncrement() {
    filesService.writeToFile(idFilePath, String.valueOf(nextId));
    return nextId++;
  }

}
