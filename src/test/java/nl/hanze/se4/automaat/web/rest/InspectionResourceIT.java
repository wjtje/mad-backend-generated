package nl.hanze.se4.automaat.web.rest;

import static nl.hanze.se4.automaat.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import nl.hanze.se4.automaat.IntegrationTest;
import nl.hanze.se4.automaat.domain.Inspection;
import nl.hanze.se4.automaat.repository.InspectionRepository;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link InspectionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class InspectionResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final Long DEFAULT_ODOMETER = 1L;
    private static final Long UPDATED_ODOMETER = 2L;

    private static final String DEFAULT_RESULT = "AAAAAAAAAA";
    private static final String UPDATED_RESULT = "BBBBBBBBBB";

    private static final byte[] DEFAULT_PHOTO = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PHOTO = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_PHOTO_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PHOTO_CONTENT_TYPE = "image/png";

    private static final ZonedDateTime DEFAULT_COMPLETED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_COMPLETED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/inspections";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private InspectionRepository inspectionRepository;

    @Mock
    private InspectionRepository inspectionRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInspectionMockMvc;

    private Inspection inspection;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Inspection createEntity(EntityManager em) {
        Inspection inspection = new Inspection()
            .code(DEFAULT_CODE)
            .odometer(DEFAULT_ODOMETER)
            .result(DEFAULT_RESULT)
            .photo(DEFAULT_PHOTO)
            .photoContentType(DEFAULT_PHOTO_CONTENT_TYPE)
            .completed(DEFAULT_COMPLETED);
        return inspection;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Inspection createUpdatedEntity(EntityManager em) {
        Inspection inspection = new Inspection()
            .code(UPDATED_CODE)
            .odometer(UPDATED_ODOMETER)
            .result(UPDATED_RESULT)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .completed(UPDATED_COMPLETED);
        return inspection;
    }

    @BeforeEach
    public void initTest() {
        inspection = createEntity(em);
    }

    @Test
    @Transactional
    void createInspection() throws Exception {
        int databaseSizeBeforeCreate = inspectionRepository.findAll().size();
        // Create the Inspection
        restInspectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(inspection)))
            .andExpect(status().isCreated());

        // Validate the Inspection in the database
        List<Inspection> inspectionList = inspectionRepository.findAll();
        assertThat(inspectionList).hasSize(databaseSizeBeforeCreate + 1);
        Inspection testInspection = inspectionList.get(inspectionList.size() - 1);
        assertThat(testInspection.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testInspection.getOdometer()).isEqualTo(DEFAULT_ODOMETER);
        assertThat(testInspection.getResult()).isEqualTo(DEFAULT_RESULT);
        assertThat(testInspection.getPhoto()).isEqualTo(DEFAULT_PHOTO);
        assertThat(testInspection.getPhotoContentType()).isEqualTo(DEFAULT_PHOTO_CONTENT_TYPE);
        assertThat(testInspection.getCompleted()).isEqualTo(DEFAULT_COMPLETED);
    }

    @Test
    @Transactional
    void createInspectionWithExistingId() throws Exception {
        // Create the Inspection with an existing ID
        inspection.setId(1L);

        int databaseSizeBeforeCreate = inspectionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInspectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(inspection)))
            .andExpect(status().isBadRequest());

        // Validate the Inspection in the database
        List<Inspection> inspectionList = inspectionRepository.findAll();
        assertThat(inspectionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllInspections() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList
        restInspectionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(inspection.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].odometer").value(hasItem(DEFAULT_ODOMETER.intValue())))
            .andExpect(jsonPath("$.[*].result").value(hasItem(DEFAULT_RESULT)))
            .andExpect(jsonPath("$.[*].photoContentType").value(hasItem(DEFAULT_PHOTO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].photo").value(hasItem(Base64Utils.encodeToString(DEFAULT_PHOTO))))
            .andExpect(jsonPath("$.[*].completed").value(hasItem(sameInstant(DEFAULT_COMPLETED))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInspectionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(inspectionRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInspectionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(inspectionRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInspectionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(inspectionRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInspectionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(inspectionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getInspection() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get the inspection
        restInspectionMockMvc
            .perform(get(ENTITY_API_URL_ID, inspection.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(inspection.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.odometer").value(DEFAULT_ODOMETER.intValue()))
            .andExpect(jsonPath("$.result").value(DEFAULT_RESULT))
            .andExpect(jsonPath("$.photoContentType").value(DEFAULT_PHOTO_CONTENT_TYPE))
            .andExpect(jsonPath("$.photo").value(Base64Utils.encodeToString(DEFAULT_PHOTO)))
            .andExpect(jsonPath("$.completed").value(sameInstant(DEFAULT_COMPLETED)));
    }

    @Test
    @Transactional
    void getNonExistingInspection() throws Exception {
        // Get the inspection
        restInspectionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInspection() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        int databaseSizeBeforeUpdate = inspectionRepository.findAll().size();

        // Update the inspection
        Inspection updatedInspection = inspectionRepository.findById(inspection.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedInspection are not directly saved in db
        em.detach(updatedInspection);
        updatedInspection
            .code(UPDATED_CODE)
            .odometer(UPDATED_ODOMETER)
            .result(UPDATED_RESULT)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .completed(UPDATED_COMPLETED);

        restInspectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedInspection.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedInspection))
            )
            .andExpect(status().isOk());

        // Validate the Inspection in the database
        List<Inspection> inspectionList = inspectionRepository.findAll();
        assertThat(inspectionList).hasSize(databaseSizeBeforeUpdate);
        Inspection testInspection = inspectionList.get(inspectionList.size() - 1);
        assertThat(testInspection.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testInspection.getOdometer()).isEqualTo(UPDATED_ODOMETER);
        assertThat(testInspection.getResult()).isEqualTo(UPDATED_RESULT);
        assertThat(testInspection.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testInspection.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
        assertThat(testInspection.getCompleted()).isEqualTo(UPDATED_COMPLETED);
    }

    @Test
    @Transactional
    void putNonExistingInspection() throws Exception {
        int databaseSizeBeforeUpdate = inspectionRepository.findAll().size();
        inspection.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInspectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, inspection.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(inspection))
            )
            .andExpect(status().isBadRequest());

        // Validate the Inspection in the database
        List<Inspection> inspectionList = inspectionRepository.findAll();
        assertThat(inspectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInspection() throws Exception {
        int databaseSizeBeforeUpdate = inspectionRepository.findAll().size();
        inspection.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInspectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(inspection))
            )
            .andExpect(status().isBadRequest());

        // Validate the Inspection in the database
        List<Inspection> inspectionList = inspectionRepository.findAll();
        assertThat(inspectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInspection() throws Exception {
        int databaseSizeBeforeUpdate = inspectionRepository.findAll().size();
        inspection.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInspectionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(inspection)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Inspection in the database
        List<Inspection> inspectionList = inspectionRepository.findAll();
        assertThat(inspectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInspectionWithPatch() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        int databaseSizeBeforeUpdate = inspectionRepository.findAll().size();

        // Update the inspection using partial update
        Inspection partialUpdatedInspection = new Inspection();
        partialUpdatedInspection.setId(inspection.getId());

        partialUpdatedInspection.code(UPDATED_CODE).odometer(UPDATED_ODOMETER).result(UPDATED_RESULT);

        restInspectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInspection.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedInspection))
            )
            .andExpect(status().isOk());

        // Validate the Inspection in the database
        List<Inspection> inspectionList = inspectionRepository.findAll();
        assertThat(inspectionList).hasSize(databaseSizeBeforeUpdate);
        Inspection testInspection = inspectionList.get(inspectionList.size() - 1);
        assertThat(testInspection.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testInspection.getOdometer()).isEqualTo(UPDATED_ODOMETER);
        assertThat(testInspection.getResult()).isEqualTo(UPDATED_RESULT);
        assertThat(testInspection.getPhoto()).isEqualTo(DEFAULT_PHOTO);
        assertThat(testInspection.getPhotoContentType()).isEqualTo(DEFAULT_PHOTO_CONTENT_TYPE);
        assertThat(testInspection.getCompleted()).isEqualTo(DEFAULT_COMPLETED);
    }

    @Test
    @Transactional
    void fullUpdateInspectionWithPatch() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        int databaseSizeBeforeUpdate = inspectionRepository.findAll().size();

        // Update the inspection using partial update
        Inspection partialUpdatedInspection = new Inspection();
        partialUpdatedInspection.setId(inspection.getId());

        partialUpdatedInspection
            .code(UPDATED_CODE)
            .odometer(UPDATED_ODOMETER)
            .result(UPDATED_RESULT)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .completed(UPDATED_COMPLETED);

        restInspectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInspection.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedInspection))
            )
            .andExpect(status().isOk());

        // Validate the Inspection in the database
        List<Inspection> inspectionList = inspectionRepository.findAll();
        assertThat(inspectionList).hasSize(databaseSizeBeforeUpdate);
        Inspection testInspection = inspectionList.get(inspectionList.size() - 1);
        assertThat(testInspection.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testInspection.getOdometer()).isEqualTo(UPDATED_ODOMETER);
        assertThat(testInspection.getResult()).isEqualTo(UPDATED_RESULT);
        assertThat(testInspection.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testInspection.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
        assertThat(testInspection.getCompleted()).isEqualTo(UPDATED_COMPLETED);
    }

    @Test
    @Transactional
    void patchNonExistingInspection() throws Exception {
        int databaseSizeBeforeUpdate = inspectionRepository.findAll().size();
        inspection.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInspectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, inspection.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(inspection))
            )
            .andExpect(status().isBadRequest());

        // Validate the Inspection in the database
        List<Inspection> inspectionList = inspectionRepository.findAll();
        assertThat(inspectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInspection() throws Exception {
        int databaseSizeBeforeUpdate = inspectionRepository.findAll().size();
        inspection.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInspectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(inspection))
            )
            .andExpect(status().isBadRequest());

        // Validate the Inspection in the database
        List<Inspection> inspectionList = inspectionRepository.findAll();
        assertThat(inspectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInspection() throws Exception {
        int databaseSizeBeforeUpdate = inspectionRepository.findAll().size();
        inspection.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInspectionMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(inspection))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Inspection in the database
        List<Inspection> inspectionList = inspectionRepository.findAll();
        assertThat(inspectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInspection() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        int databaseSizeBeforeDelete = inspectionRepository.findAll().size();

        // Delete the inspection
        restInspectionMockMvc
            .perform(delete(ENTITY_API_URL_ID, inspection.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Inspection> inspectionList = inspectionRepository.findAll();
        assertThat(inspectionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
