package co.com.bancolombia.usecase.listReport;

import co.com.bancolombia.enums.DynamoTableNameEnum;
import co.com.bancolombia.model.loanapprovalreport.ReportInfo;
import co.com.bancolombia.model.loanapprovalreport.gateways.LoanApprovalReportRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@RequiredArgsConstructor
public class ListReportUseCase {

    private final LoanApprovalReportRepository loanApprovalReportRepository;

    Logger log = Logger.getLogger(ListReportUseCase.class.getName());

    public Mono<ReportInfo> execute() {

        log.info("ENTER TO ListReportUseCase");

        return loanApprovalReportRepository.getById(DynamoTableNameEnum.TOTAL_APPROVED_LOANS.getId())
                .map(loanApprovalReport -> ReportInfo.builder()
                        .totalApprovedLoans(loanApprovalReport.getTotalApprovedLoans())
                        .build())
                .flatMap(reportInfo -> loanApprovalReportRepository.getById(DynamoTableNameEnum.TOTAL_AMOUNT_APPROVED.getId())
                        .map(loanApprovalReport -> reportInfo.toBuilder()
                                .totalAmountApprovedLoans(loanApprovalReport.getTotalApprovedLoans())
                                .build()));
    }

}
