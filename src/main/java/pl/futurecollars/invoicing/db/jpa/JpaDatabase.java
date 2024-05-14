package pl.futurecollars.invoicing.db.jpa;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;

@AllArgsConstructor
public class JpaDatabase implements Database {

  private final InvoiceRepository invoiceRepository;

  @Override
  public Long save(Invoice invoice) {
    return invoiceRepository.save(invoice).getId();
  }

  @Override
  public Optional<Invoice> getById(Long id) {
    return invoiceRepository.findById(id);
  }

  @Override
  public List<Invoice> getAll() {
    return StreamSupport
        .stream(invoiceRepository.findAll().spliterator(), false)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<Invoice> update(Long id, Invoice updatedInvoice) {

    Optional<Invoice> invoiceOptional = getById(id);

    if (invoiceOptional.isPresent()) {
      Invoice invoice = invoiceOptional.get();

      updatedInvoice.setId(id); // just in case it was not set
      updatedInvoice.getBuyer().setId(invoice.getBuyer().getId());
      updatedInvoice.getSeller().setId(invoice.getSeller().getId());

      invoiceRepository.save(updatedInvoice);
    }

    return invoiceOptional;
  }

  @Override
  public Optional<Invoice> delete(Long id) {
    Optional<Invoice> invoice = getById(id);

    invoice.ifPresent(invoiceRepository::delete);

    return invoice;
  }
}
