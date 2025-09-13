package co.com.bancolombia.model.loanapprovalreport.gateways;

import co.com.bancolombia.model.loanapprovalreport.LoanApprovalReport;
import reactor.core.publisher.Mono;

public interface LoanApprovalReportRepository {

    Mono<LoanApprovalReport> save(LoanApprovalReport loanApprovalReport);

    Mono<LoanApprovalReport> getById(String id);
}
