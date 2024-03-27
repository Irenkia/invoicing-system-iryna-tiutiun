package pl.futurecollars.invoicing.db;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import pl.futurecollars.invoicing.model.Invoice;

@Repository
public interface Database<I> {

  int save(Invoice invoice);

  Optional<Invoice> getById(int id);

  List<Invoice> getAll();

  void update(int id, Invoice updatedInvoice);

  void delete(int id);
}
