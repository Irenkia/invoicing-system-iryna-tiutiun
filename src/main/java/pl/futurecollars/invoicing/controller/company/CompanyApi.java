package pl.futurecollars.invoicing.controller.company;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.futurecollars.invoicing.model.Company;

@CrossOrigin
@RequestMapping(value = "/companies", produces = {"application/json;charset=UTF-8"})
@Api(tags = {"company-controller"})
public interface CompanyApi {

  @ApiOperation(value = "Get list of all companies")
  @GetMapping
  List<Company> getAll();

  @ApiOperation(value = "Add new company to system")
  @PostMapping
  long addCompany(@RequestBody Company company);

  @ApiOperation(value = "Get company by id")
  @GetMapping(value = "/{id}")
  ResponseEntity<Optional<Company>> findById(@PathVariable Long id);

  @ApiOperation(value = "Update company with given id")
  @PutMapping("/{id}")
  ResponseEntity<Optional<Company>> update(@PathVariable Long id, @RequestBody Company company);

  @ApiOperation(value = "Delete company with given id")
  @DeleteMapping("/{id}")
  ResponseEntity<Optional<Company>> deleteById(@PathVariable Long id);
}
