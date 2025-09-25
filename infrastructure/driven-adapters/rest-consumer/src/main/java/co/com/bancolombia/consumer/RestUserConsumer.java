package co.com.bancolombia.consumer;

import co.com.bancolombia.consumer.dto.TokenDTO;
import co.com.bancolombia.enums.ReportErrorEnum;
import co.com.bancolombia.exceptions.ReportException;
import co.com.bancolombia.model.token.Token;
import co.com.bancolombia.model.token.gateways.AuthorizationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class RestUserConsumer implements AuthorizationService {

    @Qualifier("webClientUser")
    private final WebClient webClientUser;

    private final ObjectMapper mapper;
    @Value("${adapter.restconsumer.user.token-url}")
    private String urlGetToken;

    @Value("${adapter.restconsumer.retry}")
    private Integer retry;

    @Value("${adapter.restconsumer.user.email}")
    private String email;

    @Value("${adapter.restconsumer.user.password}")
    private String password;


    private final Logger log = Logger.getLogger(RestLoanConsumer.class.getName());


    @Override
    public Mono<Token> generateToken() {
        log.info("ENTER TO RestUserConsumer - generateToken:: ");

        Map<String, String> payload = new HashMap<>();
        payload.put("email", email);
        payload.put("password", password);

        return webClientUser.post()
                .uri(uriBuilder -> uriBuilder
                        .path(urlGetToken)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(payload))
                .retrieve()
                .onStatus(
                        HttpStatus.INTERNAL_SERVER_ERROR::equals,
                        response -> {
                            log.info("ERROR status in generateToken from API: {} for URI: " + response.statusCode());
                            return response.bodyToMono(String.class).map(Exception::new);
                        })
                .bodyToMono(TokenDTO.class)
                .retry(retry)
                .map(response -> mapper.convertValue(response, Token.class))
                .onErrorResume(error -> {
                    log.info("ERROR RestConsumer in generateToken " + error.getMessage());
                    return Mono.error(new ReportException(ReportErrorEnum.ADMIN_CREDENTIALS_NO_VALID));
                });
    }
}
