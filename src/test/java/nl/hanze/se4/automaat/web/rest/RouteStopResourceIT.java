package nl.hanze.se4.automaat.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import nl.hanze.se4.automaat.IntegrationTest;
import nl.hanze.se4.automaat.domain.RouteStop;
import nl.hanze.se4.automaat.repository.RouteStopRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link RouteStopResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RouteStopResourceIT {

    private static final Integer DEFAULT_NR = 1;
    private static final Integer UPDATED_NR = 2;

    private static final String ENTITY_API_URL = "/api/route-stops";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RouteStopRepository routeStopRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRouteStopMockMvc;

    private RouteStop routeStop;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RouteStop createEntity(EntityManager em) {
        RouteStop routeStop = new RouteStop().nr(DEFAULT_NR);
        return routeStop;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RouteStop createUpdatedEntity(EntityManager em) {
        RouteStop routeStop = new RouteStop().nr(UPDATED_NR);
        return routeStop;
    }

    @BeforeEach
    public void initTest() {
        routeStop = createEntity(em);
    }

    @Test
    @Transactional
    void createRouteStop() throws Exception {
        int databaseSizeBeforeCreate = routeStopRepository.findAll().size();
        // Create the RouteStop
        restRouteStopMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(routeStop)))
            .andExpect(status().isCreated());

        // Validate the RouteStop in the database
        List<RouteStop> routeStopList = routeStopRepository.findAll();
        assertThat(routeStopList).hasSize(databaseSizeBeforeCreate + 1);
        RouteStop testRouteStop = routeStopList.get(routeStopList.size() - 1);
        assertThat(testRouteStop.getNr()).isEqualTo(DEFAULT_NR);
    }

    @Test
    @Transactional
    void createRouteStopWithExistingId() throws Exception {
        // Create the RouteStop with an existing ID
        routeStop.setId(1L);

        int databaseSizeBeforeCreate = routeStopRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRouteStopMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(routeStop)))
            .andExpect(status().isBadRequest());

        // Validate the RouteStop in the database
        List<RouteStop> routeStopList = routeStopRepository.findAll();
        assertThat(routeStopList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllRouteStops() throws Exception {
        // Initialize the database
        routeStopRepository.saveAndFlush(routeStop);

        // Get all the routeStopList
        restRouteStopMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(routeStop.getId().intValue())))
            .andExpect(jsonPath("$.[*].nr").value(hasItem(DEFAULT_NR)));
    }

    @Test
    @Transactional
    void getRouteStop() throws Exception {
        // Initialize the database
        routeStopRepository.saveAndFlush(routeStop);

        // Get the routeStop
        restRouteStopMockMvc
            .perform(get(ENTITY_API_URL_ID, routeStop.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(routeStop.getId().intValue()))
            .andExpect(jsonPath("$.nr").value(DEFAULT_NR));
    }

    @Test
    @Transactional
    void getNonExistingRouteStop() throws Exception {
        // Get the routeStop
        restRouteStopMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRouteStop() throws Exception {
        // Initialize the database
        routeStopRepository.saveAndFlush(routeStop);

        int databaseSizeBeforeUpdate = routeStopRepository.findAll().size();

        // Update the routeStop
        RouteStop updatedRouteStop = routeStopRepository.findById(routeStop.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRouteStop are not directly saved in db
        em.detach(updatedRouteStop);
        updatedRouteStop.nr(UPDATED_NR);

        restRouteStopMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedRouteStop.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedRouteStop))
            )
            .andExpect(status().isOk());

        // Validate the RouteStop in the database
        List<RouteStop> routeStopList = routeStopRepository.findAll();
        assertThat(routeStopList).hasSize(databaseSizeBeforeUpdate);
        RouteStop testRouteStop = routeStopList.get(routeStopList.size() - 1);
        assertThat(testRouteStop.getNr()).isEqualTo(UPDATED_NR);
    }

    @Test
    @Transactional
    void putNonExistingRouteStop() throws Exception {
        int databaseSizeBeforeUpdate = routeStopRepository.findAll().size();
        routeStop.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRouteStopMockMvc
            .perform(
                put(ENTITY_API_URL_ID, routeStop.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(routeStop))
            )
            .andExpect(status().isBadRequest());

        // Validate the RouteStop in the database
        List<RouteStop> routeStopList = routeStopRepository.findAll();
        assertThat(routeStopList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRouteStop() throws Exception {
        int databaseSizeBeforeUpdate = routeStopRepository.findAll().size();
        routeStop.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRouteStopMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(routeStop))
            )
            .andExpect(status().isBadRequest());

        // Validate the RouteStop in the database
        List<RouteStop> routeStopList = routeStopRepository.findAll();
        assertThat(routeStopList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRouteStop() throws Exception {
        int databaseSizeBeforeUpdate = routeStopRepository.findAll().size();
        routeStop.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRouteStopMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(routeStop)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RouteStop in the database
        List<RouteStop> routeStopList = routeStopRepository.findAll();
        assertThat(routeStopList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRouteStopWithPatch() throws Exception {
        // Initialize the database
        routeStopRepository.saveAndFlush(routeStop);

        int databaseSizeBeforeUpdate = routeStopRepository.findAll().size();

        // Update the routeStop using partial update
        RouteStop partialUpdatedRouteStop = new RouteStop();
        partialUpdatedRouteStop.setId(routeStop.getId());

        restRouteStopMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRouteStop.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRouteStop))
            )
            .andExpect(status().isOk());

        // Validate the RouteStop in the database
        List<RouteStop> routeStopList = routeStopRepository.findAll();
        assertThat(routeStopList).hasSize(databaseSizeBeforeUpdate);
        RouteStop testRouteStop = routeStopList.get(routeStopList.size() - 1);
        assertThat(testRouteStop.getNr()).isEqualTo(DEFAULT_NR);
    }

    @Test
    @Transactional
    void fullUpdateRouteStopWithPatch() throws Exception {
        // Initialize the database
        routeStopRepository.saveAndFlush(routeStop);

        int databaseSizeBeforeUpdate = routeStopRepository.findAll().size();

        // Update the routeStop using partial update
        RouteStop partialUpdatedRouteStop = new RouteStop();
        partialUpdatedRouteStop.setId(routeStop.getId());

        partialUpdatedRouteStop.nr(UPDATED_NR);

        restRouteStopMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRouteStop.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRouteStop))
            )
            .andExpect(status().isOk());

        // Validate the RouteStop in the database
        List<RouteStop> routeStopList = routeStopRepository.findAll();
        assertThat(routeStopList).hasSize(databaseSizeBeforeUpdate);
        RouteStop testRouteStop = routeStopList.get(routeStopList.size() - 1);
        assertThat(testRouteStop.getNr()).isEqualTo(UPDATED_NR);
    }

    @Test
    @Transactional
    void patchNonExistingRouteStop() throws Exception {
        int databaseSizeBeforeUpdate = routeStopRepository.findAll().size();
        routeStop.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRouteStopMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, routeStop.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(routeStop))
            )
            .andExpect(status().isBadRequest());

        // Validate the RouteStop in the database
        List<RouteStop> routeStopList = routeStopRepository.findAll();
        assertThat(routeStopList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRouteStop() throws Exception {
        int databaseSizeBeforeUpdate = routeStopRepository.findAll().size();
        routeStop.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRouteStopMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(routeStop))
            )
            .andExpect(status().isBadRequest());

        // Validate the RouteStop in the database
        List<RouteStop> routeStopList = routeStopRepository.findAll();
        assertThat(routeStopList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRouteStop() throws Exception {
        int databaseSizeBeforeUpdate = routeStopRepository.findAll().size();
        routeStop.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRouteStopMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(routeStop))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the RouteStop in the database
        List<RouteStop> routeStopList = routeStopRepository.findAll();
        assertThat(routeStopList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRouteStop() throws Exception {
        // Initialize the database
        routeStopRepository.saveAndFlush(routeStop);

        int databaseSizeBeforeDelete = routeStopRepository.findAll().size();

        // Delete the routeStop
        restRouteStopMockMvc
            .perform(delete(ENTITY_API_URL_ID, routeStop.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<RouteStop> routeStopList = routeStopRepository.findAll();
        assertThat(routeStopList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
