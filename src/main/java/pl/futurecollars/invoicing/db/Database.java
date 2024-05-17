package pl.futurecollars.invoicing.db;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import pl.futurecollars.invoicing.model.WithId;

@Repository
public interface Database<T extends WithId> {

  Long save(T item);

  Optional<T> getById(Long id);

  List<T> getAll();

  Optional<T> update(Long id, T updateItem);

  Optional<T> delete(Long id);

  default void reset() {
    getAll().forEach(invoice -> delete(invoice.getId()));
  }

}
