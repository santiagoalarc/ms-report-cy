package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.ReportDTORes;
import co.com.bancolombia.model.loanapprovalreport.ReportInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel= "Spring")
public interface ReportMapper {

    ReportDTORes toResponse(ReportInfo reportInfo);
}
