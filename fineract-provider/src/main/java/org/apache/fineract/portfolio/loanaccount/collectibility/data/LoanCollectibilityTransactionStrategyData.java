package org.apache.fineract.portfolio.loanaccount.collectibility.data;

import org.apache.fineract.portfolio.loanaccount.domain.Loan;

public class LoanCollectibilityTransactionStrategyData {

	private Long id;
	private Long loanId;
	private Long productId;
	private Long criteriaId;
	private Long collectibilityAccount;
	private Long collectibilityCif;
	private Long manualCollectibility;
	private Long loanTransactionStrategyAccount;
	private Long loanTransactionStrategyCif;
	private Long loanTransactionStrategyManual;
	private Loan loan;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getLoanId() {
		return loanId;
	}

	public void setLoanId(Long loanId) {
		this.loanId = loanId;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Long getCriteriaId() {
		return criteriaId;
	}

	public void setCriteriaId(Long criteriaId) {
		this.criteriaId = criteriaId;
	}

	public Long getCollectibilityAccount() {
		return collectibilityAccount;
	}

	public void setCollectibilityAccount(Long collectibilityAccount) {
		this.collectibilityAccount = collectibilityAccount;
	}

	public Long getCollectibilityCif() {
		return collectibilityCif;
	}

	public void setCollectibilityCif(Long collectibilityCif) {
		this.collectibilityCif = collectibilityCif;
	}

	public Long getManualCollectibility() {
		return manualCollectibility;
	}

	public void setManualCollectibility(Long manualCollectibility) {
		this.manualCollectibility = manualCollectibility;
	}

	public Long getLoanTransactionStrategyAccount() {
		return loanTransactionStrategyAccount;
	}

	public void setLoanTransactionStrategyAccount(Long loanTransactionStrategyAccount) {
		this.loanTransactionStrategyAccount = loanTransactionStrategyAccount;
	}

	public Long getLoanTransactionStrategyCif() {
		return loanTransactionStrategyCif;
	}

	public void setLoanTransactionStrategyCif(Long loanTransactionStrategyCif) {
		this.loanTransactionStrategyCif = loanTransactionStrategyCif;
	}

	public Long getLoanTransactionStrategyManual() {
		return loanTransactionStrategyManual;
	}

	public void setLoanTransactionStrategyManual(Long loanTransactionStrategyManual) {
		this.loanTransactionStrategyManual = loanTransactionStrategyManual;
	}

	public Loan getLoan() {
		return loan;
	}

	public void setLoan(Loan loan) {
		this.loan = loan;
	}
}
