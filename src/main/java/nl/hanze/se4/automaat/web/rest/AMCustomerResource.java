package nl.hanze.se4.automaat.web.rest;

import nl.hanze.se4.automaat.domain.Customer;
import nl.hanze.se4.automaat.domain.User;
import nl.hanze.se4.automaat.repository.AMCustomerRepository;
import nl.hanze.se4.automaat.repository.UserRepository;
import nl.hanze.se4.automaat.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST controller for managing {@link Customer}.
 */
@RestController
@RequestMapping("/api/AM")
@Transactional
public class AMCustomerResource {

    private final Logger log = LoggerFactory.getLogger(AMCustomerResource.class);

    private static final String ENTITY_NAME = "customer";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AMCustomerRepository customerRepository;
    private final UserRepository userRepository;

    public AMCustomerResource(AMCustomerRepository customerRepository, UserRepository userRepository) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    /**
     * {@code GET  /customers/:id} : get the "id" customer.
     *
     * @param id the id of the customer to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the customer, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/me")
    public ResponseEntity<Customer> getCurrentCustomer() {
        log.debug("REST request to get the logged in Customer");
        User user = userRepository
            .findOneByLogin(SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED)))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Customer customer = customerRepository
            .findOneWithUserId(user.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok().body(customer);
    }
}
