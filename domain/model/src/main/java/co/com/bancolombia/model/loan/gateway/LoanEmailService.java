package co.com.bancolombia.model.loan.gateway;

import co.com.bancolombia.model.loanapprovalreport.ReportInfo;
import reactor.core.publisher.Mono;

public interface LoanEmailService {

    Mono<Void> sendEmail(ReportInfo reportInfo, int totalLoanToday);
}
