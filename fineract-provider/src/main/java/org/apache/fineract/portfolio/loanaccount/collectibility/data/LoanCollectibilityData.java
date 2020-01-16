package org.apache.fineract.portfolio.loanaccount.collectibility.data;

import java.util.Date;

import org.apache.fineract.portfolio.loanaccount.domain.Loan;

public class LoanCollectibilityData {

	private Long id;
	private Long loanId;
	private Long clientId;
	private Long criteriaId;
	private Long maturedCriteriaId;
	private Long collectibilityAccount;
	private Long collectibilityCif;
	private Long manualCollectibility;
	private Long numberOverduesDayAccount;
	private Long numberOverduesDayCif;
	private Date submittedOn;
	private Long submittedBy;
	private Date updatedOn;
	private Long updatedBy;
	private Date deletedOn;
	private Long deletedBy;
	private Boolean isDeleted;
	private Date lossDate;
	private Boolean isMatured;

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

	public Date getSubmittedOn() {
		return submittedOn;
	}

	public void setSubmittedOn(Date submittedOn) {
		this.submittedOn = submittedOn;
	}

	public Long getSubmittedBy() {
		return submittedBy;
	}

	public void setSubmittedBy(Long submittedBy) {
		this.submittedBy = submittedBy;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public Long getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getDeletedOn() {
		return deletedOn;
	}

	public void setDeletedOn(Date deletedOn) {
		this.deletedOn = deletedOn;
	}

	public Long getDeletedBy() {
		return deletedBy;
	}

	public void setDeletedBy(Long deletedBy) {
		this.deletedBy = deletedBy;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Loan getLoan() {
		return loan;
	}

	public void setLoan(Loan loan) {
		this.loan = loan;
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

	public Long getNumberOverduesDayAccount() {
		return numberOverduesDayAccount;
	}

	public void setNumberOverduesDayAccount(Long numberOverduesDayAccount) {
		this.numberOverduesDayAccount = numberOverduesDayAccount;
	}

	public Long getNumberOverduesDayCif() {
		return numberOverduesDayCif;
	}

	public void setNumberOverduesDayCif(Long numberOverduesDayCif) {
		this.numberOverduesDayCif = numberOverduesDayCif;
	}

	public Date getLossDate() {
		return lossDate;
	}

	public void setLossDate(Date lossDate) {
		this.lossDate = lossDate;
	}

	/**
	 * @return the maturedCriteriaId
	 */
	public Long getMaturedCriteriaId() {
		return maturedCriteriaId;
	}

	/**
	 * @param maturedCriteriaId the maturedCriteriaId to set
	 */
	public void setMaturedCriteriaId(Long maturedCriteriaId) {
		this.maturedCriteriaId = maturedCriteriaId;
	}

	/**
	 * @return the clientId
	 */
	public Long getClientId() {
		return clientId;
	}

	/**
	 * @param clientId the clientId to set
	 */
	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	/**
	 * @return the isMatured
	 */
	public Boolean getIsMatured() {
		return isMatured;
	}

	/**
	 * @param isMatured the isMatured to set
	 */
	public void setIsMatured(Boolean isMatured) {
		this.isMatured = isMatured;
	}
}
