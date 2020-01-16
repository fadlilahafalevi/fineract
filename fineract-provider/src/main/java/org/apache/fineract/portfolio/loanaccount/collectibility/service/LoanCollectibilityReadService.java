package org.apache.fineract.portfolio.loanaccount.collectibility.service;

import java.util.Collection;

import org.apache.fineract.portfolio.loanaccount.collectibility.data.LoanCollectibilityData;

public interface LoanCollectibilityReadService {

	public Collection<LoanCollectibilityData> retrieveLoanCollectibility();

	LoanCollectibilityData retrieveLoanCollectibilityByLoanId(Long loanId);

	LoanCollectibilityData retrieveLoanCollectibilityCifByClientId(Long clientId);
}
