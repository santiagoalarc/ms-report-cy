package co.com.bancolombia.sqs.listener;

import co.com.bancolombia.sqs.listener.dto.DailyReportDTO;
import co.com.bancolombia.usecase.dailyreport.DailyReportUseCase;
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
public class DailyReportSQSProcessor  implements Function<Message, Mono<Void>> {

    private final DailyReportUseCase dailyReportUseCase;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Logger log = Logger.getLogger(DailyReportSQSProcessor.class.getName());

    @Override
    public Mono<Void> apply(Message message) {

        log.info("MESSAGE ARRIVED IN SQSProcessor :: message " + message);

        try {

            DailyReportDTO dailyReportDTO = objectMapper.readValue(message.body(), DailyReportDTO.class);
            boolean isReport = dailyReportDTO.report();
            return dailyReportUseCase.execute(isReport)
                    .then();

        } catch (IOException e) {
            log.info("ERROR PARSING MESSAGE IN SQSProcessor :: " + e);
            return Mono.error(e);
        }
    }
}
