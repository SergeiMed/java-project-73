package hexlet.code.service.impl;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.TaskStatusService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class TaskStatusServiceImpl implements TaskStatusService {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Override
    public TaskStatus createNewTaskStatus(TaskStatusDto taskStatusDto) {
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setName(taskStatusDto.getName());
        return taskStatusRepository.save(taskStatus);
    }

    @Override
    public TaskStatus updateTaskStatus(Long id, TaskStatusDto taskStatusDto) {
        final TaskStatus taskStatusToUpdate = taskStatusRepository.findById(id).get();
        taskStatusToUpdate.setName(taskStatusDto.getName());
        return taskStatusRepository.save(taskStatusToUpdate);
    }

    @Override
    public void deleteTaskStatusById(Long id) {
        TaskStatus taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Status with id: " + id + ", not found"));
        taskStatusRepository.deleteById(id);
    }
}
