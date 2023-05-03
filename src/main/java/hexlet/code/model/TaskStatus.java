package hexlet.code.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotBlank;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

@Getter
@Setter
@Entity
@Table(name = "statuses")
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatus {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @CreationTimestamp
    @Temporal(TIMESTAMP)
    private Date createdAt;

    public TaskStatus(final Long id) {
        this.id = id;
    }
}
