package co.com.bancolombia.sqs.listener;

import co.com.bancolombia.sqs.listener.dto.LoanAmountDTO;
import co.com.bancolombia.usecase.updatequantityfund.UpdateQuantityFundUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.io.IOException;
import java.util.function.Function;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class SQSProcessor implements Function<Message, Mono<Void>> {

    private final UpdateQuantityFundUseCase updateQuantityFundUseCase;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Logger log = Logger.getLogger(SQSProcessor.class.getName());

    @Override
    public Mono<Void> apply(Message message) {

        log.info("MESSAGE ARRIVED IN SQSProcessor :: message " + message);

        try {

            LoanAmountDTO loanAmountDTO = objectMapper.readValue(message.body(), LoanAmountDTO.class);
            Long approvedAmount = loanAmountDTO.approvedAmount();
            return updateQuantityFundUseCase.execute(approvedAmount)
                    .then();

        } catch (IOException e) {
            log.info("ERROR PARSING MESSAGE IN SQSProcessor :: " + e);
            return Mono.error(e);
        }
    }
}
