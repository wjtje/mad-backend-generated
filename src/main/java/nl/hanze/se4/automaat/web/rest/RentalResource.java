package nl.hanze.se4.automaat.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import nl.hanze.se4.automaat.domain.Rental;
import nl.hanze.se4.automaat.repository.RentalRepository;
import nl.hanze.se4.automaat.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link nl.hanze.se4.automaat.domain.Rental}.
 */
@RestController
@RequestMapping("/api/rentals")
@Transactional
public class RentalResource {

    private final Logger log = LoggerFactory.getLogger(RentalResource.class);

    private static final String ENTITY_NAME = "rental";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RentalRepository rentalRepository;

    public RentalResource(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    /**
     * {@code POST  /rentals} : Create a new rental.
     *
     * @param rental the rental to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rental, or with status {@code 400 (Bad Request)} if the rental has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Rental> createRental(@RequestBody Rental rental) throws URISyntaxException {
        log.debug("REST request to save Rental : {}", rental);
        if (rental.getId() != null) {
            throw new BadRequestAlertException("A new rental cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Rental result = rentalRepository.save(rental);
        return ResponseEntity
            .created(new URI("/api/rentals/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /rentals/:id} : Updates an existing rental.
     *
     * @param id the id of the rental to save.
     * @param rental the rental to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rental,
     * or with status {@code 400 (Bad Request)} if the rental is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rental couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Rental> updateRental(@PathVariable(value = "id", required = false) final Long id, @RequestBody Rental rental)
        throws URISyntaxException {
        log.debug("REST request to update Rental : {}, {}", id, rental);
        if (rental.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rental.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!rentalRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Rental result = rentalRepository.save(rental);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, rental.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /rentals/:id} : Partial updates given fields of an existing rental, field will ignore if it is null
     *
     * @param id the id of the rental to save.
     * @param rental the rental to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rental,
     * or with status {@code 400 (Bad Request)} if the rental is not valid,
     * or with status {@code 404 (Not Found)} if the rental is not found,
     * or with status {@code 500 (Internal Server Error)} if the rental couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Rental> partialUpdateRental(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Rental rental
    ) throws URISyntaxException {
        log.debug("REST request to partial update Rental partially : {}, {}", id, rental);
        if (rental.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rental.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!rentalRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Rental> result = rentalRepository
            .findById(rental.getId())
            .map(existingRental -> {
                if (rental.getCode() != null) {
                    existingRental.setCode(rental.getCode());
                }
                if (rental.getLongitude() != null) {
                    existingRental.setLongitude(rental.getLongitude());
                }
                if (rental.getLatitude() != null) {
                    existingRental.setLatitude(rental.getLatitude());
                }
                if (rental.getFromDate() != null) {
                    existingRental.setFromDate(rental.getFromDate());
                }
                if (rental.getToDate() != null) {
                    existingRental.setToDate(rental.getToDate());
                }
                if (rental.getState() != null) {
                    existingRental.setState(rental.getState());
                }

                return existingRental;
            })
            .map(rentalRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, rental.getId().toString())
        );
    }

    /**
     * {@code GET  /rentals} : get all the rentals.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rentals in body.
     */
    @GetMapping("")
    public List<Rental> getAllRentals(@RequestParam(required = false, defaultValue = "true") boolean eagerload) {
        log.debug("REST request to get all Rentals");
        if (eagerload) {
            return rentalRepository.findAllWithEagerRelationships();
        } else {
            return rentalRepository.findAll();
        }
    }

    /**
     * {@code GET  /rentals/:id} : get the "id" rental.
     *
     * @param id the id of the rental to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rental, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Rental> getRental(@PathVariable Long id) {
        log.debug("REST request to get Rental : {}", id);
        Optional<Rental> rental = rentalRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(rental);
    }

    /**
     * {@code DELETE  /rentals/:id} : delete the "id" rental.
     *
     * @param id the id of the rental to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRental(@PathVariable Long id) {
        log.debug("REST request to delete Rental : {}", id);
        rentalRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
