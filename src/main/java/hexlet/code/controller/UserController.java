package hexlet.code.controller;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;

@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + USER_CONTROLLER_PATH)
public class UserController {

    public static final String USER_CONTROLLER_PATH = "/users";
    public static final String ID = "/{id}";
    private final UserServiceImpl userService;
    private final UserRepository userRepository;
    private static final String ONLY_OWNER_BY_ID =
            "@userRepository.findById(#id).get().getEmail() == authentication.getName()";


    @ApiResponses(@ApiResponse(responseCode = "200"))
    @Operation(summary = "Get user")
    @GetMapping(ID)
    public User getUserById(@PathVariable final Long id) {
        return userRepository.findById(id).get();
    }


    @Operation(summary = "Get all users")
    @ApiResponses(@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = User.class))))
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .toList();
    }


    @Operation(summary = "Create user")
    @ApiResponse(responseCode = "201", description = "User created")
    @PostMapping
    @ResponseStatus(CREATED)
    public User createNewUser(@RequestBody @Valid final UserDto dto) {
        return userService.createNewUser(dto);
    }


    @Operation(summary = "Update user")
    @PutMapping(ID)
    public User updateUser(@RequestBody @Valid final UserDto dto, @PathVariable long id) {
        return userService.updateUser(id, dto);
    }


    @Operation(summary = "Delete user")
    @PreAuthorize(ONLY_OWNER_BY_ID)
    @DeleteMapping(ID)
    public void deleteUser(@PathVariable long id) {
        userService.deleteUserById(id);
    }
}
