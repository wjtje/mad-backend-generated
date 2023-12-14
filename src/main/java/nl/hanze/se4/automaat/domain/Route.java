package nl.hanze.se4.automaat.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * A Route.
 */
@Entity
@Table(name = "route")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Route implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "date")
    private LocalDate date;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "route")
    @JsonIgnoreProperties(value = { "route", "location" }, allowSetters = true)
    private Set<RouteStop> routeStops = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "inspections", "repairs", "routes" }, allowSetters = true)
    private Employee employee;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Route id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public Route code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return this.description;
    }

    public Route description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public Route date(LocalDate date) {
        this.setDate(date);
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Set<RouteStop> getRouteStops() {
        return this.routeStops;
    }

    public void setRouteStops(Set<RouteStop> routeStops) {
        if (this.routeStops != null) {
            this.routeStops.forEach(i -> i.setRoute(null));
        }
        if (routeStops != null) {
            routeStops.forEach(i -> i.setRoute(this));
        }
        this.routeStops = routeStops;
    }

    public Route routeStops(Set<RouteStop> routeStops) {
        this.setRouteStops(routeStops);
        return this;
    }

    public Route addRouteStop(RouteStop routeStop) {
        this.routeStops.add(routeStop);
        routeStop.setRoute(this);
        return this;
    }

    public Route removeRouteStop(RouteStop routeStop) {
        this.routeStops.remove(routeStop);
        routeStop.setRoute(null);
        return this;
    }

    public Employee getEmployee() {
        return this.employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Route employee(Employee employee) {
        this.setEmployee(employee);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Route)) {
            return false;
        }
        return getId() != null && getId().equals(((Route) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Route{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", description='" + getDescription() + "'" +
            ", date='" + getDate() + "'" +
            "}";
    }
}
