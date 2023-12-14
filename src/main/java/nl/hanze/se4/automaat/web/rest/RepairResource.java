package nl.hanze.se4.automaat.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import nl.hanze.se4.automaat.domain.Repair;
import nl.hanze.se4.automaat.repository.RepairRepository;
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
 * REST controller for managing {@link nl.hanze.se4.automaat.domain.Repair}.
 */
@RestController
@RequestMapping("/api/repairs")
@Transactional
public class RepairResource {

    private final Logger log = LoggerFactory.getLogger(RepairResource.class);

    private static final String ENTITY_NAME = "repair";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RepairRepository repairRepository;

    public RepairResource(RepairRepository repairRepository) {
        this.repairRepository = repairRepository;
    }

    /**
     * {@code POST  /repairs} : Create a new repair.
     *
     * @param repair the repair to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new repair, or with status {@code 400 (Bad Request)} if the repair has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Repair> createRepair(@RequestBody Repair repair) throws URISyntaxException {
        log.debug("REST request to save Repair : {}", repair);
        if (repair.getId() != null) {
            throw new BadRequestAlertException("A new repair cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Repair result = repairRepository.save(repair);
        return ResponseEntity
            .created(new URI("/api/repairs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /repairs/:id} : Updates an existing repair.
     *
     * @param id the id of the repair to save.
     * @param repair the repair to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated repair,
     * or with status {@code 400 (Bad Request)} if the repair is not valid,
     * or with status {@code 500 (Internal Server Error)} if the repair couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Repair> updateRepair(@PathVariable(value = "id", required = false) final Long id, @RequestBody Repair repair)
        throws URISyntaxException {
        log.debug("REST request to update Repair : {}, {}", id, repair);
        if (repair.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, repair.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!repairRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Repair result = repairRepository.save(repair);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, repair.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /repairs/:id} : Partial updates given fields of an existing repair, field will ignore if it is null
     *
     * @param id the id of the repair to save.
     * @param repair the repair to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated repair,
     * or with status {@code 400 (Bad Request)} if the repair is not valid,
     * or with status {@code 404 (Not Found)} if the repair is not found,
     * or with status {@code 500 (Internal Server Error)} if the repair couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Repair> partialUpdateRepair(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Repair repair
    ) throws URISyntaxException {
        log.debug("REST request to partial update Repair partially : {}, {}", id, repair);
        if (repair.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, repair.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!repairRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Repair> result = repairRepository
            .findById(repair.getId())
            .map(existingRepair -> {
                if (repair.getDescription() != null) {
                    existingRepair.setDescription(repair.getDescription());
                }
                if (repair.getRepairStatus() != null) {
                    existingRepair.setRepairStatus(repair.getRepairStatus());
                }
                if (repair.getDateCompleted() != null) {
                    existingRepair.setDateCompleted(repair.getDateCompleted());
                }

                return existingRepair;
            })
            .map(repairRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, repair.getId().toString())
        );
    }

    /**
     * {@code GET  /repairs} : get all the repairs.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of repairs in body.
     */
    @GetMapping("")
    public List<Repair> getAllRepairs(@RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload) {
        log.debug("REST request to get all Repairs");
        if (eagerload) {
            return repairRepository.findAllWithEagerRelationships();
        } else {
            return repairRepository.findAll();
        }
    }

    /**
     * {@code GET  /repairs/:id} : get the "id" repair.
     *
     * @param id the id of the repair to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the repair, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Repair> getRepair(@PathVariable("id") Long id) {
        log.debug("REST request to get Repair : {}", id);
        Optional<Repair> repair = repairRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(repair);
    }

    /**
     * {@code DELETE  /repairs/:id} : delete the "id" repair.
     *
     * @param id the id of the repair to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRepair(@PathVariable("id") Long id) {
        log.debug("REST request to delete Repair : {}", id);
        repairRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
