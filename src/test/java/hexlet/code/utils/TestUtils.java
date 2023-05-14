package hexlet.code.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.LabelDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.UserDto;
import hexlet.code.component.JWTHelper;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Map;

import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static hexlet.code.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;
import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class TestUtils {

    public static final String BASE_URL = "/api";
    public static final String TEST_USERNAME = "example1@email.com";
    public static final String TEST_USERNAME_2 = "example2@email.com";
    private final UserDto testRegistrationDto = new UserDto(
            TEST_USERNAME,
            "firstName",
            "lastName",
            "password"
    );
    private final TaskStatusDto testStatusDto = new TaskStatusDto("Test status");
    private final LabelDto testLabelDto = new LabelDto("Test label");

    public UserDto getTestRegistrationDto() {
        return testRegistrationDto;
    }

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskStatusRepository statusRepository;
    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private JWTHelper jwtHelper;
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    public void tearDown() {
        taskRepository.deleteAll();
        labelRepository.deleteAll();
        statusRepository.deleteAll();
        userRepository.deleteAll();
    }

//    public User getUserByEmail(final String email) {
//        return userRepository.findByEmail(email).get();
//    }


    public ResultActions regDefaultUser() throws Exception {
        return regUser(testRegistrationDto);
    }

    public ResultActions regUser(final UserDto dto) throws Exception {
        final var request = post(BASE_URL + USER_CONTROLLER_PATH)
                .content(asJson(dto))
                .contentType(APPLICATION_JSON);
        return perform(request);
    }

    public ResultActions createDefaultStatus() throws Exception {
        return createStatus(testStatusDto);
    }

    public ResultActions createStatus(final TaskStatusDto statusDto) throws Exception {
        final var request = post("/api" + TASK_STATUS_CONTROLLER_PATH)
                .content(asJson(statusDto))
                .contentType(APPLICATION_JSON);
        return perform(request, TEST_USERNAME);
    }

    public ResultActions createDefaultLabel() throws Exception {
        return createLabel(testLabelDto);
    }

    public ResultActions createLabel(LabelDto testLabelDto) throws Exception {
        final var request = post("/api" + LABEL_CONTROLLER_PATH)
                .content(asJson(testLabelDto))
                .contentType(APPLICATION_JSON);
        return perform(request, TEST_USERNAME);
    }



    public ResultActions perform(final MockHttpServletRequestBuilder request, final String byUser) throws Exception {
        final String token = jwtHelper.expiring(Map.of("username", byUser));
        request.header(AUTHORIZATION, token);
        return perform(request);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request) throws Exception {
        return mockMvc.perform(request);
    }

    public static String asJson(final Object object) throws JsonProcessingException {
        return MAPPER.writeValueAsString(object);
    }

    public static <T> T fromJson(final String json, final TypeReference<T> to) throws JsonProcessingException {
        return MAPPER.readValue(json, to);
    }
}
