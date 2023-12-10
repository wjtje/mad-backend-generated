package nl.hanze.se4.automaat.repository;

import java.util.List;
import java.util.Optional;
import nl.hanze.se4.automaat.domain.Route;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Route entity.
 */
@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    default Optional<Route> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Route> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Route> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(value = "select route from Route route left join fetch route.employee", countQuery = "select count(route) from Route route")
    Page<Route> findAllWithToOneRelationships(Pageable pageable);

    @Query("select route from Route route left join fetch route.employee")
    List<Route> findAllWithToOneRelationships();

    @Query("select route from Route route left join fetch route.employee where route.id =:id")
    Optional<Route> findOneWithToOneRelationships(@Param("id") Long id);
}
