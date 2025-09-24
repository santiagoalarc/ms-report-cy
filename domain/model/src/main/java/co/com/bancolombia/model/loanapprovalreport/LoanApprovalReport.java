package co.com.bancolombia.model.loanapprovalreport;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class LoanApprovalReport {

    private String reportName;
    private Long totalApprovedLoans;
}
