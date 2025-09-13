package co.com.bancolombia.dynamodb;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class LoanApprovalReportEntity {

    private String reportName;
    private Long totalApprovedLoans;

    public LoanApprovalReportEntity() {
    }

    public LoanApprovalReportEntity(String reportName, Long totalApprovedLoans) {
        this.reportName = reportName;
        this.totalApprovedLoans = totalApprovedLoans;
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute("report_name")
    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    @DynamoDbAttribute("value")
    public Long getTotalApprovedLoans() {
        return totalApprovedLoans;
    }

    public void setTotalApprovedLoans(Long totalApprovedLoans) {
        this.totalApprovedLoans = totalApprovedLoans;
    }
}
