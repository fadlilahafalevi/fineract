/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.accounting.provisioning.domain;

import java.math.BigDecimal;

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

import org.apache.fineract.accounting.glaccount.domain.GLAccount;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.organisation.provisioning.domain.ProvisioningCategory;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "m_loanaccount_provisioning_entry")
public class LoanAccountProvisioningEntry implements Persistable<Long> {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "history_id", referencedColumnName = "id", nullable = false)
    private ProvisioningEntry entry;

    @Column(name = "criteria_id", nullable = false)
    private Long criteriaId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "office_id", nullable = false)
    private Office office;

    @Column(name = "currency_code", length = 3)
    private String currencyCode;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ProvisioningCategory provisioningCategory;
    
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id_by_cif", nullable = false)
    private ProvisioningCategory provisioningCategoryByCif;

    @Column(name = "overdue_in_days", nullable = false)
    private Long overdueInDays;

    @Column(name = "reserved_amount", nullable = false)
    private BigDecimal reservedAmount;
    
    @Column(name = "old_reserved_amount", nullable = true)
    private BigDecimal oldReservedAmount;
    
    @Column(name = "reserved_amount_by_cif", nullable = false)
    private BigDecimal reservedAmountByCif;
    
    @Column(name = "old_reserved_amount_by_cif", nullable = true)
    private BigDecimal oldReservedAmountByCif;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_account", nullable = false)
    private GLAccount assetAccount;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_account", nullable = false)
    private GLAccount expenseAccount;
    
    @Column(name = "provision_deduct_amount")
    private BigDecimal provisionDeductAmount;
    
    protected LoanAccountProvisioningEntry() {
        
    }
    public LoanAccountProvisioningEntry(final Loan loan, final Office office, final String currencyCode,
            final ProvisioningCategory provisioningCategory, final Long overdueInDays, final BigDecimal reservedAmount,
            final GLAccount assetAccount, final GLAccount expenseAccount, Long criteriaId, final ProvisioningCategory provisioningCategoryByCif,
            final BigDecimal reservedAmountByCif) {
        this.loan = loan;
        this.office = office;
        this.currencyCode = currencyCode;
        this.provisioningCategory = provisioningCategory;
        this.overdueInDays = overdueInDays;
        this.reservedAmount = reservedAmount;
        this.assetAccount = assetAccount;
        this.expenseAccount = expenseAccount;
        this.criteriaId = criteriaId ;
        this.provisioningCategoryByCif = provisioningCategoryByCif;
        this.reservedAmountByCif = reservedAmountByCif;
    }

    public void setProvisioningEntry(ProvisioningEntry provisioningEntry) {
        this.entry = provisioningEntry;
    }

    public BigDecimal getReservedAmount() {
        return this.reservedAmount ;
    }
    public void addReservedAmount(BigDecimal value) {
        this.reservedAmount = this.reservedAmount.add(value) ;
    }
    
    public Office getOffice() {
        return this.office ;
    }

    public GLAccount getAssetAccount() {
        return this.assetAccount ;
    }
    
    public String getCurrencyCode() {
        return this.currencyCode ;
    }
    
    public GLAccount getExpenseAccount() {
        return this.expenseAccount ;
    }
    
    public BigDecimal getOldReservedAmount() {
		return oldReservedAmount;
	}
	public void setOldReservedAmount(BigDecimal oldReservedAmount) {
		this.oldReservedAmount = oldReservedAmount;
	}
	public BigDecimal getReservedAmountByCif() {
		return reservedAmountByCif;
	}
	public void setReservedAmountByCif(BigDecimal reservedAmountByCif) {
		this.reservedAmountByCif = reservedAmountByCif;
	}
	public BigDecimal getOldReservedAmountByCif() {
		return oldReservedAmountByCif;
	}
	public void setOldReservedAmountByCif(BigDecimal oldReservedAmountByCif) {
		this.oldReservedAmountByCif = oldReservedAmountByCif;
	}
	public Loan getLoan() {
		return loan;
	}
	public void setLoan(Loan loan) {
		this.loan = loan;
	}
	
	@Override
    public boolean equals(Object obj) {
        if (!obj.getClass().equals(getClass())) return false;
        LoanAccountProvisioningEntry entry = (LoanAccountProvisioningEntry) obj;
        return entry.loan.getId().equals(this.loan.getId())
                && entry.provisioningCategory.getId().equals(this.provisioningCategory.getId())
                && entry.office.getId().equals(this.office.getId())
                && entry.getCurrencyCode().equals(this.getCurrencyCode());
    }


	private static final long serialVersionUID = -5554308939380869754L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator="m_loanaccount_provisioning_entry_id_seq")
	@SequenceGenerator(name="m_loanaccount_provisioning_entry_id_seq", sequenceName="m_loanproduct_provisioning_entry_id_seq", allocationSize=1)
	@Column(name = "id")
	private Long id;

//<--------------------------------- bagian isi dari abstractPersistable<PK> ------------------------------------------>
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.domain.Persistable#getId()
	 */
	@Override
	public Long getId() {

		return id;
	}

	/**
	 * Sets the id of the entity.
	 * 
	 * @param id the id to set
	 */
	protected void setId(final Long id) {

		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.domain.Persistable#isNew()
	 */
	@Override
	public boolean isNew() {

		return null == getId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return String.format("Entity of type %s with id: %s", this.getClass().getName(), getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	
	/*@Override
	public boolean equals(Object obj) {

		if (null == obj) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!getClass().equals(obj.getClass())) {
			return false;
		}

		Persistable<?> that = (Persistable<?>) obj;

		return null == this.getId() ? false : this.getId().equals(that.getId());
	}*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		int hashCode = 17;

		hashCode += null == getId() ? 0 : getId().hashCode() * 31;

		return hashCode;
	}
	public BigDecimal getProvisionDeductAmount() {
		return provisionDeductAmount;
	}
	public void setProvisionDeductAmount(BigDecimal provisionDeductAmount) {
		this.provisionDeductAmount = provisionDeductAmount;
	}
}
