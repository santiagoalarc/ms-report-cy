package co.com.bancolombia.usecase.updatequantityfund;

import co.com.bancolombia.enums.DynamoTableNameEnum;
import co.com.bancolombia.model.loanapprovalreport.LoanApprovalReport;
import co.com.bancolombia.model.loanapprovalreport.gateways.LoanApprovalReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateQuantityFundUseCaseTest {

    @Mock
    private LoanApprovalReportRepository loanApprovalReportRepository;

    @InjectMocks
    private UpdateQuantityFundUseCase updateQuantityFundUseCase;

    @BeforeEach
    void setUp(){
        updateQuantityFundUseCase = new UpdateQuantityFundUseCase(loanApprovalReportRepository);
    }

    @Test
    void shouldSaveInfoSuccessfully() {

        LoanApprovalReport loanApprovalReport = LoanApprovalReport.builder()
                .reportName(DynamoTableNameEnum.TOTAL_APPROVED_LOANS.name())
                .totalApprovedLoans(10L)
                .build();

        LoanApprovalReport loanApprovalReportUpdated = LoanApprovalReport.builder()
                .reportName(DynamoTableNameEnum.TOTAL_APPROVED_LOANS.name())
                .totalApprovedLoans(11L)
                .build();

        LoanApprovalReport loanApprovalReportAmount = LoanApprovalReport.builder()
                .reportName(DynamoTableNameEnum.TOTAL_AMOUNT_APPROVED.name())
                .totalApprovedLoans(2000L)
                .build();

        LoanApprovalReport loanApprovalReportUpdatedAmount = LoanApprovalReport.builder()
                .reportName(DynamoTableNameEnum.TOTAL_AMOUNT_APPROVED.name())
                .totalApprovedLoans(3000L)
                .build();

        when(loanApprovalReportRepository.getById(DynamoTableNameEnum.TOTAL_APPROVED_LOANS.getId()))
                .thenReturn(Mono.just(loanApprovalReport));
        when(loanApprovalReportRepository.getById(DynamoTableNameEnum.TOTAL_AMOUNT_APPROVED.getId()))
                .thenReturn(Mono.just(loanApprovalReportAmount));
        when(loanApprovalReportRepository.save(loanApprovalReportUpdated))
                .thenReturn(Mono.just(loanApprovalReportUpdated));
        when(loanApprovalReportRepository.save(loanApprovalReportUpdatedAmount))
                .thenReturn(Mono.just(loanApprovalReportUpdatedAmount));

        StepVerifier.create(updateQuantityFundUseCase.execute(1000L))
                .expectNext(loanApprovalReportUpdatedAmount)
                .verifyComplete();

    }

    @Test
    void shouldHandleErrorWhenGettingTotalApprovedLoans() {
        Long approvedAmount = 1000L;

        when(loanApprovalReportRepository.getById(DynamoTableNameEnum.TOTAL_APPROVED_LOANS.getId()))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(updateQuantityFundUseCase.execute(approvedAmount))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void shouldHandleErrorWhenSavingTotalApprovedLoans() {
        Long approvedAmount = 1000L;

        LoanApprovalReport initialLoansReport = LoanApprovalReport.builder()
                .reportName(DynamoTableNameEnum.TOTAL_APPROVED_LOANS.name())
                .totalApprovedLoans(10L)
                .build();

        when(loanApprovalReportRepository.getById(DynamoTableNameEnum.TOTAL_APPROVED_LOANS.getId()))
                .thenReturn(Mono.just(initialLoansReport));
        when(loanApprovalReportRepository.save(any(LoanApprovalReport.class)))
                .thenReturn(Mono.error(new RuntimeException("Save error")));

        StepVerifier.create(updateQuantityFundUseCase.execute(approvedAmount))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void shouldHandleErrorWhenGettingTotalAmountApproved() {
        Long approvedAmount = 1000L;

        LoanApprovalReport initialLoansReport = LoanApprovalReport.builder()
                .reportName(DynamoTableNameEnum.TOTAL_APPROVED_LOANS.name())
                .totalApprovedLoans(10L)
                .build();

        LoanApprovalReport updatedLoansReport = LoanApprovalReport.builder()
                .reportName(DynamoTableNameEnum.TOTAL_APPROVED_LOANS.name())
                .totalApprovedLoans(11L)
                .build();

        when(loanApprovalReportRepository.getById(DynamoTableNameEnum.TOTAL_APPROVED_LOANS.getId()))
                .thenReturn(Mono.just(initialLoansReport));
        when(loanApprovalReportRepository.save(updatedLoansReport))
                .thenReturn(Mono.just(updatedLoansReport));
        when(loanApprovalReportRepository.getById(DynamoTableNameEnum.TOTAL_AMOUNT_APPROVED.getId()))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(updateQuantityFundUseCase.execute(approvedAmount))
                .expectError(RuntimeException.class)
                .verify();
    }
    @Test
    void shouldHandleZeroApprovedAmount() {
        Long approvedAmount = 0L;

        LoanApprovalReport initialLoansReport = LoanApprovalReport.builder()
                .reportName(DynamoTableNameEnum.TOTAL_APPROVED_LOANS.name())
                .totalApprovedLoans(5L)
                .build();

        LoanApprovalReport updatedLoansReport = LoanApprovalReport.builder()
                .reportName(DynamoTableNameEnum.TOTAL_APPROVED_LOANS.name())
                .totalApprovedLoans(6L)
                .build();

        LoanApprovalReport initialAmountReport = LoanApprovalReport.builder()
                .reportName(DynamoTableNameEnum.TOTAL_AMOUNT_APPROVED.name())
                .totalApprovedLoans(1000L)
                .build();

        LoanApprovalReport updatedAmountReport = LoanApprovalReport.builder()
                .reportName(DynamoTableNameEnum.TOTAL_AMOUNT_APPROVED.name())
                .totalApprovedLoans(1000L)
                .build();

        when(loanApprovalReportRepository.getById(DynamoTableNameEnum.TOTAL_APPROVED_LOANS.getId()))
                .thenReturn(Mono.just(initialLoansReport));
        when(loanApprovalReportRepository.save(updatedLoansReport))
                .thenReturn(Mono.just(updatedLoansReport));
        when(loanApprovalReportRepository.getById(DynamoTableNameEnum.TOTAL_AMOUNT_APPROVED.getId()))
                .thenReturn(Mono.just(initialAmountReport));
        when(loanApprovalReportRepository.save(updatedAmountReport))
                .thenReturn(Mono.just(updatedAmountReport));

        StepVerifier.create(updateQuantityFundUseCase.execute(approvedAmount))
                .expectNext(updatedAmountReport)
                .verifyComplete();
    }

}