package pl.futurecollars.invoicing.db.file;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.utils.FilesService;
import pl.futurecollars.invoicing.utils.JsonService;

public class FileBasedDatabase<I> implements Database {

  private final Path databasePath;
  private final IdService idService;
  private final FilesService filesService;
  private final JsonService jsonService;

  public FileBasedDatabase(Path databasePath, IdService idService, FilesService filesService, JsonService jsonService) {
    this.databasePath = databasePath;
    this.idService = idService;
    this.filesService = filesService;
    this.jsonService = jsonService;
  }

  @Override
  public int save(Invoice invoice) {
    try {
      invoice.setId(idService.getNextIdAndIncrement());
      filesService.appendLineToFile(databasePath, jsonService.toJson(invoice));

      return invoice.getId();
    } catch (Exception e) {
      throw new RuntimeException("Failed to save invoice in Database", e);
    }
  }

  @Override
  public Optional<Invoice> getById(int id) {
    try {
      return filesService.readAllLines(databasePath)
          .stream()
          .filter(line -> containsId(line, id))
          .map(line -> jsonService.toObject(line, Invoice.class))
          .findFirst();
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to get invoice from id - " + id + ", does not exist", e);
    }
  }

  @Override
  public List<Invoice> getAll() {
    try {
      return filesService.readAllLines(databasePath)
          .stream()
          .map(line -> jsonService.toObject(line, Invoice.class))
          .collect(Collectors.toList());
    } catch (Exception e) {
      throw new RuntimeException("Failed to read invoices from file", e);
    }
  }

  @Override
  public Optional<Invoice> update(int id, Invoice updatedInvoice) {
    try {
      List<String> allLines = filesService.readAllLines(databasePath);
      List<String> listWithoutInvoiceFromId = allLines
          .stream()
          .filter(line -> !containsId(line, id))
          .collect(Collectors.toList());
      Optional<Invoice> invoice = getById(id);
      if (invoice.isPresent()) {
        updatedInvoice.setId(id);
        listWithoutInvoiceFromId.add(jsonService.toJson(updatedInvoice));
      }
      allLines.removeAll(listWithoutInvoiceFromId);
      filesService.writeLinesToFile(databasePath, listWithoutInvoiceFromId);
      return allLines.isEmpty() ? Optional.empty() : Optional.of(jsonService.toObject(allLines.get(0), Invoice.class));
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to update invoice from id - " + id + ", does not exist");
    }
  }

  @Override
  public Optional<Invoice> delete(int id) {
    try {
      List<String> allLines = filesService.readAllLines(databasePath);
      List<String> listWithoutInvoiceFromId = allLines
          .stream()
          .filter(line -> !containsId(line, id))
          .collect(Collectors.toList());
      filesService.writeLinesToFile(databasePath, listWithoutInvoiceFromId);
      allLines.removeAll(listWithoutInvoiceFromId);

      return allLines.isEmpty() ? Optional.empty() : Optional.of(jsonService.toObject(allLines.get(0), Invoice.class));
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to delete invoice from id - " + id + ", does not exist", e);
    }
  }

  private boolean containsId(String line, int id) {
    return line.contains("{\"id\":" + id + ",");
  }
}
