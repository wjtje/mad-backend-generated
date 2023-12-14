package nl.hanze.se4.automaat.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.persistence.EntityManager;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import nl.hanze.se4.automaat.IntegrationTest;
import nl.hanze.se4.automaat.domain.InspectionPhoto;
import nl.hanze.se4.automaat.repository.InspectionPhotoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link InspectionPhotoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class InspectionPhotoResourceIT {

    private static final byte[] DEFAULT_PHOTO = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PHOTO = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_PHOTO_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PHOTO_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/inspection-photos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private InspectionPhotoRepository inspectionPhotoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInspectionPhotoMockMvc;

    private InspectionPhoto inspectionPhoto;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InspectionPhoto createEntity(EntityManager em) {
        InspectionPhoto inspectionPhoto = new InspectionPhoto().photo(DEFAULT_PHOTO).photoContentType(DEFAULT_PHOTO_CONTENT_TYPE);
        return inspectionPhoto;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InspectionPhoto createUpdatedEntity(EntityManager em) {
        InspectionPhoto inspectionPhoto = new InspectionPhoto().photo(UPDATED_PHOTO).photoContentType(UPDATED_PHOTO_CONTENT_TYPE);
        return inspectionPhoto;
    }

    @BeforeEach
    public void initTest() {
        inspectionPhoto = createEntity(em);
    }

    @Test
    @Transactional
    void createInspectionPhoto() throws Exception {
        int databaseSizeBeforeCreate = inspectionPhotoRepository.findAll().size();
        // Create the InspectionPhoto
        restInspectionPhotoMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(inspectionPhoto))
            )
            .andExpect(status().isCreated());

        // Validate the InspectionPhoto in the database
        List<InspectionPhoto> inspectionPhotoList = inspectionPhotoRepository.findAll();
        assertThat(inspectionPhotoList).hasSize(databaseSizeBeforeCreate + 1);
        InspectionPhoto testInspectionPhoto = inspectionPhotoList.get(inspectionPhotoList.size() - 1);
        assertThat(testInspectionPhoto.getPhoto()).isEqualTo(DEFAULT_PHOTO);
        assertThat(testInspectionPhoto.getPhotoContentType()).isEqualTo(DEFAULT_PHOTO_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void createInspectionPhotoWithExistingId() throws Exception {
        // Create the InspectionPhoto with an existing ID
        inspectionPhoto.setId(1L);

        int databaseSizeBeforeCreate = inspectionPhotoRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInspectionPhotoMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(inspectionPhoto))
            )
            .andExpect(status().isBadRequest());

        // Validate the InspectionPhoto in the database
        List<InspectionPhoto> inspectionPhotoList = inspectionPhotoRepository.findAll();
        assertThat(inspectionPhotoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllInspectionPhotos() throws Exception {
        // Initialize the database
        inspectionPhotoRepository.saveAndFlush(inspectionPhoto);

        // Get all the inspectionPhotoList
        restInspectionPhotoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(inspectionPhoto.getId().intValue())))
            .andExpect(jsonPath("$.[*].photoContentType").value(hasItem(DEFAULT_PHOTO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].photo").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_PHOTO))));
    }

    @Test
    @Transactional
    void getInspectionPhoto() throws Exception {
        // Initialize the database
        inspectionPhotoRepository.saveAndFlush(inspectionPhoto);

        // Get the inspectionPhoto
        restInspectionPhotoMockMvc
            .perform(get(ENTITY_API_URL_ID, inspectionPhoto.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(inspectionPhoto.getId().intValue()))
            .andExpect(jsonPath("$.photoContentType").value(DEFAULT_PHOTO_CONTENT_TYPE))
            .andExpect(jsonPath("$.photo").value(Base64.getEncoder().encodeToString(DEFAULT_PHOTO)));
    }

    @Test
    @Transactional
    void getNonExistingInspectionPhoto() throws Exception {
        // Get the inspectionPhoto
        restInspectionPhotoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInspectionPhoto() throws Exception {
        // Initialize the database
        inspectionPhotoRepository.saveAndFlush(inspectionPhoto);

        int databaseSizeBeforeUpdate = inspectionPhotoRepository.findAll().size();

        // Update the inspectionPhoto
        InspectionPhoto updatedInspectionPhoto = inspectionPhotoRepository.findById(inspectionPhoto.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedInspectionPhoto are not directly saved in db
        em.detach(updatedInspectionPhoto);
        updatedInspectionPhoto.photo(UPDATED_PHOTO).photoContentType(UPDATED_PHOTO_CONTENT_TYPE);

        restInspectionPhotoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedInspectionPhoto.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedInspectionPhoto))
            )
            .andExpect(status().isOk());

        // Validate the InspectionPhoto in the database
        List<InspectionPhoto> inspectionPhotoList = inspectionPhotoRepository.findAll();
        assertThat(inspectionPhotoList).hasSize(databaseSizeBeforeUpdate);
        InspectionPhoto testInspectionPhoto = inspectionPhotoList.get(inspectionPhotoList.size() - 1);
        assertThat(testInspectionPhoto.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testInspectionPhoto.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingInspectionPhoto() throws Exception {
        int databaseSizeBeforeUpdate = inspectionPhotoRepository.findAll().size();
        inspectionPhoto.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInspectionPhotoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, inspectionPhoto.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(inspectionPhoto))
            )
            .andExpect(status().isBadRequest());

        // Validate the InspectionPhoto in the database
        List<InspectionPhoto> inspectionPhotoList = inspectionPhotoRepository.findAll();
        assertThat(inspectionPhotoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInspectionPhoto() throws Exception {
        int databaseSizeBeforeUpdate = inspectionPhotoRepository.findAll().size();
        inspectionPhoto.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInspectionPhotoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(inspectionPhoto))
            )
            .andExpect(status().isBadRequest());

        // Validate the InspectionPhoto in the database
        List<InspectionPhoto> inspectionPhotoList = inspectionPhotoRepository.findAll();
        assertThat(inspectionPhotoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInspectionPhoto() throws Exception {
        int databaseSizeBeforeUpdate = inspectionPhotoRepository.findAll().size();
        inspectionPhoto.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInspectionPhotoMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(inspectionPhoto))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the InspectionPhoto in the database
        List<InspectionPhoto> inspectionPhotoList = inspectionPhotoRepository.findAll();
        assertThat(inspectionPhotoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInspectionPhotoWithPatch() throws Exception {
        // Initialize the database
        inspectionPhotoRepository.saveAndFlush(inspectionPhoto);

        int databaseSizeBeforeUpdate = inspectionPhotoRepository.findAll().size();

        // Update the inspectionPhoto using partial update
        InspectionPhoto partialUpdatedInspectionPhoto = new InspectionPhoto();
        partialUpdatedInspectionPhoto.setId(inspectionPhoto.getId());

        restInspectionPhotoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInspectionPhoto.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedInspectionPhoto))
            )
            .andExpect(status().isOk());

        // Validate the InspectionPhoto in the database
        List<InspectionPhoto> inspectionPhotoList = inspectionPhotoRepository.findAll();
        assertThat(inspectionPhotoList).hasSize(databaseSizeBeforeUpdate);
        InspectionPhoto testInspectionPhoto = inspectionPhotoList.get(inspectionPhotoList.size() - 1);
        assertThat(testInspectionPhoto.getPhoto()).isEqualTo(DEFAULT_PHOTO);
        assertThat(testInspectionPhoto.getPhotoContentType()).isEqualTo(DEFAULT_PHOTO_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateInspectionPhotoWithPatch() throws Exception {
        // Initialize the database
        inspectionPhotoRepository.saveAndFlush(inspectionPhoto);

        int databaseSizeBeforeUpdate = inspectionPhotoRepository.findAll().size();

        // Update the inspectionPhoto using partial update
        InspectionPhoto partialUpdatedInspectionPhoto = new InspectionPhoto();
        partialUpdatedInspectionPhoto.setId(inspectionPhoto.getId());

        partialUpdatedInspectionPhoto.photo(UPDATED_PHOTO).photoContentType(UPDATED_PHOTO_CONTENT_TYPE);

        restInspectionPhotoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInspectionPhoto.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedInspectionPhoto))
            )
            .andExpect(status().isOk());

        // Validate the InspectionPhoto in the database
        List<InspectionPhoto> inspectionPhotoList = inspectionPhotoRepository.findAll();
        assertThat(inspectionPhotoList).hasSize(databaseSizeBeforeUpdate);
        InspectionPhoto testInspectionPhoto = inspectionPhotoList.get(inspectionPhotoList.size() - 1);
        assertThat(testInspectionPhoto.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testInspectionPhoto.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingInspectionPhoto() throws Exception {
        int databaseSizeBeforeUpdate = inspectionPhotoRepository.findAll().size();
        inspectionPhoto.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInspectionPhotoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, inspectionPhoto.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(inspectionPhoto))
            )
            .andExpect(status().isBadRequest());

        // Validate the InspectionPhoto in the database
        List<InspectionPhoto> inspectionPhotoList = inspectionPhotoRepository.findAll();
        assertThat(inspectionPhotoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInspectionPhoto() throws Exception {
        int databaseSizeBeforeUpdate = inspectionPhotoRepository.findAll().size();
        inspectionPhoto.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInspectionPhotoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(inspectionPhoto))
            )
            .andExpect(status().isBadRequest());

        // Validate the InspectionPhoto in the database
        List<InspectionPhoto> inspectionPhotoList = inspectionPhotoRepository.findAll();
        assertThat(inspectionPhotoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInspectionPhoto() throws Exception {
        int databaseSizeBeforeUpdate = inspectionPhotoRepository.findAll().size();
        inspectionPhoto.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInspectionPhotoMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(inspectionPhoto))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the InspectionPhoto in the database
        List<InspectionPhoto> inspectionPhotoList = inspectionPhotoRepository.findAll();
        assertThat(inspectionPhotoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInspectionPhoto() throws Exception {
        // Initialize the database
        inspectionPhotoRepository.saveAndFlush(inspectionPhoto);

        int databaseSizeBeforeDelete = inspectionPhotoRepository.findAll().size();

        // Delete the inspectionPhoto
        restInspectionPhotoMockMvc
            .perform(delete(ENTITY_API_URL_ID, inspectionPhoto.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<InspectionPhoto> inspectionPhotoList = inspectionPhotoRepository.findAll();
        assertThat(inspectionPhotoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
