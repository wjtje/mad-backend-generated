package nl.hanze.se4.automaat.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import nl.hanze.se4.automaat.IntegrationTest;
import nl.hanze.se4.automaat.domain.Car;
import nl.hanze.se4.automaat.domain.enumeration.Body;
import nl.hanze.se4.automaat.domain.enumeration.Fuel;
import nl.hanze.se4.automaat.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CarResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CarResourceIT {

    private static final String DEFAULT_BRAND = "AAAAAAAAAA";
    private static final String UPDATED_BRAND = "BBBBBBBBBB";

    private static final String DEFAULT_MODEL = "AAAAAAAAAA";
    private static final String UPDATED_MODEL = "BBBBBBBBBB";

    private static final Fuel DEFAULT_FUEL = Fuel.GASOLINE;
    private static final Fuel UPDATED_FUEL = Fuel.DIESEL;

    private static final String DEFAULT_OPTIONS = "AAAAAAAAAA";
    private static final String UPDATED_OPTIONS = "BBBBBBBBBB";

    private static final String DEFAULT_LICENSE_PLATE = "AAAAAAAAAA";
    private static final String UPDATED_LICENSE_PLATE = "BBBBBBBBBB";

    private static final Integer DEFAULT_ENGINE_SIZE = 1;
    private static final Integer UPDATED_ENGINE_SIZE = 2;

    private static final Integer DEFAULT_MODEL_YEAR = 1;
    private static final Integer UPDATED_MODEL_YEAR = 2;

    private static final LocalDate DEFAULT_SINCE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_SINCE = LocalDate.now(ZoneId.systemDefault());

    private static final Float DEFAULT_PRICE = 1F;
    private static final Float UPDATED_PRICE = 2F;

    private static final Integer DEFAULT_NR_OF_SEATS = 1;
    private static final Integer UPDATED_NR_OF_SEATS = 2;

    private static final Body DEFAULT_BODY = Body.STATIONWAGON;
    private static final Body UPDATED_BODY = Body.SEDAN;

    private static final String ENTITY_API_URL = "/api/cars";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCarMockMvc;

    private Car car;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Car createEntity(EntityManager em) {
        Car car = new Car()
            .brand(DEFAULT_BRAND)
            .model(DEFAULT_MODEL)
            .fuel(DEFAULT_FUEL)
            .options(DEFAULT_OPTIONS)
            .licensePlate(DEFAULT_LICENSE_PLATE)
            .engineSize(DEFAULT_ENGINE_SIZE)
            .modelYear(DEFAULT_MODEL_YEAR)
            .since(DEFAULT_SINCE)
            .price(DEFAULT_PRICE)
            .nrOfSeats(DEFAULT_NR_OF_SEATS)
            .body(DEFAULT_BODY);
        return car;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Car createUpdatedEntity(EntityManager em) {
        Car car = new Car()
            .brand(UPDATED_BRAND)
            .model(UPDATED_MODEL)
            .fuel(UPDATED_FUEL)
            .options(UPDATED_OPTIONS)
            .licensePlate(UPDATED_LICENSE_PLATE)
            .engineSize(UPDATED_ENGINE_SIZE)
            .modelYear(UPDATED_MODEL_YEAR)
            .since(UPDATED_SINCE)
            .price(UPDATED_PRICE)
            .nrOfSeats(UPDATED_NR_OF_SEATS)
            .body(UPDATED_BODY);
        return car;
    }

    @BeforeEach
    public void initTest() {
        car = createEntity(em);
    }

    @Test
    @Transactional
    void createCar() throws Exception {
        int databaseSizeBeforeCreate = carRepository.findAll().size();
        // Create the Car
        restCarMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(car)))
            .andExpect(status().isCreated());

        // Validate the Car in the database
        List<Car> carList = carRepository.findAll();
        assertThat(carList).hasSize(databaseSizeBeforeCreate + 1);
        Car testCar = carList.get(carList.size() - 1);
        assertThat(testCar.getBrand()).isEqualTo(DEFAULT_BRAND);
        assertThat(testCar.getModel()).isEqualTo(DEFAULT_MODEL);
        assertThat(testCar.getFuel()).isEqualTo(DEFAULT_FUEL);
        assertThat(testCar.getOptions()).isEqualTo(DEFAULT_OPTIONS);
        assertThat(testCar.getLicensePlate()).isEqualTo(DEFAULT_LICENSE_PLATE);
        assertThat(testCar.getEngineSize()).isEqualTo(DEFAULT_ENGINE_SIZE);
        assertThat(testCar.getModelYear()).isEqualTo(DEFAULT_MODEL_YEAR);
        assertThat(testCar.getSince()).isEqualTo(DEFAULT_SINCE);
        assertThat(testCar.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testCar.getNrOfSeats()).isEqualTo(DEFAULT_NR_OF_SEATS);
        assertThat(testCar.getBody()).isEqualTo(DEFAULT_BODY);
    }

    @Test
    @Transactional
    void createCarWithExistingId() throws Exception {
        // Create the Car with an existing ID
        car.setId(1L);

        int databaseSizeBeforeCreate = carRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCarMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(car)))
            .andExpect(status().isBadRequest());

        // Validate the Car in the database
        List<Car> carList = carRepository.findAll();
        assertThat(carList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCars() throws Exception {
        // Initialize the database
        carRepository.saveAndFlush(car);

        // Get all the carList
        restCarMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(car.getId().intValue())))
            .andExpect(jsonPath("$.[*].brand").value(hasItem(DEFAULT_BRAND)))
            .andExpect(jsonPath("$.[*].model").value(hasItem(DEFAULT_MODEL)))
            .andExpect(jsonPath("$.[*].fuel").value(hasItem(DEFAULT_FUEL.toString())))
            .andExpect(jsonPath("$.[*].options").value(hasItem(DEFAULT_OPTIONS)))
            .andExpect(jsonPath("$.[*].licensePlate").value(hasItem(DEFAULT_LICENSE_PLATE)))
            .andExpect(jsonPath("$.[*].engineSize").value(hasItem(DEFAULT_ENGINE_SIZE)))
            .andExpect(jsonPath("$.[*].modelYear").value(hasItem(DEFAULT_MODEL_YEAR)))
            .andExpect(jsonPath("$.[*].since").value(hasItem(DEFAULT_SINCE.toString())))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].nrOfSeats").value(hasItem(DEFAULT_NR_OF_SEATS)))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY.toString())));
    }

    @Test
    @Transactional
    void getCar() throws Exception {
        // Initialize the database
        carRepository.saveAndFlush(car);

        // Get the car
        restCarMockMvc
            .perform(get(ENTITY_API_URL_ID, car.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(car.getId().intValue()))
            .andExpect(jsonPath("$.brand").value(DEFAULT_BRAND))
            .andExpect(jsonPath("$.model").value(DEFAULT_MODEL))
            .andExpect(jsonPath("$.fuel").value(DEFAULT_FUEL.toString()))
            .andExpect(jsonPath("$.options").value(DEFAULT_OPTIONS))
            .andExpect(jsonPath("$.licensePlate").value(DEFAULT_LICENSE_PLATE))
            .andExpect(jsonPath("$.engineSize").value(DEFAULT_ENGINE_SIZE))
            .andExpect(jsonPath("$.modelYear").value(DEFAULT_MODEL_YEAR))
            .andExpect(jsonPath("$.since").value(DEFAULT_SINCE.toString()))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.doubleValue()))
            .andExpect(jsonPath("$.nrOfSeats").value(DEFAULT_NR_OF_SEATS))
            .andExpect(jsonPath("$.body").value(DEFAULT_BODY.toString()));
    }

    @Test
    @Transactional
    void getNonExistingCar() throws Exception {
        // Get the car
        restCarMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCar() throws Exception {
        // Initialize the database
        carRepository.saveAndFlush(car);

        int databaseSizeBeforeUpdate = carRepository.findAll().size();

        // Update the car
        Car updatedCar = carRepository.findById(car.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCar are not directly saved in db
        em.detach(updatedCar);
        updatedCar
            .brand(UPDATED_BRAND)
            .model(UPDATED_MODEL)
            .fuel(UPDATED_FUEL)
            .options(UPDATED_OPTIONS)
            .licensePlate(UPDATED_LICENSE_PLATE)
            .engineSize(UPDATED_ENGINE_SIZE)
            .modelYear(UPDATED_MODEL_YEAR)
            .since(UPDATED_SINCE)
            .price(UPDATED_PRICE)
            .nrOfSeats(UPDATED_NR_OF_SEATS)
            .body(UPDATED_BODY);

        restCarMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCar.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCar))
            )
            .andExpect(status().isOk());

        // Validate the Car in the database
        List<Car> carList = carRepository.findAll();
        assertThat(carList).hasSize(databaseSizeBeforeUpdate);
        Car testCar = carList.get(carList.size() - 1);
        assertThat(testCar.getBrand()).isEqualTo(UPDATED_BRAND);
        assertThat(testCar.getModel()).isEqualTo(UPDATED_MODEL);
        assertThat(testCar.getFuel()).isEqualTo(UPDATED_FUEL);
        assertThat(testCar.getOptions()).isEqualTo(UPDATED_OPTIONS);
        assertThat(testCar.getLicensePlate()).isEqualTo(UPDATED_LICENSE_PLATE);
        assertThat(testCar.getEngineSize()).isEqualTo(UPDATED_ENGINE_SIZE);
        assertThat(testCar.getModelYear()).isEqualTo(UPDATED_MODEL_YEAR);
        assertThat(testCar.getSince()).isEqualTo(UPDATED_SINCE);
        assertThat(testCar.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testCar.getNrOfSeats()).isEqualTo(UPDATED_NR_OF_SEATS);
        assertThat(testCar.getBody()).isEqualTo(UPDATED_BODY);
    }

    @Test
    @Transactional
    void putNonExistingCar() throws Exception {
        int databaseSizeBeforeUpdate = carRepository.findAll().size();
        car.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarMockMvc
            .perform(
                put(ENTITY_API_URL_ID, car.getId()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(car))
            )
            .andExpect(status().isBadRequest());

        // Validate the Car in the database
        List<Car> carList = carRepository.findAll();
        assertThat(carList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCar() throws Exception {
        int databaseSizeBeforeUpdate = carRepository.findAll().size();
        car.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(car))
            )
            .andExpect(status().isBadRequest());

        // Validate the Car in the database
        List<Car> carList = carRepository.findAll();
        assertThat(carList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCar() throws Exception {
        int databaseSizeBeforeUpdate = carRepository.findAll().size();
        car.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(car)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Car in the database
        List<Car> carList = carRepository.findAll();
        assertThat(carList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCarWithPatch() throws Exception {
        // Initialize the database
        carRepository.saveAndFlush(car);

        int databaseSizeBeforeUpdate = carRepository.findAll().size();

        // Update the car using partial update
        Car partialUpdatedCar = new Car();
        partialUpdatedCar.setId(car.getId());

        partialUpdatedCar
            .model(UPDATED_MODEL)
            .options(UPDATED_OPTIONS)
            .engineSize(UPDATED_ENGINE_SIZE)
            .price(UPDATED_PRICE)
            .body(UPDATED_BODY);

        restCarMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCar.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCar))
            )
            .andExpect(status().isOk());

        // Validate the Car in the database
        List<Car> carList = carRepository.findAll();
        assertThat(carList).hasSize(databaseSizeBeforeUpdate);
        Car testCar = carList.get(carList.size() - 1);
        assertThat(testCar.getBrand()).isEqualTo(DEFAULT_BRAND);
        assertThat(testCar.getModel()).isEqualTo(UPDATED_MODEL);
        assertThat(testCar.getFuel()).isEqualTo(DEFAULT_FUEL);
        assertThat(testCar.getOptions()).isEqualTo(UPDATED_OPTIONS);
        assertThat(testCar.getLicensePlate()).isEqualTo(DEFAULT_LICENSE_PLATE);
        assertThat(testCar.getEngineSize()).isEqualTo(UPDATED_ENGINE_SIZE);
        assertThat(testCar.getModelYear()).isEqualTo(DEFAULT_MODEL_YEAR);
        assertThat(testCar.getSince()).isEqualTo(DEFAULT_SINCE);
        assertThat(testCar.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testCar.getNrOfSeats()).isEqualTo(DEFAULT_NR_OF_SEATS);
        assertThat(testCar.getBody()).isEqualTo(UPDATED_BODY);
    }

    @Test
    @Transactional
    void fullUpdateCarWithPatch() throws Exception {
        // Initialize the database
        carRepository.saveAndFlush(car);

        int databaseSizeBeforeUpdate = carRepository.findAll().size();

        // Update the car using partial update
        Car partialUpdatedCar = new Car();
        partialUpdatedCar.setId(car.getId());

        partialUpdatedCar
            .brand(UPDATED_BRAND)
            .model(UPDATED_MODEL)
            .fuel(UPDATED_FUEL)
            .options(UPDATED_OPTIONS)
            .licensePlate(UPDATED_LICENSE_PLATE)
            .engineSize(UPDATED_ENGINE_SIZE)
            .modelYear(UPDATED_MODEL_YEAR)
            .since(UPDATED_SINCE)
            .price(UPDATED_PRICE)
            .nrOfSeats(UPDATED_NR_OF_SEATS)
            .body(UPDATED_BODY);

        restCarMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCar.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCar))
            )
            .andExpect(status().isOk());

        // Validate the Car in the database
        List<Car> carList = carRepository.findAll();
        assertThat(carList).hasSize(databaseSizeBeforeUpdate);
        Car testCar = carList.get(carList.size() - 1);
        assertThat(testCar.getBrand()).isEqualTo(UPDATED_BRAND);
        assertThat(testCar.getModel()).isEqualTo(UPDATED_MODEL);
        assertThat(testCar.getFuel()).isEqualTo(UPDATED_FUEL);
        assertThat(testCar.getOptions()).isEqualTo(UPDATED_OPTIONS);
        assertThat(testCar.getLicensePlate()).isEqualTo(UPDATED_LICENSE_PLATE);
        assertThat(testCar.getEngineSize()).isEqualTo(UPDATED_ENGINE_SIZE);
        assertThat(testCar.getModelYear()).isEqualTo(UPDATED_MODEL_YEAR);
        assertThat(testCar.getSince()).isEqualTo(UPDATED_SINCE);
        assertThat(testCar.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testCar.getNrOfSeats()).isEqualTo(UPDATED_NR_OF_SEATS);
        assertThat(testCar.getBody()).isEqualTo(UPDATED_BODY);
    }

    @Test
    @Transactional
    void patchNonExistingCar() throws Exception {
        int databaseSizeBeforeUpdate = carRepository.findAll().size();
        car.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, car.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(car))
            )
            .andExpect(status().isBadRequest());

        // Validate the Car in the database
        List<Car> carList = carRepository.findAll();
        assertThat(carList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCar() throws Exception {
        int databaseSizeBeforeUpdate = carRepository.findAll().size();
        car.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(car))
            )
            .andExpect(status().isBadRequest());

        // Validate the Car in the database
        List<Car> carList = carRepository.findAll();
        assertThat(carList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCar() throws Exception {
        int databaseSizeBeforeUpdate = carRepository.findAll().size();
        car.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(car)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Car in the database
        List<Car> carList = carRepository.findAll();
        assertThat(carList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCar() throws Exception {
        // Initialize the database
        carRepository.saveAndFlush(car);

        int databaseSizeBeforeDelete = carRepository.findAll().size();

        // Delete the car
        restCarMockMvc.perform(delete(ENTITY_API_URL_ID, car.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Car> carList = carRepository.findAll();
        assertThat(carList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
