package co.com.bancolombia.usecase.listReport;

import co.com.bancolombia.enums.DynamoTableNameEnum;
import co.com.bancolombia.model.loanapprovalreport.LoanApprovalReport;
import co.com.bancolombia.model.loanapprovalreport.ReportInfo;
import co.com.bancolombia.model.loanapprovalreport.gateways.LoanApprovalReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListReportUseCaseTest {

    @Mock
    private LoanApprovalReportRepository loanApprovalReportRepository;

    @InjectMocks
    private ListReportUseCase listReportUseCase;

    @BeforeEach
    void setUp() {
        listReportUseCase = new ListReportUseCase(loanApprovalReportRepository);
    }

    @Test
    void shouldReturnReportInfoSuccessfully() {
        LoanApprovalReport totalLoansReport = LoanApprovalReport.builder()
                .totalApprovedLoans(100L)
                .build();

        LoanApprovalReport totalAmountReport = LoanApprovalReport.builder()
                .totalApprovedLoans(50000L)
                .build();

        when(loanApprovalReportRepository.getById(DynamoTableNameEnum.TOTAL_APPROVED_LOANS.getId()))
                .thenReturn(Mono.just(totalLoansReport));
        when(loanApprovalReportRepository.getById(DynamoTableNameEnum.TOTAL_AMOUNT_APPROVED.getId()))
                .thenReturn(Mono.just(totalAmountReport));

        ReportInfo expectedReportInfo = ReportInfo.builder()
                .totalApprovedLoans(100L)
                .totalAmountApprovedLoans(50000L)
                .build();

        StepVerifier.create(listReportUseCase.execute())
                .expectNext(expectedReportInfo)
                .verifyComplete();
    }

    @Test
    void shouldHandleErrorWhenFirstRepositoryCallFails() {
        when(loanApprovalReportRepository.getById(DynamoTableNameEnum.TOTAL_APPROVED_LOANS.getId()))
                .thenReturn(Mono.error(new RuntimeException("Repository error")));

        StepVerifier.create(listReportUseCase.execute())
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void shouldHandleErrorWhenSecondRepositoryCallFails() {
        LoanApprovalReport totalLoansReport = LoanApprovalReport.builder()
                .totalApprovedLoans(100L)
                .build();

        when(loanApprovalReportRepository.getById(DynamoTableNameEnum.TOTAL_APPROVED_LOANS.getId()))
                .thenReturn(Mono.just(totalLoansReport));
        when(loanApprovalReportRepository.getById(DynamoTableNameEnum.TOTAL_AMOUNT_APPROVED.getId()))
                .thenReturn(Mono.error(new RuntimeException("Repository error")));

        StepVerifier.create(listReportUseCase.execute())
                .expectError(RuntimeException.class)
                .verify();
    }
}