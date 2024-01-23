package nl.hanze.se4.automaat.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import nl.hanze.se4.automaat.domain.enumeration.RentalState;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link nl.hanze.se4.automaat.domain.Rental} entity. This class is used
 * in {@link nl.hanze.se4.automaat.web.rest.RentalResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /rentals?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RentalCriteria implements Serializable, Criteria {

    /**
     * Class for filtering RentalState
     */
    public static class RentalStateFilter extends Filter<RentalState> {

        public RentalStateFilter() {}

        public RentalStateFilter(RentalStateFilter filter) {
            super(filter);
        }

        @Override
        public RentalStateFilter copy() {
            return new RentalStateFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter code;

    private FloatFilter longitude;

    private FloatFilter latitude;

    private LocalDateFilter fromDate;

    private LocalDateFilter toDate;

    private RentalStateFilter state;

    private LongFilter inspectionId;

    private LongFilter customerId;

    private LongFilter carId;

    private Boolean distinct;

    public RentalCriteria() {}

    public RentalCriteria(RentalCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.code = other.code == null ? null : other.code.copy();
        this.longitude = other.longitude == null ? null : other.longitude.copy();
        this.latitude = other.latitude == null ? null : other.latitude.copy();
        this.fromDate = other.fromDate == null ? null : other.fromDate.copy();
        this.toDate = other.toDate == null ? null : other.toDate.copy();
        this.state = other.state == null ? null : other.state.copy();
        this.inspectionId = other.inspectionId == null ? null : other.inspectionId.copy();
        this.customerId = other.customerId == null ? null : other.customerId.copy();
        this.carId = other.carId == null ? null : other.carId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public RentalCriteria copy() {
        return new RentalCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getCode() {
        return code;
    }

    public StringFilter code() {
        if (code == null) {
            code = new StringFilter();
        }
        return code;
    }

    public void setCode(StringFilter code) {
        this.code = code;
    }

    public FloatFilter getLongitude() {
        return longitude;
    }

    public FloatFilter longitude() {
        if (longitude == null) {
            longitude = new FloatFilter();
        }
        return longitude;
    }

    public void setLongitude(FloatFilter longitude) {
        this.longitude = longitude;
    }

    public FloatFilter getLatitude() {
        return latitude;
    }

    public FloatFilter latitude() {
        if (latitude == null) {
            latitude = new FloatFilter();
        }
        return latitude;
    }

    public void setLatitude(FloatFilter latitude) {
        this.latitude = latitude;
    }

    public LocalDateFilter getFromDate() {
        return fromDate;
    }

    public LocalDateFilter fromDate() {
        if (fromDate == null) {
            fromDate = new LocalDateFilter();
        }
        return fromDate;
    }

    public void setFromDate(LocalDateFilter fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDateFilter getToDate() {
        return toDate;
    }

    public LocalDateFilter toDate() {
        if (toDate == null) {
            toDate = new LocalDateFilter();
        }
        return toDate;
    }

    public void setToDate(LocalDateFilter toDate) {
        this.toDate = toDate;
    }

    public RentalStateFilter getState() {
        return state;
    }

    public RentalStateFilter state() {
        if (state == null) {
            state = new RentalStateFilter();
        }
        return state;
    }

    public void setState(RentalStateFilter state) {
        this.state = state;
    }

    public LongFilter getInspectionId() {
        return inspectionId;
    }

    public LongFilter inspectionId() {
        if (inspectionId == null) {
            inspectionId = new LongFilter();
        }
        return inspectionId;
    }

    public void setInspectionId(LongFilter inspectionId) {
        this.inspectionId = inspectionId;
    }

    public LongFilter getCustomerId() {
        return customerId;
    }

    public LongFilter customerId() {
        if (customerId == null) {
            customerId = new LongFilter();
        }
        return customerId;
    }

    public void setCustomerId(LongFilter customerId) {
        this.customerId = customerId;
    }

    public LongFilter getCarId() {
        return carId;
    }

    public LongFilter carId() {
        if (carId == null) {
            carId = new LongFilter();
        }
        return carId;
    }

    public void setCarId(LongFilter carId) {
        this.carId = carId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RentalCriteria that = (RentalCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(longitude, that.longitude) &&
            Objects.equals(latitude, that.latitude) &&
            Objects.equals(fromDate, that.fromDate) &&
            Objects.equals(toDate, that.toDate) &&
            Objects.equals(state, that.state) &&
            Objects.equals(inspectionId, that.inspectionId) &&
            Objects.equals(customerId, that.customerId) &&
            Objects.equals(carId, that.carId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, longitude, latitude, fromDate, toDate, state, inspectionId, customerId, carId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RentalCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (code != null ? "code=" + code + ", " : "") +
            (longitude != null ? "longitude=" + longitude + ", " : "") +
            (latitude != null ? "latitude=" + latitude + ", " : "") +
            (fromDate != null ? "fromDate=" + fromDate + ", " : "") +
            (toDate != null ? "toDate=" + toDate + ", " : "") +
            (state != null ? "state=" + state + ", " : "") +
            (inspectionId != null ? "inspectionId=" + inspectionId + ", " : "") +
            (customerId != null ? "customerId=" + customerId + ", " : "") +
            (carId != null ? "carId=" + carId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
