package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.controller.TaskStatusController.ID;
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


    @AfterEach
    public void clear() {
        utils.tearDown();
    }


    @Test
    public void testCreateStatus() throws Exception {
        utils.regDefaultUser();
        final var statusDto = new TaskStatusDto("new");
        final var postRequest = post(BASE_URL + TASK_STATUS_CONTROLLER_PATH)
                .content(asJson(statusDto))
                .contentType(APPLICATION_JSON);
        final var response = utils.perform(postRequest, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();
        final TaskStatus expectedStatus = statusRepository.findAll().get(0);
        final TaskStatus status = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertTrue(statusRepository.existsById(status.getId()));
        assertEquals(expectedStatus.getId(), status.getId());
        assertEquals(expectedStatus.getName(), status.getName());
    }

    @Disabled
    @Test
    public void testCreatedStatusFails() throws Exception {
        utils.regDefaultUser();
        final var statusDto = new TaskStatusDto("");
        final var postRequest = post(BASE_URL + TASK_STATUS_CONTROLLER_PATH)
                .content(asJson(statusDto))
                .contentType(APPLICATION_JSON);
        utils.perform(postRequest, TEST_USERNAME)
                .andExpect(status().is(400));
        assertEquals(0, statusRepository.count());
    }


    @Test
    public void testUpdateStatus() throws Exception {
        utils.regDefaultUser();
        final var postRequest = post(BASE_URL + TASK_STATUS_CONTROLLER_PATH)
                .content(asJson(new TaskStatusDto("new")))
                .contentType(APPLICATION_JSON);
        utils.perform(postRequest, TEST_USERNAME);
        TaskStatus createdStatus = statusRepository.findAll().get(0);
        final var statusDto = new TaskStatusDto("verified");
        final var putRequest = put(BASE_URL + TASK_STATUS_CONTROLLER_PATH + ID, createdStatus.getId())
                .content(asJson(statusDto))
                .contentType(APPLICATION_JSON);
        final var response = utils.perform(putRequest, TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        createdStatus = statusRepository.findAll().get(0);
        final TaskStatus status = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertEquals(createdStatus.getId(), status.getId());
        assertEquals(createdStatus.getName(), statusDto.getName());
    }


    @Disabled
    @Test
    public void getAllStatuses() throws Exception {
        utils.regDefaultUser();
        final var statusDto = new TaskStatusDto("verified");
        final var postRequest = post(BASE_URL + TASK_STATUS_CONTROLLER_PATH)
                .content(asJson(statusDto))
                .contentType(APPLICATION_JSON);
        utils.perform(postRequest, TEST_USERNAME)
                .andExpect(status().isCreated());
        final var response = utils.perform(get(BASE_URL + TASK_STATUS_CONTROLLER_PATH), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        final List<TaskStatus> statuses = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(statuses.size()).isEqualTo(1);
    }


    @Test
    public void deleteStatus() throws Exception {
        utils.regDefaultUser();
        final var statusDto = new TaskStatusDto("verified");
        final var postRequest = post(BASE_URL + TASK_STATUS_CONTROLLER_PATH)
                .content(asJson(statusDto))
                .contentType(APPLICATION_JSON);
        utils.perform(postRequest, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();
        utils.perform(delete(BASE_URL + TASK_STATUS_CONTROLLER_PATH + ID,
                                statusRepository.findAll().get(0).getId()),
                        TEST_USERNAME)
                .andExpect(status().isOk());
        assertEquals(0, statusRepository.count());
    }
}
