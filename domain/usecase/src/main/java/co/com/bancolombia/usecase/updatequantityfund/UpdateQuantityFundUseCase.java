package co.com.bancolombia.usecase.updatequantityfund;

import co.com.bancolombia.enums.DynamoTableNameEnum;
import co.com.bancolombia.model.loanapprovalreport.LoanApprovalReport;
import co.com.bancolombia.model.loanapprovalreport.gateways.LoanApprovalReportRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@RequiredArgsConstructor
public class UpdateQuantityFundUseCase {

    private final LoanApprovalReportRepository loanApprovalReportRepository;

    Logger log = Logger.getLogger(UpdateQuantityFundUseCase.class.getName());

    public Mono<LoanApprovalReport> execute(Long approvedAmount) {

        log.info("ENTER TO UpdateQuantityFundUseCase :: ");


        return loanApprovalReportRepository.getById(DynamoTableNameEnum.TOTAL_APPROVED_LOANS.getId())
                .map(loanApproval -> loanApproval.toBuilder()
                        .totalApprovedLoans(loanApproval.getTotalApprovedLoans() + 1)
                        .build())
                .flatMap(loanApprovalReportRepository::save)
                .flatMap(loanApprovalReport -> loanApprovalReportRepository.getById(DynamoTableNameEnum.TOTAL_AMOUNT_APPROVED.getId())
                        .map(loanApprovalData -> loanApprovalData.toBuilder()
                                .totalApprovedLoans(loanApprovalData.getTotalApprovedLoans() + approvedAmount)
                                .build())
                        .flatMap(loanApprovalReportRepository::save)
                );

    }
}
