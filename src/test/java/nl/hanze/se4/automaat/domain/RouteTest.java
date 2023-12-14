package nl.hanze.se4.automaat.domain;

import static nl.hanze.se4.automaat.domain.EmployeeTestSamples.*;
import static nl.hanze.se4.automaat.domain.RouteStopTestSamples.*;
import static nl.hanze.se4.automaat.domain.RouteTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import nl.hanze.se4.automaat.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RouteTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Route.class);
        Route route1 = getRouteSample1();
        Route route2 = new Route();
        assertThat(route1).isNotEqualTo(route2);

        route2.setId(route1.getId());
        assertThat(route1).isEqualTo(route2);

        route2 = getRouteSample2();
        assertThat(route1).isNotEqualTo(route2);
    }

    @Test
    void routeStopTest() throws Exception {
        Route route = getRouteRandomSampleGenerator();
        RouteStop routeStopBack = getRouteStopRandomSampleGenerator();

        route.addRouteStop(routeStopBack);
        assertThat(route.getRouteStops()).containsOnly(routeStopBack);
        assertThat(routeStopBack.getRoute()).isEqualTo(route);

        route.removeRouteStop(routeStopBack);
        assertThat(route.getRouteStops()).doesNotContain(routeStopBack);
        assertThat(routeStopBack.getRoute()).isNull();

        route.routeStops(new HashSet<>(Set.of(routeStopBack)));
        assertThat(route.getRouteStops()).containsOnly(routeStopBack);
        assertThat(routeStopBack.getRoute()).isEqualTo(route);

        route.setRouteStops(new HashSet<>());
        assertThat(route.getRouteStops()).doesNotContain(routeStopBack);
        assertThat(routeStopBack.getRoute()).isNull();
    }

    @Test
    void employeeTest() throws Exception {
        Route route = getRouteRandomSampleGenerator();
        Employee employeeBack = getEmployeeRandomSampleGenerator();

        route.setEmployee(employeeBack);
        assertThat(route.getEmployee()).isEqualTo(employeeBack);

        route.employee(null);
        assertThat(route.getEmployee()).isNull();
    }
}
