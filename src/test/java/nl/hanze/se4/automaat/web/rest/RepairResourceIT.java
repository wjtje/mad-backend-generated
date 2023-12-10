package nl.hanze.se4.automaat.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import nl.hanze.se4.automaat.IntegrationTest;
import nl.hanze.se4.automaat.domain.Repair;
import nl.hanze.se4.automaat.domain.enumeration.RepairStatus;
import nl.hanze.se4.automaat.repository.RepairRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link RepairResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class RepairResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final RepairStatus DEFAULT_REPAIR_STATUS = RepairStatus.PLANNED;
    private static final RepairStatus UPDATED_REPAIR_STATUS = RepairStatus.DOING;

    private static final LocalDate DEFAULT_DATE_COMPLETED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_COMPLETED = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/repairs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RepairRepository repairRepository;

    @Mock
    private RepairRepository repairRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRepairMockMvc;

    private Repair repair;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Repair createEntity(EntityManager em) {
        Repair repair = new Repair()
            .description(DEFAULT_DESCRIPTION)
            .repairStatus(DEFAULT_REPAIR_STATUS)
            .dateCompleted(DEFAULT_DATE_COMPLETED);
        return repair;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Repair createUpdatedEntity(EntityManager em) {
        Repair repair = new Repair()
            .description(UPDATED_DESCRIPTION)
            .repairStatus(UPDATED_REPAIR_STATUS)
            .dateCompleted(UPDATED_DATE_COMPLETED);
        return repair;
    }

    @BeforeEach
    public void initTest() {
        repair = createEntity(em);
    }

    @Test
    @Transactional
    void createRepair() throws Exception {
        int databaseSizeBeforeCreate = repairRepository.findAll().size();
        // Create the Repair
        restRepairMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(repair)))
            .andExpect(status().isCreated());

        // Validate the Repair in the database
        List<Repair> repairList = repairRepository.findAll();
        assertThat(repairList).hasSize(databaseSizeBeforeCreate + 1);
        Repair testRepair = repairList.get(repairList.size() - 1);
        assertThat(testRepair.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testRepair.getRepairStatus()).isEqualTo(DEFAULT_REPAIR_STATUS);
        assertThat(testRepair.getDateCompleted()).isEqualTo(DEFAULT_DATE_COMPLETED);
    }

    @Test
    @Transactional
    void createRepairWithExistingId() throws Exception {
        // Create the Repair with an existing ID
        repair.setId(1L);

        int databaseSizeBeforeCreate = repairRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRepairMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(repair)))
            .andExpect(status().isBadRequest());

        // Validate the Repair in the database
        List<Repair> repairList = repairRepository.findAll();
        assertThat(repairList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllRepairs() throws Exception {
        // Initialize the database
        repairRepository.saveAndFlush(repair);

        // Get all the repairList
        restRepairMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(repair.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].repairStatus").value(hasItem(DEFAULT_REPAIR_STATUS.toString())))
            .andExpect(jsonPath("$.[*].dateCompleted").value(hasItem(DEFAULT_DATE_COMPLETED.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRepairsWithEagerRelationshipsIsEnabled() throws Exception {
        when(repairRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRepairMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(repairRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRepairsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(repairRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRepairMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(repairRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getRepair() throws Exception {
        // Initialize the database
        repairRepository.saveAndFlush(repair);

        // Get the repair
        restRepairMockMvc
            .perform(get(ENTITY_API_URL_ID, repair.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(repair.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.repairStatus").value(DEFAULT_REPAIR_STATUS.toString()))
            .andExpect(jsonPath("$.dateCompleted").value(DEFAULT_DATE_COMPLETED.toString()));
    }

    @Test
    @Transactional
    void getNonExistingRepair() throws Exception {
        // Get the repair
        restRepairMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRepair() throws Exception {
        // Initialize the database
        repairRepository.saveAndFlush(repair);

        int databaseSizeBeforeUpdate = repairRepository.findAll().size();

        // Update the repair
        Repair updatedRepair = repairRepository.findById(repair.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRepair are not directly saved in db
        em.detach(updatedRepair);
        updatedRepair.description(UPDATED_DESCRIPTION).repairStatus(UPDATED_REPAIR_STATUS).dateCompleted(UPDATED_DATE_COMPLETED);

        restRepairMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedRepair.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedRepair))
            )
            .andExpect(status().isOk());

        // Validate the Repair in the database
        List<Repair> repairList = repairRepository.findAll();
        assertThat(repairList).hasSize(databaseSizeBeforeUpdate);
        Repair testRepair = repairList.get(repairList.size() - 1);
        assertThat(testRepair.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testRepair.getRepairStatus()).isEqualTo(UPDATED_REPAIR_STATUS);
        assertThat(testRepair.getDateCompleted()).isEqualTo(UPDATED_DATE_COMPLETED);
    }

    @Test
    @Transactional
    void putNonExistingRepair() throws Exception {
        int databaseSizeBeforeUpdate = repairRepository.findAll().size();
        repair.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRepairMockMvc
            .perform(
                put(ENTITY_API_URL_ID, repair.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(repair))
            )
            .andExpect(status().isBadRequest());

        // Validate the Repair in the database
        List<Repair> repairList = repairRepository.findAll();
        assertThat(repairList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRepair() throws Exception {
        int databaseSizeBeforeUpdate = repairRepository.findAll().size();
        repair.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRepairMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(repair))
            )
            .andExpect(status().isBadRequest());

        // Validate the Repair in the database
        List<Repair> repairList = repairRepository.findAll();
        assertThat(repairList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRepair() throws Exception {
        int databaseSizeBeforeUpdate = repairRepository.findAll().size();
        repair.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRepairMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(repair)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Repair in the database
        List<Repair> repairList = repairRepository.findAll();
        assertThat(repairList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRepairWithPatch() throws Exception {
        // Initialize the database
        repairRepository.saveAndFlush(repair);

        int databaseSizeBeforeUpdate = repairRepository.findAll().size();

        // Update the repair using partial update
        Repair partialUpdatedRepair = new Repair();
        partialUpdatedRepair.setId(repair.getId());

        partialUpdatedRepair.description(UPDATED_DESCRIPTION).repairStatus(UPDATED_REPAIR_STATUS);

        restRepairMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRepair.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRepair))
            )
            .andExpect(status().isOk());

        // Validate the Repair in the database
        List<Repair> repairList = repairRepository.findAll();
        assertThat(repairList).hasSize(databaseSizeBeforeUpdate);
        Repair testRepair = repairList.get(repairList.size() - 1);
        assertThat(testRepair.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testRepair.getRepairStatus()).isEqualTo(UPDATED_REPAIR_STATUS);
        assertThat(testRepair.getDateCompleted()).isEqualTo(DEFAULT_DATE_COMPLETED);
    }

    @Test
    @Transactional
    void fullUpdateRepairWithPatch() throws Exception {
        // Initialize the database
        repairRepository.saveAndFlush(repair);

        int databaseSizeBeforeUpdate = repairRepository.findAll().size();

        // Update the repair using partial update
        Repair partialUpdatedRepair = new Repair();
        partialUpdatedRepair.setId(repair.getId());

        partialUpdatedRepair.description(UPDATED_DESCRIPTION).repairStatus(UPDATED_REPAIR_STATUS).dateCompleted(UPDATED_DATE_COMPLETED);

        restRepairMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRepair.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRepair))
            )
            .andExpect(status().isOk());

        // Validate the Repair in the database
        List<Repair> repairList = repairRepository.findAll();
        assertThat(repairList).hasSize(databaseSizeBeforeUpdate);
        Repair testRepair = repairList.get(repairList.size() - 1);
        assertThat(testRepair.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testRepair.getRepairStatus()).isEqualTo(UPDATED_REPAIR_STATUS);
        assertThat(testRepair.getDateCompleted()).isEqualTo(UPDATED_DATE_COMPLETED);
    }

    @Test
    @Transactional
    void patchNonExistingRepair() throws Exception {
        int databaseSizeBeforeUpdate = repairRepository.findAll().size();
        repair.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRepairMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, repair.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(repair))
            )
            .andExpect(status().isBadRequest());

        // Validate the Repair in the database
        List<Repair> repairList = repairRepository.findAll();
        assertThat(repairList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRepair() throws Exception {
        int databaseSizeBeforeUpdate = repairRepository.findAll().size();
        repair.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRepairMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(repair))
            )
            .andExpect(status().isBadRequest());

        // Validate the Repair in the database
        List<Repair> repairList = repairRepository.findAll();
        assertThat(repairList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRepair() throws Exception {
        int databaseSizeBeforeUpdate = repairRepository.findAll().size();
        repair.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRepairMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(repair)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Repair in the database
        List<Repair> repairList = repairRepository.findAll();
        assertThat(repairList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRepair() throws Exception {
        // Initialize the database
        repairRepository.saveAndFlush(repair);

        int databaseSizeBeforeDelete = repairRepository.findAll().size();

        // Delete the repair
        restRepairMockMvc
            .perform(delete(ENTITY_API_URL_ID, repair.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Repair> repairList = repairRepository.findAll();
        assertThat(repairList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
