package nl.hanze.se4.automaat.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import nl.hanze.se4.automaat.domain.RouteStop;
import nl.hanze.se4.automaat.repository.RouteStopRepository;
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
 * REST controller for managing {@link nl.hanze.se4.automaat.domain.RouteStop}.
 */
@RestController
@RequestMapping("/api/route-stops")
@Transactional
public class RouteStopResource {

    private final Logger log = LoggerFactory.getLogger(RouteStopResource.class);

    private static final String ENTITY_NAME = "routeStop";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RouteStopRepository routeStopRepository;

    public RouteStopResource(RouteStopRepository routeStopRepository) {
        this.routeStopRepository = routeStopRepository;
    }

    /**
     * {@code POST  /route-stops} : Create a new routeStop.
     *
     * @param routeStop the routeStop to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new routeStop, or with status {@code 400 (Bad Request)} if the routeStop has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<RouteStop> createRouteStop(@RequestBody RouteStop routeStop) throws URISyntaxException {
        log.debug("REST request to save RouteStop : {}", routeStop);
        if (routeStop.getId() != null) {
            throw new BadRequestAlertException("A new routeStop cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RouteStop result = routeStopRepository.save(routeStop);
        return ResponseEntity
            .created(new URI("/api/route-stops/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /route-stops/:id} : Updates an existing routeStop.
     *
     * @param id the id of the routeStop to save.
     * @param routeStop the routeStop to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated routeStop,
     * or with status {@code 400 (Bad Request)} if the routeStop is not valid,
     * or with status {@code 500 (Internal Server Error)} if the routeStop couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RouteStop> updateRouteStop(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody RouteStop routeStop
    ) throws URISyntaxException {
        log.debug("REST request to update RouteStop : {}, {}", id, routeStop);
        if (routeStop.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, routeStop.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!routeStopRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        RouteStop result = routeStopRepository.save(routeStop);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, routeStop.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /route-stops/:id} : Partial updates given fields of an existing routeStop, field will ignore if it is null
     *
     * @param id the id of the routeStop to save.
     * @param routeStop the routeStop to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated routeStop,
     * or with status {@code 400 (Bad Request)} if the routeStop is not valid,
     * or with status {@code 404 (Not Found)} if the routeStop is not found,
     * or with status {@code 500 (Internal Server Error)} if the routeStop couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RouteStop> partialUpdateRouteStop(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody RouteStop routeStop
    ) throws URISyntaxException {
        log.debug("REST request to partial update RouteStop partially : {}, {}", id, routeStop);
        if (routeStop.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, routeStop.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!routeStopRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RouteStop> result = routeStopRepository
            .findById(routeStop.getId())
            .map(existingRouteStop -> {
                if (routeStop.getNr() != null) {
                    existingRouteStop.setNr(routeStop.getNr());
                }

                return existingRouteStop;
            })
            .map(routeStopRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, routeStop.getId().toString())
        );
    }

    /**
     * {@code GET  /route-stops} : get all the routeStops.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of routeStops in body.
     */
    @GetMapping("")
    public List<RouteStop> getAllRouteStops() {
        log.debug("REST request to get all RouteStops");
        return routeStopRepository.findAll();
    }

    /**
     * {@code GET  /route-stops/:id} : get the "id" routeStop.
     *
     * @param id the id of the routeStop to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the routeStop, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RouteStop> getRouteStop(@PathVariable Long id) {
        log.debug("REST request to get RouteStop : {}", id);
        Optional<RouteStop> routeStop = routeStopRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(routeStop);
    }

    /**
     * {@code DELETE  /route-stops/:id} : delete the "id" routeStop.
     *
     * @param id the id of the routeStop to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRouteStop(@PathVariable Long id) {
        log.debug("REST request to delete RouteStop : {}", id);
        routeStopRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
