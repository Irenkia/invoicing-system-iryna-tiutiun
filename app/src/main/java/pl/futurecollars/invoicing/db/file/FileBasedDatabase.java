package pl.futurecollars.invoicing.db.file;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.utils.FilesService;
import pl.futurecollars.invoicing.utils.JsonService;

public class FileBasedDatabase<T> implements Database {

  private final Path databasePath;
  private final IdService idService;
  private final FilesService filesService;
  private final JsonService jsonService;
  private final Class<T> clazz;

  public FileBasedDatabase(Path databasePath, IdService idService, FilesService filesService, JsonService jsonService, Class<T> clazz) {
    this.databasePath = databasePath;
    this.idService = idService;
    this.filesService = filesService;
    this.jsonService = jsonService;
    this.clazz = clazz;
  }

  @Override
  public int save(Invoice invoice) {
    invoice.setId(idService.getNextIdAndIncrement());
    filesService.appendLineToFile(databasePath, jsonService.toJson(invoice));
    return invoice.getId();
  }

  @Override
  public Optional<Invoice> getById(int id) {
    return filesService.readAllLines(databasePath)
        .stream()
        .filter(line -> containsId(line, id))
        .map(line -> (Invoice) jsonService.toObject(line, clazz))
        .findFirst();
  }

  @Override
  public List<Invoice> getAll() {
    return filesService.readAllLines(databasePath)
        .stream()
        .map(line -> (Invoice) jsonService.toObject(line, clazz))
        .collect(Collectors.toList());
  }

  @Override
  public void update(int id, Invoice updatedInvoice) {
    List<String> allLines = filesService.readAllLines(databasePath);
    List<String> listUpdatedInvoice = allLines
        .stream()
        .filter(line -> !containsId(line, id))
        .collect(Collectors.toList());

    Optional<Invoice> invoice = getById(id);
    if (invoice.isPresent()) {
      Invoice newInvoice = invoice.get();
      newInvoice.setId(id);
      newInvoice.setDate(updatedInvoice.getDate());
      newInvoice.setBuyer(updatedInvoice.getBuyer());
      newInvoice.setSeller(updatedInvoice.getSeller());
      newInvoice.setEntries(updatedInvoice.getEntries());
      listUpdatedInvoice.add(jsonService.toJson(newInvoice));
    } else {
      throw new IllegalArgumentException("Id " + id + " does not exist");
    }
    filesService.writeLinesToFile(databasePath, listUpdatedInvoice);
  }

  @Override
  public void delete(int id) {
    List<String> allLines = filesService.readAllLines(databasePath);
    List<String> listWithoutInvoiceFromId = allLines
        .stream()
        .filter(line -> !containsId(line, id))
        .collect(Collectors.toList());
    filesService.writeLinesToFile(databasePath, listWithoutInvoiceFromId);
  }

  private boolean containsId(String line, int id) {
    return line.contains("{\"id\":" + id + ",");
  }
}
