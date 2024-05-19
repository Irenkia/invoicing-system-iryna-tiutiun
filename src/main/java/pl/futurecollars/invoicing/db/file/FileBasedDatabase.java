package pl.futurecollars.invoicing.db.file;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.WithId;
import pl.futurecollars.invoicing.utils.FilesService;
import pl.futurecollars.invoicing.utils.JsonService;

@AllArgsConstructor
public class FileBasedDatabase<T extends WithId> implements Database<T> {

  private final Path databasePath;
  private final IdService idService;
  private final FilesService filesService;
  private final JsonService jsonService;
  private final Class<T> clazz;

  @Override
  public Long save(T item) {
    try {
      item.setId(idService.getNextIdAndIncrement());
      filesService.appendLineToFile(databasePath, jsonService.toJson(item));

      return item.getId();
    } catch (Exception e) {
      throw new RuntimeException("Failed to save invoice in Database", e);
    }
  }

  @Override
  public Optional<T> getById(Long id) {
    try {
      return filesService.readAllLines(databasePath)
          .stream()
          .filter(line -> containsId(line, id))
          .map(line -> jsonService.toObject(line, clazz))
          .findFirst();
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to get invoice from id - " + id + ", does not exist", e);
    }
  }

  @Override
  public List<T> getAll() {
    try {
      return filesService.readAllLines(databasePath)
          .stream()
          .map(line -> jsonService.toObject(line, clazz))
          .collect(Collectors.toList());
    } catch (Exception e) {
      throw new RuntimeException("Failed to read invoices from file", e);
    }
  }

  @Override
  public Optional<T> update(Long id, T updateItem) {
    try {
      List<String> allLines = filesService.readAllLines(databasePath);
      List<String> listWithoutItemFromId = allLines
          .stream()
          .filter(line -> !containsId(line, id))
          .collect(Collectors.toList());
      Optional<T> item = getById(id);
      if (item.isPresent()) {
        updateItem.setId(id);
        listWithoutItemFromId.add(jsonService.toJson(updateItem));
      }
      allLines.removeAll(listWithoutItemFromId);
      filesService.writeLinesToFile(databasePath, listWithoutItemFromId);
      return allLines.isEmpty() ? Optional.empty() : Optional.of(jsonService.toObject(allLines.get(0), clazz));
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to update invoice from id - " + id + ", does not exist");
    }
  }

  @Override
  public Optional<T> delete(Long id) {
    try {
      List<String> allLines = filesService.readAllLines(databasePath);
      List<String> listWithoutItemFromId = allLines
          .stream()
          .filter(line -> !containsId(line, id))
          .collect(Collectors.toList());
      filesService.writeLinesToFile(databasePath, listWithoutItemFromId);
      allLines.removeAll(listWithoutItemFromId);

      return allLines.isEmpty() ? Optional.empty() : Optional.of(jsonService.toObject(allLines.get(0), clazz));
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to delete invoice from id - " + id + ", does not exist", e);
    }
  }

  private boolean containsId(String line, Long id) {
    return line.contains("{\"id\":" + id + ",");
  }

}
