package pl.futurecollars.invoicing.db.mongo;

import com.mongodb.client.MongoCollection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import org.bson.Document;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.WithId;

@AllArgsConstructor
public class MongoBasedDatabase<T extends WithId> implements Database<T> {

  private final MongoCollection<T> items;
  private final MongoIdProvider idProvider;

  @Override
  public Long save(T item) {
    item.setId(idProvider.getNextIdAndIncrement());
    items.insertOne(item);

    return item.getId();
  }

  @Override
  public Optional<T> getById(Long id) {
    return Optional.ofNullable(
        items.find(idFilter(id)).first()
    );
  }

  @Override
  public List<T> getAll() {
    return StreamSupport
        .stream(items.find().spliterator(), false)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<T> update(Long id, T updateItem) {
    updateItem.setId(id);
    return Optional.ofNullable(
        items.findOneAndReplace(idFilter(id), updateItem)
    );
  }

  @Override
  public Optional<T> delete(Long id) {
    return Optional.ofNullable(
        items.findOneAndDelete(idFilter(id))
    );
  }

  private Document idFilter(long id) {
    return new Document("_id", id);
  }

}
