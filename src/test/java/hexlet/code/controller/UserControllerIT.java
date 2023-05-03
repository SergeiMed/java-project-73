package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.dto.LoginDto;
import hexlet.code.dto.UserDto;
import hexlet.code.config.SpringConfigForIT;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Set;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.controller.UserController.ID;
import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static hexlet.code.utils.TestUtils.TEST_USERNAME;
import static hexlet.code.utils.TestUtils.TEST_USERNAME_2;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import static hexlet.code.config.security.SecurityConfig.LOGIN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class UserControllerIT {

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
    public void testRegistration() throws Exception {
        assertEquals(0, userRepository.count());
        utils.regDefaultUser().andExpect(status().isCreated());
        assertEquals(1, userRepository.count());
    }

    @Test
    public void testCreateInvalidFistName() throws Exception {
        final var userDto = new UserDto(TEST_USERNAME, null, "last name", "pass");
        final var postRequest = MockMvcRequestBuilders.post(BASE_URL + USER_CONTROLLER_PATH)
                .content(asJson(userDto))
                .contentType(APPLICATION_JSON);
        utils.perform(postRequest, TEST_USERNAME).andExpect(status().is(400));
    }

    @Test
    public void testGetUserById() throws Exception {
        utils.regDefaultUser();
        final User expectedUser = userRepository.findAll().get(0);
        final var response = utils.perform(
                        get(BASE_URL + USER_CONTROLLER_PATH + ID, expectedUser.getId()),
                        expectedUser.getEmail()
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse();
        final User user = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertEquals(expectedUser.getId(), user.getId());
        assertEquals(expectedUser.getEmail(), user.getEmail());
    }

    @Test
    public void testGetAllUsers() throws Exception {
        utils.regDefaultUser();
        final var response = utils.perform(get(BASE_URL + USER_CONTROLLER_PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        final Set<User> users = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(users.size()).isEqualTo(1);
    }

    @Test
    public void testLogin() throws Exception {
        utils.regDefaultUser();
        final LoginDto loginDto = new LoginDto(
                utils.getTestRegistrationDto().getEmail(),
                utils.getTestRegistrationDto().getPassword()
        );
        final var loginRequest = MockMvcRequestBuilders
                .post(BASE_URL + LOGIN)
                .content(asJson(loginDto))
                .contentType(APPLICATION_JSON);
        utils.perform(loginRequest).andExpect(status().isOk());
    }

    @Test
    public void testLoginFail() throws Exception {
        final LoginDto loginDto = new LoginDto();
        final var loginRequest = MockMvcRequestBuilders
                .post(BASE_URL + LOGIN)
                .content(asJson(loginDto))
                .contentType(APPLICATION_JSON);
        utils.perform(loginRequest).andExpect(status().isUnauthorized());
    }

    @Test
    public void testUpdateUser() throws Exception {
        utils.regDefaultUser();
        final Long userId = userRepository.findByEmail(TEST_USERNAME).get().getId();
        final var userDto = new UserDto(TEST_USERNAME_2, "newFirstName", "newLastName", "newPassword");
        final var updateRequest = MockMvcRequestBuilders
                .put(BASE_URL + USER_CONTROLLER_PATH + ID, userId)
                .content(asJson(userDto))
                .contentType(APPLICATION_JSON);
        utils.perform(updateRequest, TEST_USERNAME_2).andExpect(status().isOk());
        assertTrue(userRepository.existsById(userId));
        assertNull(userRepository.findByEmail(TEST_USERNAME).orElse(null));
        assertNotNull(userRepository.findByEmail(TEST_USERNAME_2).orElse(null));
    }

    @Test
    public void testDeleteUser() throws Exception {
        utils.regDefaultUser();
        final Long userId = userRepository.findByEmail(TEST_USERNAME).get().getId();
        utils.perform(delete(BASE_URL + USER_CONTROLLER_PATH + ID, userId), TEST_USERNAME)
                .andExpect(status().isOk());
        assertEquals(0, userRepository.count());
    }

    @Test
    public void deleteUserFails() throws Exception {
        utils.regDefaultUser();
        utils.regUser(new UserDto(
                TEST_USERNAME_2,
                "firstName",
                "lastName",
                "password"
        ));
        final Long userId = userRepository.findByEmail(TEST_USERNAME).get().getId();
        utils.perform(delete(BASE_URL + USER_CONTROLLER_PATH + ID, userId), TEST_USERNAME_2)
                .andExpect(status().isForbidden());
        assertEquals(2, userRepository.count());
    }
}
