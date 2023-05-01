package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.dto.LabelDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Set;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;
import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static hexlet.code.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static hexlet.code.utils.TestUtils.TEST_USERNAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private TestUtils utils;


    @AfterEach
    public void clear() {
        utils.tearDown();
    }

    @Test
    public void testCreateTask() throws Exception {
        utils.regDefaultUser();
        final User expectedUser = userRepository.findAll().get(0);
        final var taskStatusDto = new TaskStatusDto("TaskStatus dto test 1");
        final var taskStatusPostRequest = post(BASE_URL + TASK_STATUS_CONTROLLER_PATH)
                .content(asJson(taskStatusDto))
                .contentType(APPLICATION_JSON);
        final TaskStatus status = fromJson((utils.perform(taskStatusPostRequest, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse())
                .getContentAsString(), new TypeReference<>() {
                });
        final var labelDto = new LabelDto("first Test Label");
        final var labelPostRequest = post(BASE_URL + LABEL_CONTROLLER_PATH)
                .content(asJson(labelDto))
                .contentType(APPLICATION_JSON);
        final Label label = fromJson((utils.perform(labelPostRequest, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse())
                .getContentAsString(), new TypeReference<>() {
                });
        Set<Long> labels = new HashSet<>();
        labels.add(label.getId());
        final var taskDto = new TaskDto("first Test Task Name",
                "first Test Task description",
                status.getId(), labels, expectedUser.getId()
        );
        final var taskPostRequest = post(BASE_URL + TASK_CONTROLLER_PATH)
                .content(asJson(taskDto))
                .contentType(APPLICATION_JSON);
        final var response = utils.perform(taskPostRequest, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();
        final Task task = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertEquals(task.getName(), taskDto.getName());
        assertEquals(task.getDescription(), taskDto.getDescription());
        assertEquals(task.getExecutor().getId(), taskDto.getExecutorId());
        assertEquals(task.getTaskStatus().getId(), taskDto.getTaskStatusId());
    }
}
