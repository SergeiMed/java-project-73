package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.model.Label;
import hexlet.code.repository.TaskStatusRepository;
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

import java.util.List;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static hexlet.code.utils.TestUtils.TEST_USERNAME;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class TaskStatusControllerIT {

    public static final String BASE_URL = "/api";
    @Autowired
    private TaskStatusRepository statusRepository;
    @Autowired
    private TestUtils utils;

    @BeforeEach
    public void testInit() throws Exception {
        utils.createDefaultStatus();
    }

    @AfterEach
    public void clear() {
        utils.tearDown();
    }


    @Test
    public void testCreateStatus() throws Exception {
        assertEquals(1, statusRepository.count());
        utils.createStatus(new TaskStatusDto("Task status 1")).andExpect(status().isCreated());
        assertEquals(2, statusRepository.count());
    }


    @Test
    public void testCreatedStatusFails() throws Exception {
        assertEquals(1, statusRepository.count());
        final var statusDto = new TaskStatusDto("");
        final var postRequest = post(BASE_URL + TASK_STATUS_CONTROLLER_PATH)
                .content(asJson(statusDto))
                .contentType(APPLICATION_JSON);
        utils.perform(postRequest, TEST_USERNAME)
                .andExpect(status().isBadRequest());
        assertEquals(1, statusRepository.count());
    }


    @Test
    public void testUpdateStatus() throws Exception {
        final Long existStatusId = statusRepository.findAll().get(0).getId();
        final var updateTaskStatusDto = new TaskStatusDto("Updated status");
        final var updateRequest = put(
                BASE_URL + TASK_STATUS_CONTROLLER_PATH + TaskStatusController.ID, existStatusId)
                .content(asJson(updateTaskStatusDto))
                .contentType(APPLICATION_JSON);
        utils.perform(updateRequest, TEST_USERNAME).andExpect(status().isOk());
        assertTrue(statusRepository.existsById(existStatusId));
        assertEquals(statusRepository.findAll().get(0).getName(), updateTaskStatusDto.getName());
    }


    @Test
    public void getAllStatuses() throws Exception {
        final var taskStatusDto = new TaskStatusDto("Task status 1");
        final var postRequest = post(BASE_URL + TASK_STATUS_CONTROLLER_PATH)
                .content(asJson(taskStatusDto))
                .contentType(APPLICATION_JSON);
        utils.perform(postRequest, TEST_USERNAME)
                .andExpect(status().isCreated());
        final var response = utils.perform(get(BASE_URL + TASK_STATUS_CONTROLLER_PATH), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        final List<Label> statuses = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(statuses.size()).isEqualTo(2);
    }


    @Test
    public void deleteStatus() throws Exception {
        assertEquals(1, statusRepository.count());
        utils.perform(delete(BASE_URL + TASK_STATUS_CONTROLLER_PATH + TaskStatusController.ID,
                                statusRepository.findAll().get(0).getId()), TEST_USERNAME)
                .andExpect(status().isOk());
        assertEquals(0, statusRepository.count());
    }
}
