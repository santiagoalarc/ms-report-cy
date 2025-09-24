package co.com.bancolombia.api;

import co.com.bancolombia.api.config.ReportPath;
import co.com.bancolombia.api.dto.ReportDTORes;
import co.com.bancolombia.api.exceptions.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterRest {

    private final Handler reportHandler;
    private final ReportPath reportPath;
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/reports",
                    method = RequestMethod.GET,
                    operation = @Operation(
                            operationId = "getLoanApprovalReport",
                            summary = "Get loan approval summary report",
                            description = "Retrieves comprehensive statistics about loan approvals including total count of approved loans and total monetary amount approved. This endpoint provides consolidated reporting data for administrative purposes.",
                            tags = {"Loan Approval Reports"},
                            security = @SecurityRequirement(name = "bearerAuth"),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Successfully retrieved loan approval report data",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = ReportDTORes.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "401",
                                            description = "Unauthorized - Authentication token is missing, invalid, or expired",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = ErrorResponse.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "403",
                                            description = "Forbidden - User does not have ADMIN authority required to access this resource",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = ErrorResponse.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Internal Server Error - An unexpected error occurred while processing the request",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = ErrorResponse.class)
                                            )
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(GET(reportPath.getReports()), reportHandler::listenGETUseCase);
    }

    @Bean
    @RouterOperation(operation = @Operation(
            operationId = "healthCheck",
            summary = "Health check endpoint",
            description = "Simple health check endpoint to verify that the API service is running and responding correctly. This endpoint can be used by monitoring systems, load balancers, or orchestration tools to check service availability. No authentication is required for this endpoint.",
            tags = { "Health Check" },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Service is healthy and running correctly",
                            content = @Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(
                                            type = "string",
                                            example = "ok",
                                            description = "Simple text response indicating service health status"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error - Service may be experiencing issues",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "503",
                            description = "Service unavailable - Service is temporarily unable to handle requests",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    ))
    public RouterFunction<ServerResponse> healthCheck(){
        return route(GET("/health"), reportHandler::healthCheck);
    }
}
