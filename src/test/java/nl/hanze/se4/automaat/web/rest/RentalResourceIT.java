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
import nl.hanze.se4.automaat.domain.Car;
import nl.hanze.se4.automaat.domain.Customer;
import nl.hanze.se4.automaat.domain.Inspection;
import nl.hanze.se4.automaat.domain.Rental;
import nl.hanze.se4.automaat.domain.enumeration.RentalState;
import nl.hanze.se4.automaat.repository.RentalRepository;
import nl.hanze.se4.automaat.service.RentalService;
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
 * Integration tests for the {@link RentalResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class RentalResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final Float DEFAULT_LONGITUDE = 1F;
    private static final Float UPDATED_LONGITUDE = 2F;
    private static final Float SMALLER_LONGITUDE = 1F - 1F;

    private static final Float DEFAULT_LATITUDE = 1F;
    private static final Float UPDATED_LATITUDE = 2F;
    private static final Float SMALLER_LATITUDE = 1F - 1F;

    private static final LocalDate DEFAULT_FROM_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FROM_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_FROM_DATE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_TO_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_TO_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_TO_DATE = LocalDate.ofEpochDay(-1L);

    private static final RentalState DEFAULT_STATE = RentalState.ACTIVE;
    private static final RentalState UPDATED_STATE = RentalState.RESERVED;

    private static final String ENTITY_API_URL = "/api/rentals";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RentalRepository rentalRepository;

    @Mock
    private RentalRepository rentalRepositoryMock;

    @Mock
    private RentalService rentalServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRentalMockMvc;

    private Rental rental;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Rental createEntity(EntityManager em) {
        Rental rental = new Rental()
            .code(DEFAULT_CODE)
            .longitude(DEFAULT_LONGITUDE)
            .latitude(DEFAULT_LATITUDE)
            .fromDate(DEFAULT_FROM_DATE)
            .toDate(DEFAULT_TO_DATE)
            .state(DEFAULT_STATE);
        return rental;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Rental createUpdatedEntity(EntityManager em) {
        Rental rental = new Rental()
            .code(UPDATED_CODE)
            .longitude(UPDATED_LONGITUDE)
            .latitude(UPDATED_LATITUDE)
            .fromDate(UPDATED_FROM_DATE)
            .toDate(UPDATED_TO_DATE)
            .state(UPDATED_STATE);
        return rental;
    }

    @BeforeEach
    public void initTest() {
        rental = createEntity(em);
    }

    @Test
    @Transactional
    void createRental() throws Exception {
        int databaseSizeBeforeCreate = rentalRepository.findAll().size();
        // Create the Rental
        restRentalMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(rental)))
            .andExpect(status().isCreated());

        // Validate the Rental in the database
        List<Rental> rentalList = rentalRepository.findAll();
        assertThat(rentalList).hasSize(databaseSizeBeforeCreate + 1);
        Rental testRental = rentalList.get(rentalList.size() - 1);
        assertThat(testRental.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testRental.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
        assertThat(testRental.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
        assertThat(testRental.getFromDate()).isEqualTo(DEFAULT_FROM_DATE);
        assertThat(testRental.getToDate()).isEqualTo(DEFAULT_TO_DATE);
        assertThat(testRental.getState()).isEqualTo(DEFAULT_STATE);
    }

    @Test
    @Transactional
    void createRentalWithExistingId() throws Exception {
        // Create the Rental with an existing ID
        rental.setId(1L);

        int databaseSizeBeforeCreate = rentalRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRentalMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(rental)))
            .andExpect(status().isBadRequest());

        // Validate the Rental in the database
        List<Rental> rentalList = rentalRepository.findAll();
        assertThat(rentalList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllRentals() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList
        restRentalMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rental.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].fromDate").value(hasItem(DEFAULT_FROM_DATE.toString())))
            .andExpect(jsonPath("$.[*].toDate").value(hasItem(DEFAULT_TO_DATE.toString())))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRentalsWithEagerRelationshipsIsEnabled() throws Exception {
        when(rentalServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRentalMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(rentalServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRentalsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(rentalServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRentalMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(rentalRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getRental() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get the rental
        restRentalMockMvc
            .perform(get(ENTITY_API_URL_ID, rental.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(rental.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE.doubleValue()))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE.doubleValue()))
            .andExpect(jsonPath("$.fromDate").value(DEFAULT_FROM_DATE.toString()))
            .andExpect(jsonPath("$.toDate").value(DEFAULT_TO_DATE.toString()))
            .andExpect(jsonPath("$.state").value(DEFAULT_STATE.toString()));
    }

    @Test
    @Transactional
    void getRentalsByIdFiltering() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        Long id = rental.getId();

        defaultRentalShouldBeFound("id.equals=" + id);
        defaultRentalShouldNotBeFound("id.notEquals=" + id);

        defaultRentalShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultRentalShouldNotBeFound("id.greaterThan=" + id);

        defaultRentalShouldBeFound("id.lessThanOrEqual=" + id);
        defaultRentalShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRentalsByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList where code equals to DEFAULT_CODE
        defaultRentalShouldBeFound("code.equals=" + DEFAULT_CODE);

        // Get all the rentalList where code equals to UPDATED_CODE
        defaultRentalShouldNotBeFound("code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllRentalsByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList where code in DEFAULT_CODE or UPDATED_CODE
        defaultRentalShouldBeFound("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE);

        // Get all the rentalList where code equals to UPDATED_CODE
        defaultRentalShouldNotBeFound("code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllRentalsByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList where code is not null
        defaultRentalShouldBeFound("code.specified=true");

        // Get all the rentalList where code is null
        defaultRentalShouldNotBeFound("code.specified=false");
    }

    @Test
    @Transactional
    void getAllRentalsByCodeContainsSomething() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList where code contains DEFAULT_CODE
        defaultRentalShouldBeFound("code.contains=" + DEFAULT_CODE);

        // Get all the rentalList where code contains UPDATED_CODE
        defaultRentalShouldNotBeFound("code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllRentalsByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList where code does not contain DEFAULT_CODE
        defaultRentalShouldNotBeFound("code.doesNotContain=" + DEFAULT_CODE);

        // Get all the rentalList where code does not contain UPDATED_CODE
        defaultRentalShouldBeFound("code.doesNotContain=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllRentalsByLongitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList where longitude equals to DEFAULT_LONGITUDE
        defaultRentalShouldBeFound("longitude.equals=" + DEFAULT_LONGITUDE);

        // Get all the rentalList where longitude equals to UPDATED_LONGITUDE
        defaultRentalShouldNotBeFound("longitude.equals=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllRentalsByLongitudeIsInShouldWork() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList where longitude in DEFAULT_LONGITUDE or UPDATED_LONGITUDE
        defaultRentalShouldBeFound("longitude.in=" + DEFAULT_LONGITUDE + "," + UPDATED_LONGITUDE);

        // Get all the rentalList where longitude equals to UPDATED_LONGITUDE
        defaultRentalShouldNotBeFound("longitude.in=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllRentalsByLongitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList where longitude is not null
        defaultRentalShouldBeFound("longitude.specified=true");

        // Get all the rentalList where longitude is null
        defaultRentalShouldNotBeFound("longitude.specified=false");
    }

    @Test
    @Transactional
    void getAllRentalsByLongitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList where longitude is greater than or equal to DEFAULT_LONGITUDE
        defaultRentalShouldBeFound("longitude.greaterThanOrEqual=" + DEFAULT_LONGITUDE);

        // Get all the rentalList where longitude is greater than or equal to UPDATED_LONGITUDE
        defaultRentalShouldNotBeFound("longitude.greaterThanOrEqual=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllRentalsByLongitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList where longitude is less than or equal to DEFAULT_LONGITUDE
        defaultRentalShouldBeFound("longitude.lessThanOrEqual=" + DEFAULT_LONGITUDE);

        // Get all the rentalList where longitude is less than or equal to SMALLER_LONGITUDE
        defaultRentalShouldNotBeFound("longitude.lessThanOrEqual=" + SMALLER_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllRentalsByLongitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList where longitude is less than DEFAULT_LONGITUDE
        defaultRentalShouldNotBeFound("longitude.lessThan=" + DEFAULT_LONGITUDE);

        // Get all the rentalList where longitude is less than UPDATED_LONGITUDE
        defaultRentalShouldBeFound("longitude.lessThan=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllRentalsByLongitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList where longitude is greater than DEFAULT_LONGITUDE
        defaultRentalShouldNotBeFound("longitude.greaterThan=" + DEFAULT_LONGITUDE);

        // Get all the rentalList where longitude is greater than SMALLER_LONGITUDE
        defaultRentalShouldBeFound("longitude.greaterThan=" + SMALLER_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllRentalsByLatitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList where latitude equals to DEFAULT_LATITUDE
        defaultRentalShouldBeFound("latitude.equals=" + DEFAULT_LATITUDE);

        // Get all the rentalList where latitude equals to UPDATED_LATITUDE
        defaultRentalShouldNotBeFound("latitude.equals=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllRentalsByLatitudeIsInShouldWork() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList where latitude in DEFAULT_LATITUDE or UPDATED_LATITUDE
        defaultRentalShouldBeFound("latitude.in=" + DEFAULT_LATITUDE + "," + UPDATED_LATITUDE);

        // Get all the rentalList where latitude equals to UPDATED_LATITUDE
        defaultRentalShouldNotBeFound("latitude.in=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllRentalsByLatitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList where latitude is not null
        defaultRentalShouldBeFound("latitude.specified=true");

        // Get all the rentalList where latitude is null
        defaultRentalShouldNotBeFound("latitude.specified=false");
    }

    @Test
    @Transactional
    void getAllRentalsByLatitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList where latitude is greater than or equal to DEFAULT_LATITUDE
        defaultRentalShouldBeFound("latitude.greaterThanOrEqual=" + DEFAULT_LATITUDE);

        // Get all the rentalList where latitude is greater than or equal to UPDATED_LATITUDE
        defaultRentalShouldNotBeFound("latitude.greaterThanOrEqual=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllRentalsByLatitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList where latitude is less than or equal to DEFAULT_LATITUDE
        defaultRentalShouldBeFound("latitude.lessThanOrEqual=" + DEFAULT_LATITUDE);

        // Get all the rentalList where latitude is less than or equal to SMALLER_LATITUDE
        defaultRentalShouldNotBeFound("latitude.lessThanOrEqual=" + SMALLER_LATITUDE);
    }

    @Test
    @Transactional
    void getAllRentalsByLatitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList where latitude is less than DEFAULT_LATITUDE
        defaultRentalShouldNotBeFound("latitude.lessThan=" + DEFAULT_LATITUDE);

        // Get all the rentalList where latitude is less than UPDATED_LATITUDE
        defaultRentalShouldBeFound("latitude.lessThan=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllRentalsByLatitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList where latitude is greater than DEFAULT_LATITUDE
        defaultRentalShouldNotBeFound("latitude.greaterThan=" + DEFAULT_LATITUDE);

        // Get all the rentalList where latitude is greater than SMALLER_LATITUDE
        defaultRentalShouldBeFound("latitude.greaterThan=" + SMALLER_LATITUDE);
    }

    @Test
    @Transactional
    void getAllRentalsByFromDateIsEqualToSomething() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList where fromDate equals to DEFAULT_FROM_DATE
        defaultRentalShouldBeFound("fromDate.equals=" + DEFAULT_FROM_DATE);

        // Get all the rentalList where fromDate equals to UPDATED_FROM_DATE
        defaultRentalShouldNotBeFound("fromDate.equals=" + UPDATED_FROM_DATE);
    }

    @Test
    @Transactional
    void getAllRentalsByFromDateIsInShouldWork() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList where fromDate in DEFAULT_FROM_DATE or UPDATED_FROM_DATE
        defaultRentalShouldBeFound("fromDate.in=" + DEFAULT_FROM_DATE + "," + UPDATED_FROM_DATE);

        // Get all the rentalList where fromDate equals to UPDATED_FROM_DATE
        defaultRentalShouldNotBeFound("fromDate.in=" + UPDATED_FROM_DATE);
    }

    @Test
    @Transactional
    void getAllRentalsByFromDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList where fromDate is not null
        defaultRentalShouldBeFound("fromDate.specified=true");

        // Get all the rentalList where fromDate is null
        defaultRentalShouldNotBeFound("fromDate.specified=false");
    }

    @Test
    @Transactional
    void getAllRentalsByFromDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList where fromDate is greater than or equal to DEFAULT_FROM_DATE
        defaultRentalShouldBeFound("fromDate.greaterThanOrEqual=" + DEFAULT_FROM_DATE);

        // Get all the rentalList where fromDate is greater than or equal to UPDATED_FROM_DATE
        defaultRentalShouldNotBeFound("fromDate.greaterThanOrEqual=" + UPDATED_FROM_DATE);
    }

    @Test
    @Transactional
    void getAllRentalsByFromDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList where fromDate is less than or equal to DEFAULT_FROM_DATE
        defaultRentalShouldBeFound("fromDate.lessThanOrEqual=" + DEFAULT_FROM_DATE);

        // Get all the rentalList where fromDate is less than or equal to SMALLER_FROM_DATE
        defaultRentalShouldNotBeFound("fromDate.lessThanOrEqual=" + SMALLER_FROM_DATE);
    }

    @Test
    @Transactional
    void getAllRentalsByFromDateIsLessThanSomething() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList where fromDate is less than DEFAULT_FROM_DATE
        defaultRentalShouldNotBeFound("fromDate.lessThan=" + DEFAULT_FROM_DATE);

        // Get all the rentalList where fromDate is less than UPDATED_FROM_DATE
        defaultRentalShouldBeFound("fromDate.lessThan=" + UPDATED_FROM_DATE);
    }

    @Test
    @Transactional
    void getAllRentalsByFromDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList where fromDate is greater than DEFAULT_FROM_DATE
        defaultRentalShouldNotBeFound("fromDate.greaterThan=" + DEFAULT_FROM_DATE);

        // Get all the rentalList where fromDate is greater than SMALLER_FROM_DATE
        defaultRentalShouldBeFound("fromDate.greaterThan=" + SMALLER_FROM_DATE);
    }

    @Test
    @Transactional
    void getAllRentalsByToDateIsEqualToSomething() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList where toDate equals to DEFAULT_TO_DATE
        defaultRentalShouldBeFound("toDate.equals=" + DEFAULT_TO_DATE);

        // Get all the rentalList where toDate equals to UPDATED_TO_DATE
        defaultRentalShouldNotBeFound("toDate.equals=" + UPDATED_TO_DATE);
    }

    @Test
    @Transactional
    void getAllRentalsByToDateIsInShouldWork() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList where toDate in DEFAULT_TO_DATE or UPDATED_TO_DATE
        defaultRentalShouldBeFound("toDate.in=" + DEFAULT_TO_DATE + "," + UPDATED_TO_DATE);

        // Get all the rentalList where toDate equals to UPDATED_TO_DATE
        defaultRentalShouldNotBeFound("toDate.in=" + UPDATED_TO_DATE);
    }

    @Test
    @Transactional
    void getAllRentalsByToDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList where toDate is not null
        defaultRentalShouldBeFound("toDate.specified=true");

        // Get all the rentalList where toDate is null
        defaultRentalShouldNotBeFound("toDate.specified=false");
    }

    @Test
    @Transactional
    void getAllRentalsByToDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList where toDate is greater than or equal to DEFAULT_TO_DATE
        defaultRentalShouldBeFound("toDate.greaterThanOrEqual=" + DEFAULT_TO_DATE);

        // Get all the rentalList where toDate is greater than or equal to UPDATED_TO_DATE
        defaultRentalShouldNotBeFound("toDate.greaterThanOrEqual=" + UPDATED_TO_DATE);
    }

    @Test
    @Transactional
    void getAllRentalsByToDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList where toDate is less than or equal to DEFAULT_TO_DATE
        defaultRentalShouldBeFound("toDate.lessThanOrEqual=" + DEFAULT_TO_DATE);

        // Get all the rentalList where toDate is less than or equal to SMALLER_TO_DATE
        defaultRentalShouldNotBeFound("toDate.lessThanOrEqual=" + SMALLER_TO_DATE);
    }

    @Test
    @Transactional
    void getAllRentalsByToDateIsLessThanSomething() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList where toDate is less than DEFAULT_TO_DATE
        defaultRentalShouldNotBeFound("toDate.lessThan=" + DEFAULT_TO_DATE);

        // Get all the rentalList where toDate is less than UPDATED_TO_DATE
        defaultRentalShouldBeFound("toDate.lessThan=" + UPDATED_TO_DATE);
    }

    @Test
    @Transactional
    void getAllRentalsByToDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList where toDate is greater than DEFAULT_TO_DATE
        defaultRentalShouldNotBeFound("toDate.greaterThan=" + DEFAULT_TO_DATE);

        // Get all the rentalList where toDate is greater than SMALLER_TO_DATE
        defaultRentalShouldBeFound("toDate.greaterThan=" + SMALLER_TO_DATE);
    }

    @Test
    @Transactional
    void getAllRentalsByStateIsEqualToSomething() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList where state equals to DEFAULT_STATE
        defaultRentalShouldBeFound("state.equals=" + DEFAULT_STATE);

        // Get all the rentalList where state equals to UPDATED_STATE
        defaultRentalShouldNotBeFound("state.equals=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    void getAllRentalsByStateIsInShouldWork() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList where state in DEFAULT_STATE or UPDATED_STATE
        defaultRentalShouldBeFound("state.in=" + DEFAULT_STATE + "," + UPDATED_STATE);

        // Get all the rentalList where state equals to UPDATED_STATE
        defaultRentalShouldNotBeFound("state.in=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    void getAllRentalsByStateIsNullOrNotNull() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        // Get all the rentalList where state is not null
        defaultRentalShouldBeFound("state.specified=true");

        // Get all the rentalList where state is null
        defaultRentalShouldNotBeFound("state.specified=false");
    }

    @Test
    @Transactional
    void getAllRentalsByInspectionIsEqualToSomething() throws Exception {
        Inspection inspection;
        if (TestUtil.findAll(em, Inspection.class).isEmpty()) {
            rentalRepository.saveAndFlush(rental);
            inspection = InspectionResourceIT.createEntity(em);
        } else {
            inspection = TestUtil.findAll(em, Inspection.class).get(0);
        }
        em.persist(inspection);
        em.flush();
        rental.addInspection(inspection);
        rentalRepository.saveAndFlush(rental);
        Long inspectionId = inspection.getId();
        // Get all the rentalList where inspection equals to inspectionId
        defaultRentalShouldBeFound("inspectionId.equals=" + inspectionId);

        // Get all the rentalList where inspection equals to (inspectionId + 1)
        defaultRentalShouldNotBeFound("inspectionId.equals=" + (inspectionId + 1));
    }

    @Test
    @Transactional
    void getAllRentalsByCustomerIsEqualToSomething() throws Exception {
        Customer customer;
        if (TestUtil.findAll(em, Customer.class).isEmpty()) {
            rentalRepository.saveAndFlush(rental);
            customer = CustomerResourceIT.createEntity(em);
        } else {
            customer = TestUtil.findAll(em, Customer.class).get(0);
        }
        em.persist(customer);
        em.flush();
        rental.setCustomer(customer);
        rentalRepository.saveAndFlush(rental);
        Long customerId = customer.getId();
        // Get all the rentalList where customer equals to customerId
        defaultRentalShouldBeFound("customerId.equals=" + customerId);

        // Get all the rentalList where customer equals to (customerId + 1)
        defaultRentalShouldNotBeFound("customerId.equals=" + (customerId + 1));
    }

    @Test
    @Transactional
    void getAllRentalsByCarIsEqualToSomething() throws Exception {
        Car car;
        if (TestUtil.findAll(em, Car.class).isEmpty()) {
            rentalRepository.saveAndFlush(rental);
            car = CarResourceIT.createEntity(em);
        } else {
            car = TestUtil.findAll(em, Car.class).get(0);
        }
        em.persist(car);
        em.flush();
        rental.setCar(car);
        rentalRepository.saveAndFlush(rental);
        Long carId = car.getId();
        // Get all the rentalList where car equals to carId
        defaultRentalShouldBeFound("carId.equals=" + carId);

        // Get all the rentalList where car equals to (carId + 1)
        defaultRentalShouldNotBeFound("carId.equals=" + (carId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRentalShouldBeFound(String filter) throws Exception {
        restRentalMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rental.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].fromDate").value(hasItem(DEFAULT_FROM_DATE.toString())))
            .andExpect(jsonPath("$.[*].toDate").value(hasItem(DEFAULT_TO_DATE.toString())))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE.toString())));

        // Check, that the count call also returns 1
        restRentalMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRentalShouldNotBeFound(String filter) throws Exception {
        restRentalMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRentalMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingRental() throws Exception {
        // Get the rental
        restRentalMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRental() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        int databaseSizeBeforeUpdate = rentalRepository.findAll().size();

        // Update the rental
        Rental updatedRental = rentalRepository.findById(rental.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRental are not directly saved in db
        em.detach(updatedRental);
        updatedRental
            .code(UPDATED_CODE)
            .longitude(UPDATED_LONGITUDE)
            .latitude(UPDATED_LATITUDE)
            .fromDate(UPDATED_FROM_DATE)
            .toDate(UPDATED_TO_DATE)
            .state(UPDATED_STATE);

        restRentalMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedRental.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedRental))
            )
            .andExpect(status().isOk());

        // Validate the Rental in the database
        List<Rental> rentalList = rentalRepository.findAll();
        assertThat(rentalList).hasSize(databaseSizeBeforeUpdate);
        Rental testRental = rentalList.get(rentalList.size() - 1);
        assertThat(testRental.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testRental.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testRental.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testRental.getFromDate()).isEqualTo(UPDATED_FROM_DATE);
        assertThat(testRental.getToDate()).isEqualTo(UPDATED_TO_DATE);
        assertThat(testRental.getState()).isEqualTo(UPDATED_STATE);
    }

    @Test
    @Transactional
    void putNonExistingRental() throws Exception {
        int databaseSizeBeforeUpdate = rentalRepository.findAll().size();
        rental.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRentalMockMvc
            .perform(
                put(ENTITY_API_URL_ID, rental.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rental))
            )
            .andExpect(status().isBadRequest());

        // Validate the Rental in the database
        List<Rental> rentalList = rentalRepository.findAll();
        assertThat(rentalList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRental() throws Exception {
        int databaseSizeBeforeUpdate = rentalRepository.findAll().size();
        rental.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRentalMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rental))
            )
            .andExpect(status().isBadRequest());

        // Validate the Rental in the database
        List<Rental> rentalList = rentalRepository.findAll();
        assertThat(rentalList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRental() throws Exception {
        int databaseSizeBeforeUpdate = rentalRepository.findAll().size();
        rental.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRentalMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(rental)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Rental in the database
        List<Rental> rentalList = rentalRepository.findAll();
        assertThat(rentalList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRentalWithPatch() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        int databaseSizeBeforeUpdate = rentalRepository.findAll().size();

        // Update the rental using partial update
        Rental partialUpdatedRental = new Rental();
        partialUpdatedRental.setId(rental.getId());

        partialUpdatedRental.latitude(UPDATED_LATITUDE).state(UPDATED_STATE);

        restRentalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRental.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRental))
            )
            .andExpect(status().isOk());

        // Validate the Rental in the database
        List<Rental> rentalList = rentalRepository.findAll();
        assertThat(rentalList).hasSize(databaseSizeBeforeUpdate);
        Rental testRental = rentalList.get(rentalList.size() - 1);
        assertThat(testRental.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testRental.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
        assertThat(testRental.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testRental.getFromDate()).isEqualTo(DEFAULT_FROM_DATE);
        assertThat(testRental.getToDate()).isEqualTo(DEFAULT_TO_DATE);
        assertThat(testRental.getState()).isEqualTo(UPDATED_STATE);
    }

    @Test
    @Transactional
    void fullUpdateRentalWithPatch() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        int databaseSizeBeforeUpdate = rentalRepository.findAll().size();

        // Update the rental using partial update
        Rental partialUpdatedRental = new Rental();
        partialUpdatedRental.setId(rental.getId());

        partialUpdatedRental
            .code(UPDATED_CODE)
            .longitude(UPDATED_LONGITUDE)
            .latitude(UPDATED_LATITUDE)
            .fromDate(UPDATED_FROM_DATE)
            .toDate(UPDATED_TO_DATE)
            .state(UPDATED_STATE);

        restRentalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRental.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRental))
            )
            .andExpect(status().isOk());

        // Validate the Rental in the database
        List<Rental> rentalList = rentalRepository.findAll();
        assertThat(rentalList).hasSize(databaseSizeBeforeUpdate);
        Rental testRental = rentalList.get(rentalList.size() - 1);
        assertThat(testRental.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testRental.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testRental.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testRental.getFromDate()).isEqualTo(UPDATED_FROM_DATE);
        assertThat(testRental.getToDate()).isEqualTo(UPDATED_TO_DATE);
        assertThat(testRental.getState()).isEqualTo(UPDATED_STATE);
    }

    @Test
    @Transactional
    void patchNonExistingRental() throws Exception {
        int databaseSizeBeforeUpdate = rentalRepository.findAll().size();
        rental.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRentalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, rental.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(rental))
            )
            .andExpect(status().isBadRequest());

        // Validate the Rental in the database
        List<Rental> rentalList = rentalRepository.findAll();
        assertThat(rentalList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRental() throws Exception {
        int databaseSizeBeforeUpdate = rentalRepository.findAll().size();
        rental.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRentalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(rental))
            )
            .andExpect(status().isBadRequest());

        // Validate the Rental in the database
        List<Rental> rentalList = rentalRepository.findAll();
        assertThat(rentalList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRental() throws Exception {
        int databaseSizeBeforeUpdate = rentalRepository.findAll().size();
        rental.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRentalMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(rental)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Rental in the database
        List<Rental> rentalList = rentalRepository.findAll();
        assertThat(rentalList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRental() throws Exception {
        // Initialize the database
        rentalRepository.saveAndFlush(rental);

        int databaseSizeBeforeDelete = rentalRepository.findAll().size();

        // Delete the rental
        restRentalMockMvc
            .perform(delete(ENTITY_API_URL_ID, rental.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Rental> rentalList = rentalRepository.findAll();
        assertThat(rentalList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
