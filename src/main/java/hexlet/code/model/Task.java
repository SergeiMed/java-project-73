package hexlet.code.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

@Getter
@Setter
@Entity
@Table(name = "tasks")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private String description;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "task_status_id")
    private TaskStatus taskStatus;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "executor_id")
    private User executor;

    @CreationTimestamp
    @Temporal(TIMESTAMP)
    private Date createdAt;

    @ManyToMany
    @JoinColumn(name = "labels_id")
    private Set<Label> labels;
}
