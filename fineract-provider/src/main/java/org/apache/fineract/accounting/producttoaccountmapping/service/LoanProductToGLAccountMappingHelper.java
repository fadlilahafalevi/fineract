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
package org.apache.fineract.accounting.producttoaccountmapping.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.fineract.accounting.common.AccountingRuleType;
import org.apache.fineract.accounting.common.AccountingConstants.ACCRUAL_ACCOUNTS_FOR_LOAN;
import org.apache.fineract.accounting.common.AccountingConstants.CASH_ACCOUNTS_FOR_LOAN;
import org.apache.fineract.accounting.common.AccountingConstants.LOAN_PRODUCT_ACCOUNTING_PARAMS;
import org.apache.fineract.accounting.glaccount.domain.GLAccountRepository;
import org.apache.fineract.accounting.glaccount.domain.GLAccountRepositoryWrapper;
import org.apache.fineract.accounting.glaccount.domain.GLAccountType;
import org.apache.fineract.accounting.producttoaccountmapping.domain.PortfolioProductType;
import org.apache.fineract.accounting.producttoaccountmapping.domain.ProductToGLAccountMappingRepository;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.charge.domain.ChargeRepositoryWrapper;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentTypeRepositoryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;

@Component
public class LoanProductToGLAccountMappingHelper extends ProductToGLAccountMappingHelper {

    @Autowired
    public LoanProductToGLAccountMappingHelper(final GLAccountRepository glAccountRepository,
            final ProductToGLAccountMappingRepository glAccountMappingRepository, final FromJsonHelper fromApiJsonHelper,
            final ChargeRepositoryWrapper chargeRepositoryWrapper, final GLAccountRepositoryWrapper accountRepositoryWrapper,
            final PaymentTypeRepositoryWrapper paymentTypeRepositoryWrapper) {
        super(glAccountRepository, glAccountMappingRepository, fromApiJsonHelper, chargeRepositoryWrapper, accountRepositoryWrapper,
                paymentTypeRepositoryWrapper);
    }

    /*** Set of abstractions for saving Loan Products to GL Account Mappings ***/

    public void saveLoanToAssetAccountMapping(final JsonElement element, final String paramName, final Long productId,
            final int placeHolderTypeId) {
        saveProductToAccountMapping(element, paramName, productId, placeHolderTypeId, GLAccountType.ASSET, PortfolioProductType.LOAN);
    }

    public void saveLoanToIncomeAccountMapping(final JsonElement element, final String paramName, final Long productId,
            final int placeHolderTypeId) {
        saveProductToAccountMapping(element, paramName, productId, placeHolderTypeId, GLAccountType.INCOME, PortfolioProductType.LOAN);
    }

    public void saveLoanToExpenseAccountMapping(final JsonElement element, final String paramName, final Long productId,
            final int placeHolderTypeId) {
        saveProductToAccountMapping(element, paramName, productId, placeHolderTypeId, GLAccountType.EXPENSE, PortfolioProductType.LOAN);
    }

    public void saveLoanToLiabilityAccountMapping(final JsonElement element, final String paramName, final Long productId,
            final int placeHolderTypeId) {
        saveProductToAccountMapping(element, paramName, productId, placeHolderTypeId, GLAccountType.LIABILITY, PortfolioProductType.LOAN);
    }
    
    public void saveLoanToOffBalanceSheetClaimAccountMapping(final JsonElement element, final String paramName, final Long productId,
            final int placeHolderTypeId) {
        saveProductToAccountMapping(element, paramName, productId, placeHolderTypeId, GLAccountType.OFF_BALANCE_SHEET_CLAIM, PortfolioProductType.LOAN);
    }
    
    public void saveLoanToOffBalanceSheetLiabilityAccountMapping(final JsonElement element, final String paramName, final Long productId,
            final int placeHolderTypeId) {
        saveProductToAccountMapping(element, paramName, productId, placeHolderTypeId, GLAccountType.OFF_BALANCE_SHEET_LIABILITY, PortfolioProductType.LOAN);
    }

