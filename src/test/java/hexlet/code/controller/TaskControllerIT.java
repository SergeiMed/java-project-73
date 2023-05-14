package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.controller.LabelController.ID;
import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static hexlet.code.utils.TestUtils.TEST_USERNAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class TaskControllerIT {

    public static final String BASE_URL = "/api";
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TestUtils utils;
    private TaskDto taskDto;

    @BeforeEach
    public void testInit() throws Exception {
        utils.regDefaultUser();
        utils.createDefaultStatus();
        utils.createDefaultLabel();
        final TaskStatus status = taskStatusRepository.findAll().get(0);
        final User executor = userRepository.findByEmail(TEST_USERNAME).get();
        final Label label = labelRepository.findAll().get(0);
        final TaskDto taskDto = new TaskDto(
                "Test task",
                "Test description",
                status.getId(),
                Set.of(label.getId()),
                executor.getId()
        );
        utils.perform(post(BASE_URL + TASK_CONTROLLER_PATH)
                        .content(asJson(taskDto))
                        .contentType(APPLICATION_JSON),
                TEST_USERNAME);
    }
    @AfterEach
    public void clear() {
        utils.tearDown();
    }

    @Test
    public void testCreateTask() throws Exception {
        utils.createStatus(new TaskStatusDto("Test status 1"));
        final TaskDto taskDto = new TaskDto(
                "Test task 1",
                "Test description 1",
                taskStatusRepository.findAll().get(0).getId(),
                Set.of(labelRepository.findAll().get(0).getId()),
                userRepository.findAll().get(0).getId()
        );
        final var postRequest = post(BASE_URL + TASK_CONTROLLER_PATH)
                .content(asJson(taskDto))
                .contentType(APPLICATION_JSON);
        utils.perform(postRequest, TEST_USERNAME).andExpect(status().isCreated());
    }

    @Test
    public void testUpdateTask() throws Exception {
        final long taskId = taskRepository.findAll().get(0).getId();
        final TaskDto updateDto = new TaskDto(
                "New test task",
                "New test description",
                taskRepository.findAll().get(0).getId(),
                Set.of(labelRepository.findAll().get(0).getId()),
                userRepository.findAll().get(0).getId());
        final var response = utils.perform(
                        put(BASE_URL + TASK_CONTROLLER_PATH + ID, taskId)
                                .content(asJson(updateDto))
                                .contentType(APPLICATION_JSON),
                        TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        final Task task = fromJson(response.getContentAsString(), new TypeReference<Task>() {
        });
        assertTrue(taskRepository.existsById(taskId));
        assertEquals(task.getName(), updateDto.getName());
        assertEquals(task.getDescription(), updateDto.getDescription());
    }

    @Test
    public void getAllTasks() throws Exception {
        final var response = utils.perform(
                        get(BASE_URL + TASK_CONTROLLER_PATH),
                        TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        assertEquals(1, taskRepository.count());
    }

    @Test
    public void deleteTask() throws Exception {
        final long taskId = taskRepository.findAll().get(0).getId();
        utils.perform(delete(BASE_URL + TASK_CONTROLLER_PATH + ID, taskId), TEST_USERNAME)
                .andExpect(status().isOk());
        assertEquals(taskRepository.count(), 0);
    }
}
