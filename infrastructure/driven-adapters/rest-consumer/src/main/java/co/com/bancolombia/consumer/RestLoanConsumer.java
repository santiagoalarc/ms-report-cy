package co.com.bancolombia.consumer;

import co.com.bancolombia.consumer.dto.LoanDTO;
import co.com.bancolombia.enums.ReportErrorEnum;
import co.com.bancolombia.exceptions.ReportException;
import co.com.bancolombia.model.loan.Loan;
import co.com.bancolombia.model.loan.gateway.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class RestLoanConsumer implements LoanService {

    @Qualifier("webClientLoan")
    private final WebClient webClientLoan;

    private final ObjectMapper mapper;

    @Value("${adapter.restconsumer.loan.loan-today-url}")
    private String urlLoanToday;

    @Value("${adapter.restconsumer.retry}")
    private Integer retry;

    private final Logger log = Logger.getLogger(RestLoanConsumer.class.getName());



    @Override
    public Mono<List<Loan>> findLoansToday(String token) {
        log.info("Calling findLoansToday with token: " + (token != null ? "***PRESENT***" : "NULL"));

        return webClientLoan.get()
                .uri(uriBuilder -> uriBuilder
                        .path(urlLoanToday)
                        .build())
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .onStatus(
                        HttpStatus.INTERNAL_SERVER_ERROR::equals,
                        response -> {
                            log.severe("ERROR status 500 from API for URI: " + urlLoanToday);
                            return response.bodyToMono(String.class)
                                    .map(body -> new ReportException(ReportErrorEnum.ADMIN_CREDENTIALS_NO_VALID));
                        })
                .onStatus(
                        HttpStatus.UNAUTHORIZED::equals,
                        response -> {
                            log.severe("ERROR status 401 (Unauthorized) from API for URI: " + urlLoanToday);
                            return response.bodyToMono(String.class)
                                    .map(body -> new ReportException(ReportErrorEnum.ADMIN_CREDENTIALS_NO_VALID));
                        })
                .onStatus(
                        HttpStatus.FORBIDDEN::equals,
                        response -> {
                            log.severe("ERROR status 403 (Forbidden) from API for URI: " + urlLoanToday);
                            return response.bodyToMono(String.class)
                                    .map(body -> new ReportException(ReportErrorEnum.ADMIN_CREDENTIALS_NO_VALID));
                        })
                .bodyToFlux(LoanDTO.class)
                .retry(retry)
                .map(response -> mapper.convertValue(response, Loan.class))
                .collectList()
                .doOnSuccess(loans -> log.info("Successfully retrieved " + loans.size() + " loans"))
                .doOnError(error -> log.severe("Error retrieving loans: " + error.getMessage()))
                .onErrorResume(error -> {
                    log.severe("ERROR RestConsumer in findLoansToday: " + error.getMessage());
                    return Mono.error(new ReportException(ReportErrorEnum.ADMIN_CREDENTIALS_NO_VALID));
                });
    }
}
