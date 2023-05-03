package hexlet.code.controller;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.impl.TaskStatusServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import javax.validation.Valid;

import java.util.List;

import static hexlet.code.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;

@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + TASK_STATUS_CONTROLLER_PATH)
public class TaskStatusController {

    private final TaskStatusRepository taskStatusRepository;
    private final TaskStatusServiceImpl taskStatusService;
    public static final String TASK_STATUS_CONTROLLER_PATH = "/statuses";
    public static final String ID = "/{id}";


    @ApiResponses(@ApiResponse(responseCode = "200"))
    @Operation(summary = "Get task status")
    @GetMapping(ID)
    public TaskStatus getTaskStatusById(@PathVariable final Long id) {
        return taskStatusRepository.findById(id).get();
    }


    @Operation(summary = "Create task status")
    @ApiResponse(responseCode = "201", description = "Task status created")
    @PostMapping
    @ResponseStatus(CREATED)
    public TaskStatus registerNewTaskStatus(@RequestBody @Valid final TaskStatusDto taskStatusDto) {
        return taskStatusService.createNewTaskStatus(taskStatusDto);
    }


    @Operation(summary = "Update task status")
    @PutMapping(ID)
    public TaskStatus updateTaskStatus(@RequestBody @Valid final TaskStatusDto taskStatusDto, @PathVariable long id) {
        return taskStatusService.updateTaskStatus(id, taskStatusDto);
    }


    @Operation(summary = "Delete task status")
    @DeleteMapping(ID)
    public void deleteTaskStatus(@PathVariable long id) {
        taskStatusService.deleteTaskStatusById(id);
    }


    @Operation(summary = "Get all Task Status")
    @ApiResponses(@ApiResponse(responseCode = "200", content = @Content(
            schema = @Schema(implementation = TaskStatus.class))))
    @GetMapping
    public List<TaskStatus> getAll() {
        return taskStatusRepository.findAll()
                .stream()
                .toList();
    }
}
