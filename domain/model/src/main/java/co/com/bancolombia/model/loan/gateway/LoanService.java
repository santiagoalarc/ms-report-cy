package co.com.bancolombia.model.loan.gateway;

import co.com.bancolombia.model.loan.Loan;
import reactor.core.publisher.Mono;

import java.util.List;

public interface LoanService {

  Mono<List<Loan>> findLoansToday(String token);
}
