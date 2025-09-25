package co.com.bancolombia.snsaws;

import co.com.bancolombia.model.loan.gateway.LoanEmailService;
import co.com.bancolombia.model.loanapprovalreport.ReportInfo;
import co.com.bancolombia.snsaws.config.AwsProperties;
import io.awspring.cloud.sns.core.SnsTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.logging.Logger;

@Service
public class SnsService implements LoanEmailService {

    @Autowired
    private AwsProperties awsProperties;

    @Value("${aws.sns.email.template-daily-report}")
    private String dailyReportHTMLPath;

    private final SnsTemplate snsTemplate;

    private final Logger log = Logger.getLogger(SnsService.class.getName());

    @Autowired
    public SnsService(SnsTemplate snsTemplate) {
        this.snsTemplate = snsTemplate;
    }

    @Override
    public Mono<Void> sendEmail(ReportInfo message, int loanSize) {
        log.info("ENTER TO SnsService :: sendEmail - Reporte para: " + message.getTotalApprovedLoans() + " préstamos.");

        String emailContent = buildPlainTextEmail(message, loanSize);

        return Mono.fromRunnable(() -> snsTemplate.sendNotification(
                        awsProperties.getTopicArn(),
                        emailContent,
                        "Resumen de Créditos Aprobados Hoy"
                ))
                .then();
    }

    private String buildPlainTextEmail(ReportInfo reportInfo, int loanSize) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        String formattedAmount = currencyFormatter.format(reportInfo.getTotalAmountApprovedLoans());

        String emailBody = String.format(
                "Hola,\n\n" +
                        "Aquí tienes un resumen de las métricas de créditos aprobados en la jornada de hoy:\n\n" +
                        "==================================================\n" +
                        "  TOTAL DE CRÉDITOS APROBADOS HASTA LA FECHA: %d\n" +
                        "  MONTO TOTAL DESEMBOLSADO:    %s\n" +
                        "  TOTAL CRÉDITOS HOY: %d\n" +
                        "==================================================\n\n" +
                        "Saludos cordiales,\n" +
                        "El equipo de Reportes\n\n" +
                        "Este es un correo automático. Por favor, no responder.\n",
                reportInfo.getTotalApprovedLoans(),
                formattedAmount,
                loanSize
        );

        return emailBody;
    }
}