    /*** Set of abstractions for merging Savings Products to GL Account Mappings ***/
    public void mergeLoanToAssetAccountMappingChanges(final JsonElement element, final String paramName, final Long productId,
            final int accountTypeId, final String accountTypeName, final Map<String, Object> changes) {
        mergeProductToAccountMappingChanges(element, paramName, productId, accountTypeId, accountTypeName, changes, GLAccountType.ASSET,
                PortfolioProductType.LOAN);
    }

    public void mergeLoanToIncomeAccountMappingChanges(final JsonElement element, final String paramName, final Long productId,
            final int accountTypeId, final String accountTypeName, final Map<String, Object> changes) {
        mergeProductToAccountMappingChanges(element, paramName, productId, accountTypeId, accountTypeName, changes, GLAccountType.INCOME,
                PortfolioProductType.LOAN);
    }

    public void mergeLoanToExpenseAccountMappingChanges(final JsonElement element, final String paramName, final Long productId,
            final int accountTypeId, final String accountTypeName, final Map<String, Object> changes) {
        mergeProductToAccountMappingChanges(element, paramName, productId, accountTypeId, accountTypeName, changes, GLAccountType.EXPENSE,
                PortfolioProductType.LOAN);
    }

    public void mergeLoanToLiabilityAccountMappingChanges(final JsonElement element, final String paramName, final Long productId,
            final int accountTypeId, final String accountTypeName, final Map<String, Object> changes) {
        mergeProductToAccountMappingChanges(element, paramName, productId, accountTypeId, accountTypeName, changes,
                GLAccountType.LIABILITY, PortfolioProductType.LOAN);
    }
    
    public void mergeLoanToOffBalanceSheetClaimAccountMappingChanges(final JsonElement element, final String paramName, final Long productId,
            final int accountTypeId, final String accountTypeName, final Map<String, Object> changes) {
        mergeProductToAccountMappingChanges(element, paramName, productId, accountTypeId, accountTypeName, changes,
                GLAccountType.OFF_BALANCE_SHEET_CLAIM, PortfolioProductType.LOAN);
    }
    
    public void mergeLoanToOffBalanceSheetLiabilityAccountMappingChanges(final JsonElement element, final String paramName, final Long productId,
            final int accountTypeId, final String accountTypeName, final Map<String, Object> changes) {
        mergeProductToAccountMappingChanges(element, paramName, productId, accountTypeId, accountTypeName, changes,
                GLAccountType.OFF_BALANCE_SHEET_LIABILITY, PortfolioProductType.LOAN);
    }

    /*** Abstractions for payments channel related to loan products ***/

    public void savePaymentChannelToFundSourceMappings(final JsonCommand command, final JsonElement element, final Long productId,
            final Map<String, Object> changes) {
        savePaymentChannelToFundSourceMappings(command, element, productId, changes, PortfolioProductType.LOAN);
    }

    public void updatePaymentChannelToFundSourceMappings(final JsonCommand command, final JsonElement element, final Long productId,
            final Map<String, Object> changes) {
        updatePaymentChannelToFundSourceMappings(command, element, productId, changes, PortfolioProductType.LOAN);
    }

    public void saveChargesToIncomeAccountMappings(final JsonCommand command, final JsonElement element, final Long productId,
            final Map<String, Object> changes) {
        // save both fee and penalty charges
        saveChargesToIncomeOrLiabilityAccountMappings(command, element, productId, changes, PortfolioProductType.LOAN, true);
        saveChargesToIncomeOrLiabilityAccountMappings(command, element, productId, changes, PortfolioProductType.LOAN, false);
    }

    public void updateChargesToIncomeAccountMappings(final JsonCommand command, final JsonElement element, final Long productId,
            final Map<String, Object> changes) {
        // update both fee and penalty charges
        updateChargeToIncomeAccountMappings(command, element, productId, changes, PortfolioProductType.LOAN, true);
        updateChargeToIncomeAccountMappings(command, element, productId, changes, PortfolioProductType.LOAN, false);
    }

