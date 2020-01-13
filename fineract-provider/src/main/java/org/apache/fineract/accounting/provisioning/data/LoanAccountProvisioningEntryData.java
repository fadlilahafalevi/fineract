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
package org.apache.fineract.accounting.provisioning.data;

import java.math.BigDecimal;

public class LoanAccountProvisioningEntryData {

    private final Long historyId;

    private final Long officeId;

    private final String officeName ;
    
    private final String currencyCode;

    private final Long loanId;

    private final String productName ;
    
    private final Long categoryId;
    
    private final Long categoryIdByCif;

    private final String categoryName ;
    
    private final Long overdueInDays;

    private final BigDecimal percentage;
    
    private final BigDecimal percentageByCif;

    private final BigDecimal balance;

    private final BigDecimal amountReserved ;
    
    private final BigDecimal oldAmountReserved ;
    
    private final BigDecimal amountReservedByCif ;
    
    private final BigDecimal oldAmountReservedByCif ;
    
    private final Long assetAccount;

    private final String assetAccountCode ;
    
    private final String assetAccountName ;
    
    private final Long expenseAccount;

    private final String expenseAccountCode ;
    
    private final String expenseAccountName ;
    
    private final Long criteriaId ;
    
    private BigDecimal principal;
    
    private final String loanAccountNumber;
    
    public LoanAccountProvisioningEntryData(final Long historyId, final Long officeId, final String currencyCode, final Long loanId,
            final Long categoryId, final Long categoryIdByCif, final Long overdueInDays, final BigDecimal percentage, final BigDecimal percentageByCif, final BigDecimal balance, Long assetAccount,
            Long expenseAccount, final Long criteriaId, final String loanAccountNumber) {
        this.historyId = historyId;
        this.officeId = officeId;
        this.currencyCode = currencyCode;
        this.loanId = loanId;
        this.categoryId = categoryId;
        this.categoryIdByCif = categoryIdByCif;
        this.overdueInDays = overdueInDays;
        this.percentage = percentage;
        this.percentageByCif = percentageByCif;
        this.balance = balance;
        this.assetAccount = assetAccount;
        this.expenseAccount = expenseAccount;
        this.amountReserved = null ;
        this.officeName = null ;
        this.productName = null ;
        this.categoryName = null ;
        this.assetAccountCode = null ;
        this.assetAccountName = null ;
        this.expenseAccountCode = null ;
        this.expenseAccountName = null ;
        this.criteriaId = criteriaId ;
        this.amountReservedByCif = null;
        this.oldAmountReserved = null;
        this.oldAmountReservedByCif = null;
        this.loanAccountNumber = loanAccountNumber;
    }

    public LoanAccountProvisioningEntryData(final Long historyId, final Long officeId, final String officeName, final String currencyCode, final Long loanId,
            final String productName, final Long categoryId, final Long categoryIdByCif, final String categoryName, final Long overdueInDays, final BigDecimal amountReserved, final BigDecimal oldAmountReserved, final BigDecimal amountReservedByCif, final BigDecimal oldAmountReservedByCif, 
            Long assetAccount, String assetAccountglCode, String assetAccountName, Long expenseAccount, String expenseAccountglCode, String expenseAccountName, final Long criteriaId, final BigDecimal manualPercentage, final Long criteriaManual,
            final String loanAccountNumber) {
        this.historyId = historyId;
        this.officeId = officeId;
        this.currencyCode = currencyCode;
        this.loanId = loanId;
        this.categoryId = categoryId;
        this.categoryIdByCif = categoryIdByCif;
        this.categoryName = categoryName ;
        this.overdueInDays = overdueInDays;
        this.percentage = null;
        this.percentageByCif = null;
        this.balance = null;
        this.assetAccount = assetAccount;
        this.expenseAccount = expenseAccount;
        this.officeName = officeName ;
        this.productName = productName ;
        this.amountReserved = amountReserved ;
        this.assetAccountCode = assetAccountglCode ;
        this.assetAccountName = assetAccountName ;
        this.expenseAccountCode = expenseAccountglCode ;
        this.expenseAccountName = expenseAccountName ;
        this.criteriaId = criteriaId ;
        this.amountReservedByCif = amountReservedByCif;
        this.oldAmountReserved = oldAmountReserved;
        this.oldAmountReservedByCif = oldAmountReservedByCif;
        this.loanAccountNumber = loanAccountNumber;
    }
    public Long getHistoryId() {
        return this.historyId;
    }

    public Long getOfficeId() {
        return this.officeId;
    }

    public Long getLoanId() {
        return this.loanId;
    }

    public Long getCategoryId() {
        return this.categoryId;
    }

    public Long getOverdueInDays() {
        return this.overdueInDays;
    }

    public BigDecimal getOutstandingBalance() {
        return balance;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public Long getAssetAccount() {
        return this.assetAccount;
    }

    public Long getExpenseAccount() {
        return this.expenseAccount;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public Long getCriteriaId() {
        return this.criteriaId ;
    }

	public BigDecimal getOldAmountReserved() {
		return oldAmountReserved;
	}

	public BigDecimal getAmountReservedByCif() {
		return amountReservedByCif;
	}

	public BigDecimal getOldAmountReservedByCif() {
		return oldAmountReservedByCif;
	}

	public Long getCategoryIdByCif() {
		return categoryIdByCif;
	}

	public BigDecimal getPercentageByCif() {
		return percentageByCif;
	}
	public BigDecimal getPrincipal() {
		return principal;
	}

	public void setPrincipal(BigDecimal principal) {
		this.principal = principal;
	}
}
