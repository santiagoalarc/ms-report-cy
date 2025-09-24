package co.com.bancolombia.model.token.gateways;

import co.com.bancolombia.model.token.Token;
import reactor.core.publisher.Mono;

public interface AuthorizationService {

    Mono<Token> generateToken();
}
