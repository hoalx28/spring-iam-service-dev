package springproject.iam.v1.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import springproject.iam.v1.introspector.DeviceIntrospector;

@Data
@EqualsAndHashCode(exclude = {"user"})
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@EntityListeners(value = DeviceIntrospector.class)
@Table
@SoftDelete
public class Device implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @NotBlank(message = "ip_address can not be blank")
  @Column(name = "ip_address", nullable = false)
  String ipAddress;

  @NotBlank(message = "user_agent can not be blank")
  @Column(name = "user_agent", nullable = false)
  String userAgent;

  @CreationTimestamp LocalDateTime created;
  @CreatedBy String createdBy;
  @UpdateTimestamp LocalDateTime updated;
  @LastModifiedBy String updatedBy;

  @OneToOne
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  User user;
}
