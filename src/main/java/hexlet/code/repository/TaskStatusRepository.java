package hexlet.code.repository;

import hexlet.code.model.TaskStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long> {

    Optional<TaskStatus> findById(Long id);
}
