package pl.futurecollars.invoicing.controller.company;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
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
  public long add(Company company) {
    return companyService.save(company);
  }

  @Override
  public ResponseEntity<Company> getById(Long id) {
    return companyService.getById(id)
        .map(company -> ResponseEntity.ok().body(company))
        .orElse(ResponseEntity.notFound().build());
  }

  @Override
  public ResponseEntity<?> update(Long id, Company company) {
    return companyService.update(id, company)
        .map(name -> ResponseEntity.noContent().build())
        .orElse(ResponseEntity.notFound().build());
  }

  @Override
  public ResponseEntity<?> deleteById(Long id) {
    return companyService.delete(id)
        .map(name -> ResponseEntity.noContent().build())
        .orElse(ResponseEntity.notFound().build());
  }

}
