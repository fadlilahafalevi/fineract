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
package org.apache.fineract.accounting.journalentry.data;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;

public class SavingsDTO {

    private Long savingsId;
    private Long savingsProductId;
    private Long officeId;
    private String currencyCode;
    private boolean cashBasedAccountingEnabled;
    private boolean accrualBasedAccountingEnabled;
    private List<SavingsTransactionDTO> newSavingsTransactions;
    private BigDecimal interestAccrued;
    private LocalDate accrualDate;

    public SavingsDTO(final Long savingsId, final Long savingsProductId, final Long officeId, final String currencyCode,
            final boolean cashBasedAccountingEnabled, final boolean accrualBasedAccountingEnabled,
            final List<SavingsTransactionDTO> newSavingsTransactions) {
        this.savingsId = savingsId;
        this.savingsProductId = savingsProductId;
        this.officeId = officeId;
        this.cashBasedAccountingEnabled = cashBasedAccountingEnabled;
        this.accrualBasedAccountingEnabled = accrualBasedAccountingEnabled;
        this.newSavingsTransactions = newSavingsTransactions;
        this.currencyCode = currencyCode;
    }

    public SavingsDTO() {
		super();
	}



	public Long getSavingsId() {
        return this.savingsId;
    }

    public void setSavingsId(final Long savingsId) {
        this.savingsId = savingsId;
    }

    public Long getSavingsProductId() {
        return this.savingsProductId;
    }

    public void setSavingsProductId(final Long savingsProductId) {
        this.savingsProductId = savingsProductId;
    }

    public Long getOfficeId() {
        return this.officeId;
    }

    public void setOfficeId(final Long officeId) {
        this.officeId = officeId;
    }

    public boolean isCashBasedAccountingEnabled() {
        return this.cashBasedAccountingEnabled;
    }

    public void setCashBasedAccountingEnabled(final boolean cashBasedAccountingEnabled) {
        this.cashBasedAccountingEnabled = cashBasedAccountingEnabled;
    }

    public boolean isAccrualBasedAccountingEnabled() {
        return this.accrualBasedAccountingEnabled;
    }

    public void setAccrualBasedAccountingEnabled(final boolean accrualBasedAccountingEnabled) {
        this.accrualBasedAccountingEnabled = accrualBasedAccountingEnabled;
    }

    public List<SavingsTransactionDTO> getNewSavingsTransactions() {
        return this.newSavingsTransactions;
    }

    public void setNewSavingsTransactions(final List<SavingsTransactionDTO> newSavingsTransactions) {
        this.newSavingsTransactions = newSavingsTransactions;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public void setCurrencyCode(final String currencyCode) {
        this.currencyCode = currencyCode;
    }

	public BigDecimal getInterestAccrued() {
		return interestAccrued;
	}

	public void setInterestAccrued(BigDecimal interestAccrued) {
		this.interestAccrued = interestAccrued;
	}

	public LocalDate getAccrualDate() {
		return accrualDate;
	}

	public void setAccrualDate(LocalDate accrualDate) {
		this.accrualDate = accrualDate;
	}

}
