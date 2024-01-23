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
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import nl.hanze.se4.automaat.IntegrationTest;
import nl.hanze.se4.automaat.domain.Car;
import nl.hanze.se4.automaat.domain.Employee;
import nl.hanze.se4.automaat.domain.Inspection;
import nl.hanze.se4.automaat.domain.InspectionPhoto;
import nl.hanze.se4.automaat.domain.Rental;
import nl.hanze.se4.automaat.domain.Repair;
import nl.hanze.se4.automaat.repository.InspectionRepository;
import nl.hanze.se4.automaat.service.InspectionService;
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
    private static final Long SMALLER_ODOMETER = 1L - 1L;

    private static final String DEFAULT_RESULT = "AAAAAAAAAA";
    private static final String UPDATED_RESULT = "BBBBBBBBBB";

    private static final byte[] DEFAULT_PHOTO = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PHOTO = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_PHOTO_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PHOTO_CONTENT_TYPE = "image/png";

    private static final ZonedDateTime DEFAULT_COMPLETED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_COMPLETED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_COMPLETED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/inspections";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private InspectionRepository inspectionRepository;

    @Mock
    private InspectionRepository inspectionRepositoryMock;

    @Mock
    private InspectionService inspectionServiceMock;

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
            .andExpect(jsonPath("$.[*].photo").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_PHOTO))))
            .andExpect(jsonPath("$.[*].completed").value(hasItem(sameInstant(DEFAULT_COMPLETED))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInspectionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(inspectionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInspectionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(inspectionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInspectionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(inspectionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

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
            .andExpect(jsonPath("$.photo").value(Base64.getEncoder().encodeToString(DEFAULT_PHOTO)))
            .andExpect(jsonPath("$.completed").value(sameInstant(DEFAULT_COMPLETED)));
    }

    @Test
    @Transactional
    void getInspectionsByIdFiltering() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        Long id = inspection.getId();

        defaultInspectionShouldBeFound("id.equals=" + id);
        defaultInspectionShouldNotBeFound("id.notEquals=" + id);

        defaultInspectionShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultInspectionShouldNotBeFound("id.greaterThan=" + id);

        defaultInspectionShouldBeFound("id.lessThanOrEqual=" + id);
        defaultInspectionShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllInspectionsByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where code equals to DEFAULT_CODE
        defaultInspectionShouldBeFound("code.equals=" + DEFAULT_CODE);

        // Get all the inspectionList where code equals to UPDATED_CODE
        defaultInspectionShouldNotBeFound("code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllInspectionsByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where code in DEFAULT_CODE or UPDATED_CODE
        defaultInspectionShouldBeFound("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE);

        // Get all the inspectionList where code equals to UPDATED_CODE
        defaultInspectionShouldNotBeFound("code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllInspectionsByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where code is not null
        defaultInspectionShouldBeFound("code.specified=true");

        // Get all the inspectionList where code is null
        defaultInspectionShouldNotBeFound("code.specified=false");
    }

    @Test
    @Transactional
    void getAllInspectionsByCodeContainsSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where code contains DEFAULT_CODE
        defaultInspectionShouldBeFound("code.contains=" + DEFAULT_CODE);

        // Get all the inspectionList where code contains UPDATED_CODE
        defaultInspectionShouldNotBeFound("code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllInspectionsByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where code does not contain DEFAULT_CODE
        defaultInspectionShouldNotBeFound("code.doesNotContain=" + DEFAULT_CODE);

        // Get all the inspectionList where code does not contain UPDATED_CODE
        defaultInspectionShouldBeFound("code.doesNotContain=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllInspectionsByOdometerIsEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where odometer equals to DEFAULT_ODOMETER
        defaultInspectionShouldBeFound("odometer.equals=" + DEFAULT_ODOMETER);

        // Get all the inspectionList where odometer equals to UPDATED_ODOMETER
        defaultInspectionShouldNotBeFound("odometer.equals=" + UPDATED_ODOMETER);
    }

    @Test
    @Transactional
    void getAllInspectionsByOdometerIsInShouldWork() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where odometer in DEFAULT_ODOMETER or UPDATED_ODOMETER
        defaultInspectionShouldBeFound("odometer.in=" + DEFAULT_ODOMETER + "," + UPDATED_ODOMETER);

        // Get all the inspectionList where odometer equals to UPDATED_ODOMETER
        defaultInspectionShouldNotBeFound("odometer.in=" + UPDATED_ODOMETER);
    }

    @Test
    @Transactional
    void getAllInspectionsByOdometerIsNullOrNotNull() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where odometer is not null
        defaultInspectionShouldBeFound("odometer.specified=true");

        // Get all the inspectionList where odometer is null
        defaultInspectionShouldNotBeFound("odometer.specified=false");
    }

    @Test
    @Transactional
    void getAllInspectionsByOdometerIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where odometer is greater than or equal to DEFAULT_ODOMETER
        defaultInspectionShouldBeFound("odometer.greaterThanOrEqual=" + DEFAULT_ODOMETER);

        // Get all the inspectionList where odometer is greater than or equal to UPDATED_ODOMETER
        defaultInspectionShouldNotBeFound("odometer.greaterThanOrEqual=" + UPDATED_ODOMETER);
    }

    @Test
    @Transactional
    void getAllInspectionsByOdometerIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where odometer is less than or equal to DEFAULT_ODOMETER
        defaultInspectionShouldBeFound("odometer.lessThanOrEqual=" + DEFAULT_ODOMETER);

        // Get all the inspectionList where odometer is less than or equal to SMALLER_ODOMETER
        defaultInspectionShouldNotBeFound("odometer.lessThanOrEqual=" + SMALLER_ODOMETER);
    }

    @Test
    @Transactional
    void getAllInspectionsByOdometerIsLessThanSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where odometer is less than DEFAULT_ODOMETER
        defaultInspectionShouldNotBeFound("odometer.lessThan=" + DEFAULT_ODOMETER);

        // Get all the inspectionList where odometer is less than UPDATED_ODOMETER
        defaultInspectionShouldBeFound("odometer.lessThan=" + UPDATED_ODOMETER);
    }

    @Test
    @Transactional
    void getAllInspectionsByOdometerIsGreaterThanSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where odometer is greater than DEFAULT_ODOMETER
        defaultInspectionShouldNotBeFound("odometer.greaterThan=" + DEFAULT_ODOMETER);

        // Get all the inspectionList where odometer is greater than SMALLER_ODOMETER
        defaultInspectionShouldBeFound("odometer.greaterThan=" + SMALLER_ODOMETER);
    }

    @Test
    @Transactional
    void getAllInspectionsByResultIsEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where result equals to DEFAULT_RESULT
        defaultInspectionShouldBeFound("result.equals=" + DEFAULT_RESULT);

        // Get all the inspectionList where result equals to UPDATED_RESULT
        defaultInspectionShouldNotBeFound("result.equals=" + UPDATED_RESULT);
    }

    @Test
    @Transactional
    void getAllInspectionsByResultIsInShouldWork() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where result in DEFAULT_RESULT or UPDATED_RESULT
        defaultInspectionShouldBeFound("result.in=" + DEFAULT_RESULT + "," + UPDATED_RESULT);

        // Get all the inspectionList where result equals to UPDATED_RESULT
        defaultInspectionShouldNotBeFound("result.in=" + UPDATED_RESULT);
    }

    @Test
    @Transactional
    void getAllInspectionsByResultIsNullOrNotNull() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where result is not null
        defaultInspectionShouldBeFound("result.specified=true");

        // Get all the inspectionList where result is null
        defaultInspectionShouldNotBeFound("result.specified=false");
    }

    @Test
    @Transactional
    void getAllInspectionsByResultContainsSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where result contains DEFAULT_RESULT
        defaultInspectionShouldBeFound("result.contains=" + DEFAULT_RESULT);

        // Get all the inspectionList where result contains UPDATED_RESULT
        defaultInspectionShouldNotBeFound("result.contains=" + UPDATED_RESULT);
    }

    @Test
    @Transactional
    void getAllInspectionsByResultNotContainsSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where result does not contain DEFAULT_RESULT
        defaultInspectionShouldNotBeFound("result.doesNotContain=" + DEFAULT_RESULT);

        // Get all the inspectionList where result does not contain UPDATED_RESULT
        defaultInspectionShouldBeFound("result.doesNotContain=" + UPDATED_RESULT);
    }

    @Test
    @Transactional
    void getAllInspectionsByCompletedIsEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where completed equals to DEFAULT_COMPLETED
        defaultInspectionShouldBeFound("completed.equals=" + DEFAULT_COMPLETED);

        // Get all the inspectionList where completed equals to UPDATED_COMPLETED
        defaultInspectionShouldNotBeFound("completed.equals=" + UPDATED_COMPLETED);
    }

    @Test
    @Transactional
    void getAllInspectionsByCompletedIsInShouldWork() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where completed in DEFAULT_COMPLETED or UPDATED_COMPLETED
        defaultInspectionShouldBeFound("completed.in=" + DEFAULT_COMPLETED + "," + UPDATED_COMPLETED);

        // Get all the inspectionList where completed equals to UPDATED_COMPLETED
        defaultInspectionShouldNotBeFound("completed.in=" + UPDATED_COMPLETED);
    }

    @Test
    @Transactional
    void getAllInspectionsByCompletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where completed is not null
        defaultInspectionShouldBeFound("completed.specified=true");

        // Get all the inspectionList where completed is null
        defaultInspectionShouldNotBeFound("completed.specified=false");
    }

    @Test
    @Transactional
    void getAllInspectionsByCompletedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where completed is greater than or equal to DEFAULT_COMPLETED
        defaultInspectionShouldBeFound("completed.greaterThanOrEqual=" + DEFAULT_COMPLETED);

        // Get all the inspectionList where completed is greater than or equal to UPDATED_COMPLETED
        defaultInspectionShouldNotBeFound("completed.greaterThanOrEqual=" + UPDATED_COMPLETED);
    }

    @Test
    @Transactional
    void getAllInspectionsByCompletedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where completed is less than or equal to DEFAULT_COMPLETED
        defaultInspectionShouldBeFound("completed.lessThanOrEqual=" + DEFAULT_COMPLETED);

        // Get all the inspectionList where completed is less than or equal to SMALLER_COMPLETED
        defaultInspectionShouldNotBeFound("completed.lessThanOrEqual=" + SMALLER_COMPLETED);
    }

    @Test
    @Transactional
    void getAllInspectionsByCompletedIsLessThanSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where completed is less than DEFAULT_COMPLETED
        defaultInspectionShouldNotBeFound("completed.lessThan=" + DEFAULT_COMPLETED);

        // Get all the inspectionList where completed is less than UPDATED_COMPLETED
        defaultInspectionShouldBeFound("completed.lessThan=" + UPDATED_COMPLETED);
    }

    @Test
    @Transactional
    void getAllInspectionsByCompletedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where completed is greater than DEFAULT_COMPLETED
        defaultInspectionShouldNotBeFound("completed.greaterThan=" + DEFAULT_COMPLETED);

        // Get all the inspectionList where completed is greater than SMALLER_COMPLETED
        defaultInspectionShouldBeFound("completed.greaterThan=" + SMALLER_COMPLETED);
    }

    @Test
    @Transactional
    void getAllInspectionsByPhotoIsEqualToSomething() throws Exception {
        InspectionPhoto photo;
        if (TestUtil.findAll(em, InspectionPhoto.class).isEmpty()) {
            inspectionRepository.saveAndFlush(inspection);
            photo = InspectionPhotoResourceIT.createEntity(em);
        } else {
            photo = TestUtil.findAll(em, InspectionPhoto.class).get(0);
        }
        em.persist(photo);
        em.flush();
        inspection.addPhoto(photo);
        inspectionRepository.saveAndFlush(inspection);
        Long photoId = photo.getId();
        // Get all the inspectionList where photo equals to photoId
        defaultInspectionShouldBeFound("photoId.equals=" + photoId);

        // Get all the inspectionList where photo equals to (photoId + 1)
        defaultInspectionShouldNotBeFound("photoId.equals=" + (photoId + 1));
    }

    @Test
    @Transactional
    void getAllInspectionsByRepairIsEqualToSomething() throws Exception {
        Repair repair;
        if (TestUtil.findAll(em, Repair.class).isEmpty()) {
            inspectionRepository.saveAndFlush(inspection);
            repair = RepairResourceIT.createEntity(em);
        } else {
            repair = TestUtil.findAll(em, Repair.class).get(0);
        }
        em.persist(repair);
        em.flush();
        inspection.addRepair(repair);
        inspectionRepository.saveAndFlush(inspection);
        Long repairId = repair.getId();
        // Get all the inspectionList where repair equals to repairId
        defaultInspectionShouldBeFound("repairId.equals=" + repairId);

        // Get all the inspectionList where repair equals to (repairId + 1)
        defaultInspectionShouldNotBeFound("repairId.equals=" + (repairId + 1));
    }

    @Test
    @Transactional
    void getAllInspectionsByCarIsEqualToSomething() throws Exception {
        Car car;
        if (TestUtil.findAll(em, Car.class).isEmpty()) {
            inspectionRepository.saveAndFlush(inspection);
            car = CarResourceIT.createEntity(em);
        } else {
            car = TestUtil.findAll(em, Car.class).get(0);
        }
        em.persist(car);
        em.flush();
        inspection.setCar(car);
        inspectionRepository.saveAndFlush(inspection);
        Long carId = car.getId();
        // Get all the inspectionList where car equals to carId
        defaultInspectionShouldBeFound("carId.equals=" + carId);

        // Get all the inspectionList where car equals to (carId + 1)
        defaultInspectionShouldNotBeFound("carId.equals=" + (carId + 1));
    }

    @Test
    @Transactional
    void getAllInspectionsByEmployeeIsEqualToSomething() throws Exception {
        Employee employee;
        if (TestUtil.findAll(em, Employee.class).isEmpty()) {
            inspectionRepository.saveAndFlush(inspection);
            employee = EmployeeResourceIT.createEntity(em);
        } else {
            employee = TestUtil.findAll(em, Employee.class).get(0);
        }
        em.persist(employee);
        em.flush();
        inspection.setEmployee(employee);
        inspectionRepository.saveAndFlush(inspection);
        Long employeeId = employee.getId();
        // Get all the inspectionList where employee equals to employeeId
        defaultInspectionShouldBeFound("employeeId.equals=" + employeeId);

        // Get all the inspectionList where employee equals to (employeeId + 1)
        defaultInspectionShouldNotBeFound("employeeId.equals=" + (employeeId + 1));
    }

    @Test
    @Transactional
    void getAllInspectionsByRentalIsEqualToSomething() throws Exception {
        Rental rental;
        if (TestUtil.findAll(em, Rental.class).isEmpty()) {
            inspectionRepository.saveAndFlush(inspection);
            rental = RentalResourceIT.createEntity(em);
        } else {
            rental = TestUtil.findAll(em, Rental.class).get(0);
        }
        em.persist(rental);
        em.flush();
        inspection.setRental(rental);
        inspectionRepository.saveAndFlush(inspection);
        Long rentalId = rental.getId();
        // Get all the inspectionList where rental equals to rentalId
        defaultInspectionShouldBeFound("rentalId.equals=" + rentalId);

        // Get all the inspectionList where rental equals to (rentalId + 1)
        defaultInspectionShouldNotBeFound("rentalId.equals=" + (rentalId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultInspectionShouldBeFound(String filter) throws Exception {
        restInspectionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(inspection.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].odometer").value(hasItem(DEFAULT_ODOMETER.intValue())))
            .andExpect(jsonPath("$.[*].result").value(hasItem(DEFAULT_RESULT)))
            .andExpect(jsonPath("$.[*].photoContentType").value(hasItem(DEFAULT_PHOTO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].photo").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_PHOTO))))
            .andExpect(jsonPath("$.[*].completed").value(hasItem(sameInstant(DEFAULT_COMPLETED))));

        // Check, that the count call also returns 1
        restInspectionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultInspectionShouldNotBeFound(String filter) throws Exception {
        restInspectionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restInspectionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
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
