package nl.hanze.se4.automaat.domain;

import static nl.hanze.se4.automaat.domain.LocationTestSamples.*;
import static nl.hanze.se4.automaat.domain.RouteStopTestSamples.*;
import static nl.hanze.se4.automaat.domain.RouteTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import nl.hanze.se4.automaat.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RouteStopTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RouteStop.class);
        RouteStop routeStop1 = getRouteStopSample1();
        RouteStop routeStop2 = new RouteStop();
        assertThat(routeStop1).isNotEqualTo(routeStop2);

        routeStop2.setId(routeStop1.getId());
        assertThat(routeStop1).isEqualTo(routeStop2);

        routeStop2 = getRouteStopSample2();
        assertThat(routeStop1).isNotEqualTo(routeStop2);
    }

    @Test
    void routeTest() throws Exception {
        RouteStop routeStop = getRouteStopRandomSampleGenerator();
        Route routeBack = getRouteRandomSampleGenerator();

        routeStop.setRoute(routeBack);
        assertThat(routeStop.getRoute()).isEqualTo(routeBack);

        routeStop.route(null);
        assertThat(routeStop.getRoute()).isNull();
    }

    @Test
    void locationTest() throws Exception {
        RouteStop routeStop = getRouteStopRandomSampleGenerator();
        Location locationBack = getLocationRandomSampleGenerator();

        routeStop.setLocation(locationBack);
        assertThat(routeStop.getLocation()).isEqualTo(locationBack);

        routeStop.location(null);
        assertThat(routeStop.getLocation()).isNull();
    }
}
