package co.com.bancolombia.usecase.dailyreport;

import co.com.bancolombia.model.loan.gateway.LoanEmailService;
import co.com.bancolombia.model.loan.gateway.LoanService;
import co.com.bancolombia.model.token.gateways.AuthorizationService;
import co.com.bancolombia.usecase.listReport.ListReportUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class DailyReportUseCase {

    private final ListReportUseCase listReportUseCase;
    private final AuthorizationService authorizationService;
    private final LoanService loanService;
    private final LoanEmailService loanEmailService;

    Logger log = Logger.getLogger(DailyReportUseCase.class.getName());

    public Mono<Void> execute(boolean isReport) {

        log.info("ENTER TO DailyReportUseCase :: isReport " + isReport);

        return Mono.just(isReport)
                .filter(isReportB -> isReportB)
                .flatMap(isReportB -> listReportUseCase.execute())
                .flatMap(reportInfo -> authorizationService.generateToken()
                        .flatMap(token -> loanService.findLoansToday(token.token())
                                .map(List::size)
                                .onErrorResume(error -> {
                                    log.warning("Error getting loans, continuing with empty list: " + error.getMessage());
                                    return Mono.just(0);
                                })
                        )
                        .flatMap(loansSize -> loanEmailService.sendEmail(reportInfo, loansSize)))
                .then();

    }
}