    public Map<String, Object> populateChangesForNewLoanProductToGLAccountMappingCreation(final JsonElement element,
            final AccountingRuleType accountingRuleType) {
        final Map<String, Object> changes = new HashMap<>();

        final Long fundAccountId = this.fromApiJsonHelper.extractLongNamed(LOAN_PRODUCT_ACCOUNTING_PARAMS.FUND_SOURCE.getValue(), element);
        final Long loanPortfolioAccountId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.LOAN_PORTFOLIO.getValue(), element);
        final Long incomeFromInterestId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_ON_LOANS.getValue(), element);
        final Long incomeFromFeeId = this.fromApiJsonHelper.extractLongNamed(LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_FEES.getValue(),
                element);
        final Long incomeFromPenaltyId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_PENALTIES.getValue(), element);

        final Long incomeFromRecoveryAccountId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_RECOVERY.getValue(), element);

        final Long writeOffAccountId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.LOSSES_WRITTEN_OFF.getValue(), element);
        final Long overPaymentAccountId = this.fromApiJsonHelper.extractLongNamed(LOAN_PRODUCT_ACCOUNTING_PARAMS.OVERPAYMENT.getValue(),
                element);
        final Long transfersInSuspenseAccountId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.TRANSFERS_SUSPENSE.getValue(), element);

        final Long receivableInterestAccountId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_RECEIVABLE.getValue(), element);
        final Long receivableFeeAccountId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.FEES_RECEIVABLE.getValue(), element);
        final Long receivablePenaltyAccountId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.PENALTIES_RECEIVABLE.getValue(), element);
        
        final Long accruedAssetInterestAccountId = this.fromApiJsonHelper.extractLongNamed(
        		LOAN_PRODUCT_ACCOUNTING_PARAMS.ACCRUED_INTEREST_ASSET.getValue(), element);
        final Long accruedAdministrativeInterestClaimAccountId = this.fromApiJsonHelper.extractLongNamed(
        		LOAN_PRODUCT_ACCOUNTING_PARAMS.ACCRUED_INTEREST_ADMINISTRATIVE_CLAIM.getValue(), element);
        final Long accruedAdministrativeInterestLiabilityAccountId = this.fromApiJsonHelper.extractLongNamed(
        		LOAN_PRODUCT_ACCOUNTING_PARAMS.ACCRUED_INTEREST_ADMINISTRATIVE_LIABILITY.getValue(), element);
        
        final Long accruedReverseAccountId = this.fromApiJsonHelper.extractLongNamed(
        		LOAN_PRODUCT_ACCOUNTING_PARAMS.ACCRUED_REVERSE.getValue(), element); 

        switch (accountingRuleType) {
            case NONE:
            break;
            case CASH_BASED:
                populateChangesForCashBasedAccounting(changes, fundAccountId, loanPortfolioAccountId, incomeFromInterestId,
                        incomeFromFeeId, incomeFromPenaltyId, writeOffAccountId, overPaymentAccountId, transfersInSuspenseAccountId,
                        incomeFromRecoveryAccountId);
            break;
            case ACCRUAL_PERIODIC:
                populateChangesForAccrualBasedAccounting(changes, fundAccountId, loanPortfolioAccountId, incomeFromInterestId,
                        incomeFromFeeId, incomeFromPenaltyId, writeOffAccountId, overPaymentAccountId, transfersInSuspenseAccountId,
                        incomeFromRecoveryAccountId, receivableInterestAccountId, receivableFeeAccountId, receivablePenaltyAccountId, 
                        accruedAssetInterestAccountId, accruedAdministrativeInterestClaimAccountId, accruedAdministrativeInterestLiabilityAccountId,
                        accruedReverseAccountId);
            break;
            case ACCRUAL_UPFRONT:
                populateChangesForAccrualBasedAccounting(changes, fundAccountId, loanPortfolioAccountId, incomeFromInterestId,
                        incomeFromFeeId, incomeFromPenaltyId, writeOffAccountId, overPaymentAccountId, transfersInSuspenseAccountId,
                        incomeFromRecoveryAccountId, receivableInterestAccountId, receivableFeeAccountId, receivablePenaltyAccountId, 
                        accruedAssetInterestAccountId, accruedAdministrativeInterestClaimAccountId, accruedAdministrativeInterestLiabilityAccountId,
                        accruedReverseAccountId);
            break;
        }

        return changes;
    }

    private void populateChangesForAccrualBasedAccounting(final Map<String, Object> changes, final Long fundAccountId,
            final Long loanPortfolioAccountId, final Long incomeFromInterestId, final Long incomeFromFeeId, final Long incomeFromPenaltyId,
            final Long writeOffAccountId, final Long overPaymentAccountId, final Long transfersInSuspenseAccountId,
            final Long incomeFromRecoveryAccountId, final Long receivableInterestAccountId, final Long receivableFeeAccountId,
            final Long receivablePenaltyAccountId, Long accruedAssetInterestId, Long accruedAdministrativeInterestClaimId, 
            Long accruedAdministrativeInterestLiabilityAccountId, Long accruedReverseId) {

        changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_RECEIVABLE.getValue(), receivableInterestAccountId);
        changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.FEES_RECEIVABLE.getValue(), receivableFeeAccountId);
        changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.PENALTIES_RECEIVABLE.getValue(), receivablePenaltyAccountId);
        changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.ACCRUED_INTEREST_ASSET.getValue(), accruedAssetInterestId);
        changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.ACCRUED_INTEREST_ADMINISTRATIVE_CLAIM.getValue(), accruedAdministrativeInterestClaimId);
        changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.ACCRUED_INTEREST_ADMINISTRATIVE_LIABILITY.getValue(), accruedAdministrativeInterestLiabilityAccountId);
        changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.ACCRUED_REVERSE.getValue(), accruedReverseId);

        populateChangesForCashBasedAccounting(changes, fundAccountId, loanPortfolioAccountId, incomeFromInterestId, incomeFromFeeId,
                incomeFromPenaltyId, writeOffAccountId, overPaymentAccountId, transfersInSuspenseAccountId, incomeFromRecoveryAccountId);

    }

    private void populateChangesForCashBasedAccounting(final Map<String, Object> changes, final Long fundAccountId,
            final Long loanPortfolioAccountId, final Long incomeFromInterestId, final Long incomeFromFeeId, final Long incomeFromPenaltyId,
            final Long writeOffAccountId, final Long overPaymentAccountId, final Long transfersInSuspenseAccountId,
            final Long incomeFromRecoveryAccountId) {
        changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.FUND_SOURCE.getValue(), fundAccountId);
        changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.LOAN_PORTFOLIO.getValue(), loanPortfolioAccountId);
        changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_ON_LOANS.getValue(), incomeFromInterestId);
        changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_FEES.getValue(), incomeFromFeeId);
        changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_PENALTIES.getValue(), incomeFromPenaltyId);
        changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.LOSSES_WRITTEN_OFF.getValue(), writeOffAccountId);
        changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.OVERPAYMENT.getValue(), overPaymentAccountId);
        changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.TRANSFERS_SUSPENSE.getValue(), transfersInSuspenseAccountId);
        changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_RECOVERY.getValue(), incomeFromRecoveryAccountId);

    }

    /**
     * Examines and updates each account mapping for given loan product with
     * changes passed in from the Json element
     * 
     * @param loanProductId
     * @param changes
     * @param element
     * @param accountingRuleType
     */
    public void handleChangesToLoanProductToGLAccountMappings(final Long loanProductId, final Map<String, Object> changes,
            final JsonElement element, final AccountingRuleType accountingRuleType) {
        switch (accountingRuleType) {
            case NONE:
            break;
            case CASH_BASED:
                // asset
                mergeLoanToAssetAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.FUND_SOURCE.getValue(), loanProductId,
                        CASH_ACCOUNTS_FOR_LOAN.FUND_SOURCE.getValue(), CASH_ACCOUNTS_FOR_LOAN.FUND_SOURCE.toString(), changes);
                mergeLoanToAssetAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.LOAN_PORTFOLIO.getValue(), loanProductId,
                        CASH_ACCOUNTS_FOR_LOAN.LOAN_PORTFOLIO.getValue(), CASH_ACCOUNTS_FOR_LOAN.LOAN_PORTFOLIO.toString(), changes);
                mergeLoanToAssetAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.TRANSFERS_SUSPENSE.getValue(), loanProductId,
                        CASH_ACCOUNTS_FOR_LOAN.TRANSFERS_SUSPENSE.getValue(), CASH_ACCOUNTS_FOR_LOAN.TRANSFERS_SUSPENSE.toString(), changes);

                // income
                mergeLoanToIncomeAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_ON_LOANS.getValue(), loanProductId,
                        CASH_ACCOUNTS_FOR_LOAN.INTEREST_ON_LOANS.getValue(), CASH_ACCOUNTS_FOR_LOAN.INTEREST_ON_LOANS.toString(), changes);
                mergeLoanToIncomeAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_FEES.getValue(), loanProductId,
                        CASH_ACCOUNTS_FOR_LOAN.INCOME_FROM_FEES.getValue(), CASH_ACCOUNTS_FOR_LOAN.INCOME_FROM_FEES.toString(), changes);
                mergeLoanToIncomeAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_PENALTIES.getValue(),
                        loanProductId, CASH_ACCOUNTS_FOR_LOAN.INCOME_FROM_PENALTIES.getValue(),
                        CASH_ACCOUNTS_FOR_LOAN.INCOME_FROM_PENALTIES.toString(), changes);
                mergeLoanToIncomeAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_RECOVERY.getValue(),
                        loanProductId, CASH_ACCOUNTS_FOR_LOAN.INCOME_FROM_RECOVERY.getValue(),
                        CASH_ACCOUNTS_FOR_LOAN.INCOME_FROM_RECOVERY.toString(), changes);

                // expenses
                mergeLoanToExpenseAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.LOSSES_WRITTEN_OFF.getValue(),
                        loanProductId, CASH_ACCOUNTS_FOR_LOAN.LOSSES_WRITTEN_OFF.getValue(),
                        CASH_ACCOUNTS_FOR_LOAN.LOSSES_WRITTEN_OFF.toString(), changes);

                // liabilities
                mergeLoanToLiabilityAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.OVERPAYMENT.getValue(), loanProductId,
                        CASH_ACCOUNTS_FOR_LOAN.OVERPAYMENT.getValue(), CASH_ACCOUNTS_FOR_LOAN.OVERPAYMENT.toString(), changes);
            break;
            case ACCRUAL_UPFRONT:
                // fall through to periodic accrual
            case ACCRUAL_PERIODIC:
                // assets (including receivables)
                mergeLoanToAssetAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.FUND_SOURCE.getValue(), loanProductId,
                        ACCRUAL_ACCOUNTS_FOR_LOAN.FUND_SOURCE.getValue(), ACCRUAL_ACCOUNTS_FOR_LOAN.FUND_SOURCE.toString(), changes);
                mergeLoanToAssetAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.LOAN_PORTFOLIO.getValue(), loanProductId,
                        ACCRUAL_ACCOUNTS_FOR_LOAN.LOAN_PORTFOLIO.getValue(), ACCRUAL_ACCOUNTS_FOR_LOAN.LOAN_PORTFOLIO.toString(), changes);
                mergeLoanToAssetAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.TRANSFERS_SUSPENSE.getValue(), loanProductId,
                        ACCRUAL_ACCOUNTS_FOR_LOAN.TRANSFERS_SUSPENSE.getValue(), ACCRUAL_ACCOUNTS_FOR_LOAN.TRANSFERS_SUSPENSE.toString(),
                        changes);
                mergeLoanToAssetAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_RECEIVABLE.getValue(),
                        loanProductId, ACCRUAL_ACCOUNTS_FOR_LOAN.INTEREST_RECEIVABLE.getValue(),
                        ACCRUAL_ACCOUNTS_FOR_LOAN.INTEREST_RECEIVABLE.toString(), changes);
                mergeLoanToAssetAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.FEES_RECEIVABLE.getValue(), loanProductId,
                        ACCRUAL_ACCOUNTS_FOR_LOAN.FEES_RECEIVABLE.getValue(), ACCRUAL_ACCOUNTS_FOR_LOAN.FEES_RECEIVABLE.toString(), changes);
                mergeLoanToAssetAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.PENALTIES_RECEIVABLE.getValue(),
                        loanProductId, ACCRUAL_ACCOUNTS_FOR_LOAN.PENALTIES_RECEIVABLE.getValue(),
                        ACCRUAL_ACCOUNTS_FOR_LOAN.PENALTIES_RECEIVABLE.toString(), changes);
                mergeLoanToAssetAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.ACCRUED_INTEREST_ASSET.getValue(),
                        loanProductId, ACCRUAL_ACCOUNTS_FOR_LOAN.ACCRUED_INTEREST_ASSET.getValue(),
                        ACCRUAL_ACCOUNTS_FOR_LOAN.ACCRUED_INTEREST_ASSET.toString(), changes);

                // income
                mergeLoanToIncomeAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_ON_LOANS.getValue(), loanProductId,
                        ACCRUAL_ACCOUNTS_FOR_LOAN.INTEREST_ON_LOANS.getValue(), ACCRUAL_ACCOUNTS_FOR_LOAN.INTEREST_ON_LOANS.toString(),
                        changes);
                mergeLoanToIncomeAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_FEES.getValue(), loanProductId,
                        ACCRUAL_ACCOUNTS_FOR_LOAN.INCOME_FROM_FEES.getValue(), ACCRUAL_ACCOUNTS_FOR_LOAN.INCOME_FROM_FEES.toString(),
                        changes);
                mergeLoanToIncomeAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_PENALTIES.getValue(),
                        loanProductId, ACCRUAL_ACCOUNTS_FOR_LOAN.INCOME_FROM_PENALTIES.getValue(),
                        ACCRUAL_ACCOUNTS_FOR_LOAN.INCOME_FROM_PENALTIES.toString(), changes);
                mergeLoanToIncomeAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_RECOVERY.getValue(),
                        loanProductId, ACCRUAL_ACCOUNTS_FOR_LOAN.INCOME_FROM_RECOVERY.getValue(),
                        ACCRUAL_ACCOUNTS_FOR_LOAN.INCOME_FROM_RECOVERY.toString(), changes);

                // expenses
                mergeLoanToExpenseAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.LOSSES_WRITTEN_OFF.getValue(),
                        loanProductId, ACCRUAL_ACCOUNTS_FOR_LOAN.LOSSES_WRITTEN_OFF.getValue(),
                        ACCRUAL_ACCOUNTS_FOR_LOAN.LOSSES_WRITTEN_OFF.toString(), changes);
                mergeLoanToExpenseAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.ACCRUED_REVERSE.getValue(),
                        loanProductId, ACCRUAL_ACCOUNTS_FOR_LOAN.ACCRUED_REVERSE.getValue(),
                        ACCRUAL_ACCOUNTS_FOR_LOAN.ACCRUED_REVERSE.toString(), changes);

                // liabilities
                mergeLoanToLiabilityAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.OVERPAYMENT.getValue(), loanProductId,
                        CASH_ACCOUNTS_FOR_LOAN.OVERPAYMENT.getValue(), CASH_ACCOUNTS_FOR_LOAN.OVERPAYMENT.toString(), changes);
                
                // admininstrative
                mergeLoanToOffBalanceSheetClaimAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.ACCRUED_INTEREST_ADMINISTRATIVE_CLAIM.getValue(), loanProductId,
                		ACCRUAL_ACCOUNTS_FOR_LOAN.ACCRUED_INTEREST_ADMINISTRATIVE_CLAIM.getValue(), ACCRUAL_ACCOUNTS_FOR_LOAN.ACCRUED_INTEREST_ADMINISTRATIVE_CLAIM.toString(), changes);
                
                mergeLoanToOffBalanceSheetLiabilityAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.ACCRUED_INTEREST_ADMINISTRATIVE_LIABILITY.getValue(), loanProductId,
                		ACCRUAL_ACCOUNTS_FOR_LOAN.ACCRUED_INTEREST_ADMINISTRATIVE_LIABILITY.getValue(), ACCRUAL_ACCOUNTS_FOR_LOAN.ACCRUED_INTEREST_ADMINISTRATIVE_LIABILITY.toString(), changes);
            break;
        }
    }

    public void deleteLoanProductToGLAccountMapping(final Long loanProductId) {
        deleteProductToGLAccountMapping(loanProductId, PortfolioProductType.LOAN);
    }

}
