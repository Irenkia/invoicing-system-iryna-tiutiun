package pl.futurecollars.invoicing.controller.company;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.service.company.CompanyService;

@RestController
@AllArgsConstructor
public class CompanyController implements CompanyApi {

  private final CompanyService companyService;

  @Override
  public List<Company> getAll() {
    return companyService.getAll();
  }

  @Override
  public long addCompany(@RequestBody Company company) {
    return companyService.save(company);
  }

  @Override
  public ResponseEntity<Optional<Company>> findById(@PathVariable("id") Long id) {
    return Optional.ofNullable(companyService.getById(id))
        .map(company -> ResponseEntity.ok().body(company))
        .orElse(ResponseEntity.notFound().build());
  }

  @Override
  public ResponseEntity<Optional<Company>> update(@PathVariable("id") Long id, @RequestBody Company company) {
    return Optional.of(companyService.update(id, company))
        .map(ResponseEntity.ok()::body)
        .orElse(ResponseEntity.notFound().build());
  }

  @Override
  public ResponseEntity<Optional<Company>> deleteById(@PathVariable("id") Long id) {
    return Optional.of(companyService.delete(id))
        .map(ResponseEntity.ok()::body)
        .orElse(ResponseEntity.notFound().build());
  }

}
