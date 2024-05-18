package com.tsa.userdataaggregatorservice.web;

import com.tsa.userdataaggregatorservice.domain.User;
import com.tsa.userdataaggregatorservice.domain.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get all users")
    @ApiResponse(
            responseCode = "200",
            description = "Success",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = User.class))
                    )
            })
    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/filter")
    @Operation(summary = "Get users by selecting filters")
    @ApiResponse(
            responseCode = "200",
            description = "Success",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = User.class))
                    )
            })
    public List<User> findAllFilter(
            @Parameter(description = """
                    WHERE clause similar to WHERE Sql functional.
                    Restricted to AND, OR, LIKE.
                    Example[OR/AND]: id=id-1,OR,id=id-3 resolved as id='id-1' OR id='id-3'
                    Example[LIKE]: id LIKE id-1,OR,id LIKE id-3 resolved as id LIKE 'id-1' OR id LIKE 'id-3'
                    """)
            @RequestParam(name = "where", required = false) String where) {
        return userService.findAllFilter(where);
    }
}
