package co.com.bancolombia.model.loanapprovalreport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
public class ReportInfo {

    private Long totalApprovedLoans;
    private Long totalAmountApprovedLoans;
}
