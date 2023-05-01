package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.dto.LabelDto;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.controller.LabelController.ID;
import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;
import static hexlet.code.utils.TestUtils.TEST_USERNAME;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
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
public class LabelControllerIT {

    public static final String BASE_URL = "/api";
    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private TestUtils utils;
    @AfterEach
    public void clear() {
        utils.tearDown();
    }


    @Test
    public void testCreateLabel() throws Exception {
        utils.regDefaultUser();
        final var labelDto = new LabelDto("new");
        final var postRequest = post(BASE_URL + LABEL_CONTROLLER_PATH)
                .content(asJson(labelDto))
                .contentType(APPLICATION_JSON);
        final var response = utils.perform(postRequest, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();
        final Label expectedLabel = labelRepository.findAll().get(0);
        final Label label = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertTrue(labelRepository.existsById(label.getId()));
        assertEquals(expectedLabel.getId(), label.getId());
        assertEquals(expectedLabel.getName(), label.getName());
    }


    @Test
    public void testCreatedLabelFails() throws Exception {
        utils.regDefaultUser();
        final var labelDto = new LabelDto("");
        final var postRequest = post(BASE_URL + LABEL_CONTROLLER_PATH)
                .content(asJson(labelDto))
                .contentType(APPLICATION_JSON);
        assertEquals(0, labelRepository.count());
    }


    @Test
    public void testUpdateLabel() throws Exception {
        utils.regDefaultUser();
        final var postRequest = post(BASE_URL + LABEL_CONTROLLER_PATH)
                .content(asJson(new LabelDto("new")))
                .contentType(APPLICATION_JSON);
        utils.perform(postRequest, TEST_USERNAME);
        Label createdLabel = labelRepository.findAll().get(0);
        final var labelDto = new LabelDto("feature");
        final var putRequest = put(BASE_URL + LABEL_CONTROLLER_PATH + ID, createdLabel.getId())
                .content(asJson(labelDto))
                .contentType(APPLICATION_JSON);
        final var response = utils.perform(putRequest, TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        createdLabel = labelRepository.findAll().get(0);
        final Label label = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertEquals(createdLabel.getId(), label.getId());
        assertEquals(createdLabel.getName(), labelDto.getName());
    }


    @Test
    public void getAllLabels() throws Exception {
        utils.regDefaultUser();
        final var labelDto = new LabelDto("new");
        final var postRequest = post(BASE_URL + LABEL_CONTROLLER_PATH)
                .content(asJson(labelDto))
                .contentType(APPLICATION_JSON);
        utils.perform(postRequest, TEST_USERNAME)
                .andExpect(status().isCreated());
        final var response = utils.perform(get(BASE_URL + LABEL_CONTROLLER_PATH), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        final List<Label> labels = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(labels.size()).isEqualTo(1);
    }


    @Test
    public void deleteLabel() throws Exception {
        utils.regDefaultUser();
        final var labelDto = new LabelDto("bug");
        final var postRequest = post(BASE_URL + LABEL_CONTROLLER_PATH)
                .content(asJson(labelDto))
                .contentType(APPLICATION_JSON);
        utils.perform(postRequest, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();
        utils.perform(delete(BASE_URL + LABEL_CONTROLLER_PATH + ID,
                                labelRepository.findAll().get(0).getId()),
                        TEST_USERNAME)
                .andExpect(status().isOk());
        assertEquals(0, labelRepository.count());
    }
}
