package org.apache.fineract.portfolio.loanaccount.collectibility.domain;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoanCollectibilityRepository extends JpaRepository<LoanCollectibility, Long>, JpaSpecificationExecutor<LoanCollectibility> {
	
	@Query("SELECT lc FROM LoanCollectibility lc WHERE lc.id = :id")
	LoanCollectibility findOneByCategory(@Param("id") Long id);
	
	@Query("SELECT lc FROM LoanCollectibility lc WHERE lc.loanId = :loanId")
	LoanCollectibility findOneByLoanId(@Param("loanId") Long loanId);
	
	@Query("SELECT lc FROM LoanCollectibility lc WHERE lc.submittedOn = :submittedOn")
	List<LoanCollectibility> findBySubmittedDate(@Param("submittedOn") Date submittedOn);
}
