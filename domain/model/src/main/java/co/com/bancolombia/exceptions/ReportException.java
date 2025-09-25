package co.com.bancolombia.exceptions;

import co.com.bancolombia.enums.ReportErrorEnum;

public class ReportException extends RuntimeException {

    private final ReportErrorEnum reportErrorEnum;


    public ReportException(ReportErrorEnum reportErrorEnum) {
        super(reportErrorEnum.name());
        this.reportErrorEnum = reportErrorEnum;
    }

    public ReportErrorEnum getError(){
        return reportErrorEnum;
    }
}
