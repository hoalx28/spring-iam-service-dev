package springproject.iam.v1.repository.device;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springproject.iam.v1.model.Device;

@Repository
public interface JpaDeviceRepository extends JpaRepository<Device, Long> {
  @Query(
      value =
          "SELECT COUNT(D.id) FROM Device AS D WHERE D.ip_address = :ipAddress AND deleted IN (TRUE, FALSE)",
      nativeQuery = true)
  long countExistedByIpAddress(@Param("ipAddress") String ipAddress);

  @Query(value = "SELECT * FROM Device WHERE deleted = TRUE", nativeQuery = true)
  Page<Device> findAllByDeletedTrue(Pageable pageable);

  Page<Device> findAllByUserAgentContains(String userAgent, Pageable pageable);

  Optional<Device> findByIpAddress(String ipAddress);
}
