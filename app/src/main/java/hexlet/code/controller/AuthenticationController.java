package hexlet.code.controller;

import hexlet.code.dto.AuthenticationRequest;
import hexlet.code.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static hexlet.code.controller.AuthenticationController.AUTH_CONTROLLER_PATH;

@RestController
@RequestMapping("${base-url}" + AUTH_CONTROLLER_PATH)
public class AuthenticationController {

    public static final String AUTH_CONTROLLER_PATH = "/login";
    @Autowired
    private AuthenticationService service;

    @Operation(summary = "Get authentication token")
    @ApiResponse(responseCode = "200", description = "Token created",
            content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)))
    @PostMapping
    public ResponseEntity<?> createToken(@RequestBody AuthenticationRequest request) {
        try {
            return ResponseEntity.ok(service.authenticate(request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
    }
}
