package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.dto.LabelDto;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
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

    @BeforeEach
    public void testInit() throws Exception {
        utils.createDefaultLabel();
    }

    @AfterEach
    public void clear() {
        utils.tearDown();
    }


    @Test
    public void testCreateLabel() throws Exception {
        assertEquals(1, labelRepository.count());
        utils.createLabel(new LabelDto("Test label 1")).andExpect(status().isCreated());
        assertEquals(2, labelRepository.count());
    }


    @Test
    public void testCreatedLabelFails() throws Exception {
        final var labelDto = new LabelDto("");
        final var postRequest = post(BASE_URL + LABEL_CONTROLLER_PATH)
                .content(asJson(labelDto))
                .contentType(APPLICATION_JSON);
        utils.perform(postRequest, TEST_USERNAME).andExpect(status().isBadRequest());
        assertEquals(1, labelRepository.count());
    }


    @Test
    public void testUpdateLabel() throws Exception {
        final Long existLabelId = labelRepository.findAll().get(0).getId();
        final LabelDto updateLabelDto = new LabelDto("New label");
        final var updateRequest = put(
                BASE_URL + LABEL_CONTROLLER_PATH + ID, existLabelId)
                .content(asJson(updateLabelDto))
                .contentType(APPLICATION_JSON);
        utils.perform(updateRequest, TEST_USERNAME).andExpect(status().isOk());
        assertTrue(labelRepository.existsById(existLabelId));
        assertEquals(labelRepository.findAll().get(0).getName(), updateLabelDto.getName());
    }


    @Test
    public void getAllLabels() throws Exception {
        final var labelDto = new LabelDto("Test label 1");
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
        assertThat(labels.size()).isEqualTo(2);
    }


    @Test
    public void deleteLabel() throws Exception {
        assertEquals(1, labelRepository.count());
        utils.perform(delete(BASE_URL + LABEL_CONTROLLER_PATH + ID, labelRepository.findAll().get(0).getId()),
                        TEST_USERNAME)
                .andExpect(status().isOk());
        assertEquals(0, labelRepository.count());
    }
}
