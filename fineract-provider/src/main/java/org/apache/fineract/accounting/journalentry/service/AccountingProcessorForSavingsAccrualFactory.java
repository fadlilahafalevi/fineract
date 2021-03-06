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

import org.apache.fineract.accounting.journalentry.data.SavingsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class AccountingProcessorForSavingsAccrualFactory {

    private final ApplicationContext applicationContext;

    @Autowired
    public AccountingProcessorForSavingsAccrualFactory(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /***
     * Looks like overkill for now, but wanted to keep the savings side of
     * accounting identical to that of Loans (would we need an Accrual based
     * accounting in the future?)
     ***/
    public AccountingProcessorForSavingsAccrual determineProcessor(final SavingsDTO savingsDTO) {

    	AccountingProcessorForSavingsAccrual accountingProcessorForSavingsAccrual = null;

        if (savingsDTO.isCashBasedAccountingEnabled()) {
            accountingProcessorForSavingsAccrual = this.applicationContext.getBean("cashBasedAccountingProcessorForSavingsAccrual",
            		AccountingProcessorForSavingsAccrual.class);
        }

        return accountingProcessorForSavingsAccrual;
    }

}
