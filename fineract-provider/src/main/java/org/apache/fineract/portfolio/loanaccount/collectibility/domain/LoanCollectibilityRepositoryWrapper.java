package org.apache.fineract.portfolio.loanaccount.collectibility.domain;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoanCollectibilityRepositoryWrapper {
	private final LoanCollectibilityRepository repository;

	@Autowired
	public LoanCollectibilityRepositoryWrapper(final LoanCollectibilityRepository repository) {
		this.repository = repository;
	}

	public LoanCollectibility findOneWithNotFoundDetection(final Long id) {
		final LoanCollectibility loanCollectibility = this.repository.findOne(id);
		if (loanCollectibility == null) {
			//throw new CollateralCategoryNotFoundException(id);
		}
		return loanCollectibility;
	}

	public LoanCollectibility findOneByLoanIdWithNotFoundDetection(final Long loanId) {
		final LoanCollectibility loanCollectibility = this.repository.findOneByLoanId(loanId);
		return loanCollectibility;
	}

	public List<LoanCollectibility> findBySubmittedDate(final Date date) {
		final List<LoanCollectibility> loanCollectibility = this.repository.findBySubmittedDate(date);
		if (loanCollectibility == null) {

		}
		return loanCollectibility;
	}

	public void save(final LoanCollectibility client) {
		this.repository.save(client);
	}
}
