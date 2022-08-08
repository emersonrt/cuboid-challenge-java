package co.fullstacklabs.cuboid.challenge.repository;

import co.fullstacklabs.cuboid.challenge.model.Bag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author FullStack Labs
 * @version 1.0
 * @since 2021-10-22
 */
@Repository
public interface BagRepository extends JpaRepository<Bag, Long> {
}