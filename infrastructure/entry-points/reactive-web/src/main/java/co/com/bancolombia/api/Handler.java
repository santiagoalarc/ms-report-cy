package co.com.bancolombia.api;

import co.com.bancolombia.api.mapper.ReportMapper;
import co.com.bancolombia.usecase.listReport.ListReportUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final ListReportUseCase listReportUseCase;

    private final ReportMapper reportMapper;

    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<ServerResponse> listenGETUseCase(ServerRequest serverRequest) {

        return listReportUseCase.execute()
                .map(reportMapper::toResponse)
                .flatMap(reportDTORes -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(reportDTORes)
                );
    }
}
