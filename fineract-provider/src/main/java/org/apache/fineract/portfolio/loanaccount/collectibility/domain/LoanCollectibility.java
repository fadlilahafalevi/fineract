package org.apache.fineract.portfolio.loanaccount.collectibility.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "m_loan_collectibility")
public class LoanCollectibility implements Persistable<Long> {
	private static final long serialVersionUID = 2826815465282944395L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "m_loan_collectibility_id_seq")
	@SequenceGenerator(name = "m_loan_collectibility_id_seq", sequenceName = "m_loan_collectibility_id_seq", allocationSize = 1)
	
	@Column(name = "id")
	private Long id;
	
	@Column(name = "loan_id", nullable = false)
	private Long loanId;
	
	@Column(name = "collectibility_account")
	private Long collectibilityAccount;
	
	@Column(name = "collectibility_cif")
	private Long collectibilityCif;
	
	@Column(name = "manual_collectibility")
	private Long manualCollectibility;
	
	@Column(name = "number_overdues_day_account")
	private Long numberOverduesDayAccount;
	
	@Column(name = "number_overdues_day_cif")
	private Long numberOverduesDayCif;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "submitted_on")
	private Date submittedOn;
	
	@Column(name = "submitted_by")
	private Long submittedById;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_on")
	private Date updatedOn;
	
	@Column(name = "updated_by")
	private Long updatedById;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "delete_on")
	private Date deletedOn;
	
	@Column(name = "delete_by")
	private Long deletedById;
	
	@Column(name = "is_deleted")
	private Boolean isDeleted;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "loss_date")
	private Date lossDate;
	
	/* constructors */
	public LoanCollectibility(){}
	
	/* getters and setters */
	
	public boolean isStandartCollect() {
		Long collect = 0L;
		if (this.manualCollectibility != null){
			collect = this.manualCollectibility;
		} else if (this.collectibilityAccount != null){
			collect = this.collectibilityAccount;
		}
        return collect == 1;
    }
	
	public boolean isSpecialMentionCollect() {
		Long collect = 0L;
		if (this.manualCollectibility != null){
			collect = this.manualCollectibility;
		} else if (this.collectibilityAccount != null){
			collect = this.collectibilityAccount;
		}
        return collect == 2;
    }
	
	public boolean isSubStandartCollectAccount() {
		Long collect = 0L;
		if (this.manualCollectibility != null){
			collect = this.manualCollectibility;
		} else if (this.collectibilityAccount != null){
			collect = this.collectibilityAccount;
		}
        return collect == 3;
	}

	public boolean isDoubtfullCollectAccount() {
		Long collect = 0L;
		if (this.manualCollectibility != null){
			collect = this.manualCollectibility;
		} else if (this.collectibilityAccount != null){
			collect = this.collectibilityAccount;
		}
        return collect == 4;
	}

	public boolean isLossCollectAccount() {
		Long collect = 0L;
		if (this.manualCollectibility != null){
			collect = this.manualCollectibility;
		} else if (this.collectibilityAccount != null){
			collect = this.collectibilityAccount;
		}
        return collect == 5;
	}
	
	@Override
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Override
	public boolean isNew() {
		return null == getId();
	}

	public Long getLoanId() {
		return loanId;
	}

	public void setLoanId(Long loanId) {
		this.loanId = loanId;
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

	public Date getSubmittedOn() {
		return submittedOn;
	}

	public void setSubmittedOn(Date submittedOn) {
		this.submittedOn = submittedOn;
	}

	public Long getSubmittedById() {
		return submittedById;
	}

	public void setSubmittedById(Long submittedById) {
		this.submittedById = submittedById;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public Long getUpdatedById() {
		return updatedById;
	}

	public void setUpdatedById(Long updatedById) {
		this.updatedById = updatedById;
	}

	public Date getDeletedOn() {
		return deletedOn;
	}

	public void setDeletedOn(Date deletedOn) {
		this.deletedOn = deletedOn;
	}

	public Long getDeletedById() {
		return deletedById;
	}

	public void setDeletedById(Long deletedById) {
		this.deletedById = deletedById;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
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
}

