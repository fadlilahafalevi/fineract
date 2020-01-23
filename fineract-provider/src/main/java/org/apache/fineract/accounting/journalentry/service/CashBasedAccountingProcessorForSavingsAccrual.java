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
package org.apache.fineract.accounting.journalentry.service;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.fineract.accounting.closure.domain.GLClosure;
import org.apache.fineract.accounting.common.AccountingConstants.CASH_ACCOUNTS_FOR_SAVINGS;
import org.apache.fineract.accounting.journalentry.data.SavingsDTO;
import org.apache.fineract.organisation.office.domain.Office;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CashBasedAccountingProcessorForSavingsAccrual implements AccountingProcessorForSavingsAccrual {

    private final AccountingProcessorHelper helper;

    @Autowired
    public CashBasedAccountingProcessorForSavingsAccrual(final AccountingProcessorHelper helper) {
        this.helper = helper;
    }

    @SuppressWarnings("deprecation")
	@Override
    public void createJournalEntriesForSavingsAccrual(SavingsDTO savingsDTO) {
        final GLClosure latestGLClosure = this.helper.getLatestClosureByBranch(savingsDTO.getOfficeId());
        final String currencyCode = savingsDTO.getCurrencyCode();
        final Date transactionDate = savingsDTO.getAccrualDate().toDate();
        final Office office = this.helper.getOfficeById(savingsDTO.getOfficeId());
        final BigDecimal amount = savingsDTO.getInterestAccrued();
        final Long savingsId = savingsDTO.getSavingsId();
        final Long savingsProductId = savingsDTO.getSavingsProductId();
        final Boolean isReversal = false;
        final Long paymentTypeId = null;
        Integer year = transactionDate.getYear() + 1900;
        Integer month = transactionDate.getMonth() + 1;
		String transactionId = "S" + office.getId() + savingsProductId + savingsId + year + month
				+ transactionDate.getDate();
        
        this.helper.checkForBranchClosures(latestGLClosure, transactionDate);
        
        this.helper.createCashBasedJournalEntriesAndReversalsForSavings(office, currencyCode,
                CASH_ACCOUNTS_FOR_SAVINGS.INTEREST_ON_SAVINGS.getValue(), CASH_ACCOUNTS_FOR_SAVINGS.SAVINGS_ACCRUAL.getValue(),
                savingsProductId, paymentTypeId, savingsId, transactionId, transactionDate, amount, isReversal);

    }

}
