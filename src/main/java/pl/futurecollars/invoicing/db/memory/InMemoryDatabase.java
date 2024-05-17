package pl.futurecollars.invoicing.db.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.NoArgsConstructor;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.WithId;

@NoArgsConstructor
public class InMemoryDatabase<T extends WithId> implements Database<T> {

  private final Map<Long, T> items = new HashMap<>();
  private Long nextId = 1L;

  @Override
  public Long save(T item) {
    item.setId(nextId);
    items.put(nextId, item);
    return nextId++;
  }

  @Override
  public Optional<T> getById(Long id) {
    return Optional.ofNullable(items.get(id));
  }

  @Override
  public List<T> getAll() {
    return new ArrayList<>(items.values());
  }

  @Override
  public Optional<T> update(Long id, T updateItem) {
    Optional<T> item = Optional.ofNullable(items.get(id));
    if (items.containsKey(id)) {
      updateItem.setId(id);
      items.put(id, updateItem);
    }
    return item;
  }

  @Override
  public Optional<T> delete(Long id) {
    return Optional.ofNullable(items.remove(id));
  }

}
