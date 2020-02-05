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
package org.apache.fineract.portfolio.loanaccount.domain;

public enum LoanAccrualTransactionType {

    INVALID(0, "loanAccrualTransactionType.invalid"), //
    ACCRUAL(1, "loanAccrualTransactionType.accrual"), //
    ACCRUAL_ASSET(2, "loanAccrualTransactionType.accrualAsset"), //
    ACCRUAL_ADMINSTRATIVE(3, "loanAccrualTransactionType.accrualAdministrative"); //

    private final Integer value;
    private final String code;

    private LoanAccrualTransactionType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static LoanAccrualTransactionType fromInt(final Integer transactionType) {

        if (transactionType == null) { return LoanAccrualTransactionType.INVALID; }

        LoanAccrualTransactionType loanTransactionType = null;
        switch (transactionType) {
            case 1:
                loanTransactionType = LoanAccrualTransactionType.ACCRUAL;
            break;
            case 2:
                loanTransactionType = LoanAccrualTransactionType.ACCRUAL_ASSET;
            break;
            case 3:
                loanTransactionType = LoanAccrualTransactionType.ACCRUAL_ADMINSTRATIVE;
            break;
            default:
                loanTransactionType = LoanAccrualTransactionType.INVALID;
            break;
        }
        return loanTransactionType;
    }
}