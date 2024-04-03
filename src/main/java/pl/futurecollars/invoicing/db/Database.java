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

  Optional<Invoice> update(int id, Invoice updatedInvoice);

  Optional<Invoice> delete(int id);
}
