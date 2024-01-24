package nl.hanze.se4.automaat.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link nl.hanze.se4.automaat.domain.Inspection} entity. This class is used
 * in {@link nl.hanze.se4.automaat.web.rest.InspectionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /inspections?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InspectionCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter code;

    private LongFilter odometer;

    private StringFilter result;

    private ZonedDateTimeFilter completed;

    private LongFilter photoId;

    private LongFilter repairId;

    private LongFilter carId;

    private LongFilter employeeId;

    private LongFilter rentalId;

    private Boolean distinct;

    public InspectionCriteria() {}

    public InspectionCriteria(InspectionCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.code = other.code == null ? null : other.code.copy();
        this.odometer = other.odometer == null ? null : other.odometer.copy();
        this.result = other.result == null ? null : other.result.copy();
        this.completed = other.completed == null ? null : other.completed.copy();
        this.photoId = other.photoId == null ? null : other.photoId.copy();
        this.repairId = other.repairId == null ? null : other.repairId.copy();
        this.carId = other.carId == null ? null : other.carId.copy();
        this.employeeId = other.employeeId == null ? null : other.employeeId.copy();
        this.rentalId = other.rentalId == null ? null : other.rentalId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public InspectionCriteria copy() {
        return new InspectionCriteria(this);
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

    public LongFilter getOdometer() {
        return odometer;
    }

    public LongFilter odometer() {
        if (odometer == null) {
            odometer = new LongFilter();
        }
        return odometer;
    }

    public void setOdometer(LongFilter odometer) {
        this.odometer = odometer;
    }

    public StringFilter getResult() {
        return result;
    }

    public StringFilter result() {
        if (result == null) {
            result = new StringFilter();
        }
        return result;
    }

    public void setResult(StringFilter result) {
        this.result = result;
    }

    public ZonedDateTimeFilter getCompleted() {
        return completed;
    }

    public ZonedDateTimeFilter completed() {
        if (completed == null) {
            completed = new ZonedDateTimeFilter();
        }
        return completed;
    }

    public void setCompleted(ZonedDateTimeFilter completed) {
        this.completed = completed;
    }

    public LongFilter getPhotoId() {
        return photoId;
    }

    public LongFilter photoId() {
        if (photoId == null) {
            photoId = new LongFilter();
        }
        return photoId;
    }

    public void setPhotoId(LongFilter photoId) {
        this.photoId = photoId;
    }

    public LongFilter getRepairId() {
        return repairId;
    }

    public LongFilter repairId() {
        if (repairId == null) {
            repairId = new LongFilter();
        }
        return repairId;
    }

    public void setRepairId(LongFilter repairId) {
        this.repairId = repairId;
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

    public LongFilter getEmployeeId() {
        return employeeId;
    }

    public LongFilter employeeId() {
        if (employeeId == null) {
            employeeId = new LongFilter();
        }
        return employeeId;
    }

    public void setEmployeeId(LongFilter employeeId) {
        this.employeeId = employeeId;
    }

    public LongFilter getRentalId() {
        return rentalId;
    }

    public LongFilter rentalId() {
        if (rentalId == null) {
            rentalId = new LongFilter();
        }
        return rentalId;
    }

    public void setRentalId(LongFilter rentalId) {
        this.rentalId = rentalId;
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
        final InspectionCriteria that = (InspectionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(odometer, that.odometer) &&
            Objects.equals(result, that.result) &&
            Objects.equals(completed, that.completed) &&
            Objects.equals(photoId, that.photoId) &&
            Objects.equals(repairId, that.repairId) &&
            Objects.equals(carId, that.carId) &&
            Objects.equals(employeeId, that.employeeId) &&
            Objects.equals(rentalId, that.rentalId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, odometer, result, completed, photoId, repairId, carId, employeeId, rentalId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InspectionCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (code != null ? "code=" + code + ", " : "") +
            (odometer != null ? "odometer=" + odometer + ", " : "") +
            (result != null ? "result=" + result + ", " : "") +
            (completed != null ? "completed=" + completed + ", " : "") +
            (photoId != null ? "photoId=" + photoId + ", " : "") +
            (repairId != null ? "repairId=" + repairId + ", " : "") +
            (carId != null ? "carId=" + carId + ", " : "") +
            (employeeId != null ? "employeeId=" + employeeId + ", " : "") +
            (rentalId != null ? "rentalId=" + rentalId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
