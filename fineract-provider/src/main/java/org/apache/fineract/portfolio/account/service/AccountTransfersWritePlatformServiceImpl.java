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
package org.apache.fineract.portfolio.account.service;

import static org.apache.fineract.portfolio.account.AccountDetailConstants.fromAccountIdParamName;
import static org.apache.fineract.portfolio.account.AccountDetailConstants.fromAccountTypeParamName;
import static org.apache.fineract.portfolio.account.AccountDetailConstants.paymentTypeFromParamName;
import static org.apache.fineract.portfolio.account.AccountDetailConstants.paymentTypeToParamName;
import static org.apache.fineract.portfolio.account.AccountDetailConstants.toAccountIdParamName;
import static org.apache.fineract.portfolio.account.AccountDetailConstants.toAccountTypeParamName;
import static org.apache.fineract.portfolio.account.api.AccountTransfersApiConstants.transferAmountParamName;
import static org.apache.fineract.portfolio.account.api.AccountTransfersApiConstants.transferDateParamName;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.portfolio.account.AccountDetailConstants;
import org.apache.fineract.portfolio.account.PortfolioAccountType;
import org.apache.fineract.portfolio.account.data.AccountTransferDTO;
import org.apache.fineract.portfolio.account.data.AccountTransfersDataValidator;
import org.apache.fineract.portfolio.account.domain.AccountTransferAssembler;
import org.apache.fineract.portfolio.account.domain.AccountTransferDetailRepository;
import org.apache.fineract.portfolio.account.domain.AccountTransferDetails;
import org.apache.fineract.portfolio.account.domain.AccountTransferRepository;
import org.apache.fineract.portfolio.account.domain.AccountTransferTransaction;
import org.apache.fineract.portfolio.account.domain.AccountTransferType;
import org.apache.fineract.portfolio.loanaccount.data.HolidayDetailDTO;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanAccountDomainService;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionType;
import org.apache.fineract.portfolio.loanaccount.exception.InvalidPaidInAdvanceAmountException;
import org.apache.fineract.portfolio.loanaccount.service.LoanAssembler;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.paymentdetail.service.PaymentDetailWritePlatformService;
import org.apache.fineract.portfolio.savings.DepositAccountType;
import org.apache.fineract.portfolio.savings.SavingsTransactionBooleanValues;
import org.apache.fineract.portfolio.savings.domain.FixedDepositAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountAssembler;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountDomainService;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountRepositoryWrapper;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;
import org.apache.fineract.portfolio.savings.exception.MainSavingsAccountException;
import org.apache.fineract.portfolio.savings.exception.MainSavingsAccountNotMatchException;
import org.apache.fineract.portfolio.savings.exception.SavingsAccountBackdateException;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;
import org.apache.fineract.portfolio.savings.service.SavingsAccountWritePlatformService;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountTransfersWritePlatformServiceImpl implements AccountTransfersWritePlatformService {

    private final AccountTransfersDataValidator accountTransfersDataValidator;
    private final AccountTransferAssembler accountTransferAssembler;
    private final AccountTransferRepository accountTransferRepository;
    private final SavingsAccountAssembler savingsAccountAssembler;
    private final SavingsAccountDomainService savingsAccountDomainService;
    private final LoanAssembler loanAccountAssembler;
    private final LoanAccountDomainService loanAccountDomainService;
    private final SavingsAccountWritePlatformService savingsAccountWritePlatformService;
    private final AccountTransferDetailRepository accountTransferDetailRepository;
    private final LoanReadPlatformService loanReadPlatformService;
    private final SavingsAccountReadPlatformService savingsAccountReadPlatformService;
    private final SavingsAccountRepositoryWrapper savingsAccountRepositoryWrapper;
    private final PaymentDetailWritePlatformService paymentDetailWritePlatformService;

    @Autowired
    public AccountTransfersWritePlatformServiceImpl(final AccountTransfersDataValidator accountTransfersDataValidator,
            final AccountTransferAssembler accountTransferAssembler, final AccountTransferRepository accountTransferRepository,
            final SavingsAccountAssembler savingsAccountAssembler, final SavingsAccountDomainService savingsAccountDomainService,
            final LoanAssembler loanAssembler, final LoanAccountDomainService loanAccountDomainService,
            final SavingsAccountWritePlatformService savingsAccountWritePlatformService,
            final AccountTransferDetailRepository accountTransferDetailRepository,
            final LoanReadPlatformService loanReadPlatformService,
            final SavingsAccountReadPlatformService savingsAccountReadPlatformService,
            final SavingsAccountRepositoryWrapper savingsAccountRepositoryWrapper,
            final PaymentDetailWritePlatformService paymentDetailWritePlatformService) {
        this.accountTransfersDataValidator = accountTransfersDataValidator;
        this.accountTransferAssembler = accountTransferAssembler;
        this.accountTransferRepository = accountTransferRepository;
        this.savingsAccountAssembler = savingsAccountAssembler;
        this.savingsAccountDomainService = savingsAccountDomainService;
        this.loanAccountAssembler = loanAssembler;
        this.loanAccountDomainService = loanAccountDomainService;
        this.savingsAccountWritePlatformService = savingsAccountWritePlatformService;
        this.accountTransferDetailRepository = accountTransferDetailRepository;
        this.loanReadPlatformService = loanReadPlatformService;
        this.savingsAccountReadPlatformService = savingsAccountReadPlatformService;
        this.savingsAccountRepositoryWrapper = savingsAccountRepositoryWrapper;
        this.paymentDetailWritePlatformService = paymentDetailWritePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult create(final JsonCommand command) {
        boolean isRegularTransaction = true;

        this.accountTransfersDataValidator.validate(command);

        final LocalDate transactionDate = command.localDateValueOfParameterNamed(transferDateParamName);
        final BigDecimal transactionAmount = command.bigDecimalValueOfParameterNamed(transferAmountParamName);

        final Locale locale = command.extractLocale();
        final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);
        final LocalDate todayDate = LocalDate.now();
        final Integer fromAccountTypeId = command.integerValueSansLocaleOfParameterNamed(fromAccountTypeParamName);
        final PortfolioAccountType fromAccountType = PortfolioAccountType.fromInt(fromAccountTypeId);

        final Integer toAccountTypeId = command.integerValueSansLocaleOfParameterNamed(toAccountTypeParamName);
        final PortfolioAccountType toAccountType = PortfolioAccountType.fromInt(toAccountTypeId);
        
        final PaymentDetail paymentDetail = null;
        SavingsAccount fromSavingsAccount = null;
        Long transferDetailId = null;
        String fromSavingsAccountNumber = null;
        boolean isInterestTransfer = false;
        boolean isAccountTransfer = true;
        boolean isWithdrawBalance = false;
        Loan fromLoanAccount = null;
        if(transactionDate.equals(todayDate))
        {
        	if (isSavingsToSavingsAccountTransfer(fromAccountType, toAccountType)) {
                fromSavingsAccountNumber = command.stringValueOfParameterNamed(AccountDetailConstants.fromAccountNoParamName);
                fromSavingsAccount = this.savingsAccountAssembler.assembleFrom(fromSavingsAccountNumber);
            	final Boolean isFromSavingsMainAccount = this.savingsAccountReadPlatformService.isMainProduct(fromSavingsAccount.getId());

                final String toSavingsAccountNumber = command.stringValueOfParameterNamed(AccountDetailConstants.toAccountNoParamName);
                final SavingsAccount toSavingsAccount = this.savingsAccountAssembler.assembleFrom(toSavingsAccountNumber);
            	final Boolean isToSavingsMainAccount = this.savingsAccountReadPlatformService.isMainProduct(toSavingsAccount.getId());
            	final Integer savingsAccountType = toSavingsAccount.depositAccountType().getValue();

        		final String paymentTypeFromName = command.stringValueOfParameterNamed(paymentTypeFromParamName);
            	final String paymentTypeToName = command.stringValueOfParameterNamed(paymentTypeToParamName);
            	
            	PaymentDetail paymentDetailFrom = null;
            	PaymentDetail paymentDetailTo = null;
            	
            	if ((!paymentTypeFromName.isEmpty() && !paymentTypeToName.isEmpty()) && (isFromSavingsMainAccount && isToSavingsMainAccount)) {
            		final Map<String, Object> changes = new LinkedHashMap<>();
            		paymentDetailFrom = this.paymentDetailWritePlatformService.createAndPersistPaymentDetailByNameAndSavings(command, changes, paymentTypeFromName, toSavingsAccount);
            		paymentDetailTo = this.paymentDetailWritePlatformService.createAndPersistPaymentDetailByNameAndSavings(command, changes, paymentTypeToName, fromSavingsAccount);
            	} else if (!paymentTypeFromName.isEmpty() && !paymentTypeToName.isEmpty()) {
            		final Map<String, Object> changes = new LinkedHashMap<>();
            		paymentDetailFrom = this.paymentDetailWritePlatformService.createAndPersistPaymentDetailByName(command, changes, paymentTypeFromName);
            		paymentDetailTo = this.paymentDetailWritePlatformService.createAndPersistPaymentDetailByName(command, changes, paymentTypeToName);
            	}

            	Boolean processTransfer = false;
            	
				if ((isFromSavingsMainAccount && isToSavingsMainAccount) || (isFromSavingsMainAccount
						&& savingsAccountType.equals(DepositAccountType.FIXED_DEPOSIT.getValue()))) {
            		processTransfer = true;
            	} else if (isFromSavingsMainAccount && !isToSavingsMainAccount) {
            		final SavingsAccount mainAccountSavingsDestination = this.savingsAccountRepositoryWrapper.findMainAccountByClientId(toSavingsAccount.getClient().getId());
            		if (mainAccountSavingsDestination != null) {
	            		if (mainAccountSavingsDestination.getAccountNumber() == fromSavingsAccount.getAccountNumber()) {
	            			processTransfer = true;
	            		} else {
	            			//Jika Main dan Sub berbeda CIS, akan dicek payment typenya//
	            			if (paymentDetailFrom != null) {
	            				if (paymentDetailFrom.getPaymentType().getName().equals("OSSA") && paymentDetailTo.getPaymentType().getName().equals("ISSA")) {
	            					processTransfer = true;
	            				}
	            			}
	            		}
            		}
            	} else if (!isFromSavingsMainAccount && isToSavingsMainAccount) {
            		final SavingsAccount mainAccountSavingsSource = this.savingsAccountRepositoryWrapper.findMainAccountByClientId(fromSavingsAccount.getClient().getId());
            		if (mainAccountSavingsSource != null) {
	            		if (mainAccountSavingsSource.getAccountNumber() == toSavingsAccount.getAccountNumber()) {
	            			processTransfer = true;
	            		}
            		}
            	}
            	
            	//throw error when cant processing transfer
            	if (!processTransfer) {
            		throw new MainSavingsAccountNotMatchException();
            	}

                final SavingsTransactionBooleanValues transactionBooleanValues = new SavingsTransactionBooleanValues(isAccountTransfer,
                        isRegularTransaction, fromSavingsAccount.isWithdrawalFeeApplicableForTransfer(), isInterestTransfer, isWithdrawBalance);
                final SavingsAccountTransaction withdrawal = this.savingsAccountDomainService.handleWithdrawal(fromSavingsAccount, fmt,
                        transactionDate, transactionAmount, paymentDetailFrom, transactionBooleanValues);
                final SavingsAccountTransaction deposit = this.savingsAccountDomainService.handleDeposit(toSavingsAccount, fmt,
                        transactionDate, transactionAmount, paymentDetailTo, isAccountTransfer, isRegularTransaction);

                final AccountTransferDetails accountTransferDetails = this.accountTransferAssembler.assembleSavingsToSavingsTransfer(command,
                        fromSavingsAccount, toSavingsAccount, withdrawal, deposit);
                this.accountTransferDetailRepository.saveAndFlush(accountTransferDetails);
                transferDetailId = accountTransferDetails.getId();

            } else if (isSavingsToLoanAccountTransfer(fromAccountType, toAccountType)) {
                
            	fromSavingsAccountNumber = command.stringValueOfParameterNamed(AccountDetailConstants.fromAccountNoParamName);
                fromSavingsAccount = this.savingsAccountAssembler.assembleFrom(fromSavingsAccountNumber);
                final Boolean isMainAccount = this.savingsAccountReadPlatformService.isMainProduct(fromSavingsAccount.getId());
            	if (!isMainAccount) {
            		throw new MainSavingsAccountException(fromSavingsAccount.getAccountNumber());
            	}

                final SavingsTransactionBooleanValues transactionBooleanValues = new SavingsTransactionBooleanValues(isAccountTransfer,
                        isRegularTransaction, fromSavingsAccount.isWithdrawalFeeApplicableForTransfer(), isInterestTransfer, isWithdrawBalance);
                final SavingsAccountTransaction withdrawal = this.savingsAccountDomainService.handleWithdrawal(fromSavingsAccount, fmt,
                        transactionDate, transactionAmount, paymentDetail, transactionBooleanValues);

                final String toLoanAccountNumber = command.stringValueOfParameterNamed(AccountDetailConstants.toAccountNoParamName);
                final Loan toLoanAccount = this.loanAccountAssembler.assembleFrom(toLoanAccountNumber);

                final Boolean isHolidayValidationDone = false;
                final HolidayDetailDTO holidayDetailDto = null;
                final boolean isRecoveryRepayment = false;
                final LoanTransaction loanRepaymentTransaction = this.loanAccountDomainService.makeRepayment(toLoanAccount,
                        new CommandProcessingResultBuilder(), transactionDate, transactionAmount, paymentDetail, null, null,
                        isRecoveryRepayment, isAccountTransfer, holidayDetailDto, isHolidayValidationDone);

                final AccountTransferDetails accountTransferDetails = this.accountTransferAssembler.assembleSavingsToLoanTransfer(command,
                        fromSavingsAccount, toLoanAccount, withdrawal, loanRepaymentTransaction);
                this.accountTransferDetailRepository.saveAndFlush(accountTransferDetails);
                transferDetailId = accountTransferDetails.getId();

            } else if (isLoanToSavingsAccountTransfer(fromAccountType, toAccountType)) {
                // FIXME - kw - ADD overpaid loan to savings account transfer
                // support.

                String fromLoanAccountNumber = command.stringValueOfParameterNamed(AccountDetailConstants.fromAccountNoParamName);
                fromLoanAccount = this.loanAccountAssembler.assembleFrom(fromLoanAccountNumber);

                final LoanTransaction loanRefundTransaction = this.loanAccountDomainService.makeRefund(fromLoanAccount.getId(),
                        new CommandProcessingResultBuilder(), transactionDate, transactionAmount, paymentDetail, null, null);

                final Long toSavingsAccountId = command.longValueOfParameterNamed(toAccountIdParamName);
                final SavingsAccount toSavingsAccount = this.savingsAccountAssembler.assembleFrom(toSavingsAccountId);

                final SavingsAccountTransaction deposit = this.savingsAccountDomainService.handleDeposit(toSavingsAccount, fmt,
                        transactionDate, transactionAmount, paymentDetail, isAccountTransfer, isRegularTransaction);

                final AccountTransferDetails accountTransferDetails = this.accountTransferAssembler.assembleLoanToSavingsTransfer(command,
                        fromLoanAccount, toSavingsAccount, deposit, loanRefundTransaction);
                this.accountTransferDetailRepository.saveAndFlush(accountTransferDetails);
                transferDetailId = accountTransferDetails.getId();

            } else {

            }

            final CommandProcessingResultBuilder builder = new CommandProcessingResultBuilder().withEntityId(transferDetailId);

            if (fromAccountType.isSavingsAccount()) {
                builder.withSavingsId(fromSavingsAccount.getId());
            }
            if (fromAccountType.isLoanAccount()) {
                builder.withLoanId(fromLoanAccount.getId());
            }

            return builder.build();
        }
        else
        {
        	throw new SavingsAccountBackdateException(transactionDate);
        }
        
    }

    @Override
    @Transactional
    public void reverseTransfersWithFromAccountType(final Long accountNumber, final PortfolioAccountType accountTypeId) {
        List<AccountTransferTransaction> acccountTransfers = null;
        if (accountTypeId.isLoanAccount()) {
            acccountTransfers = this.accountTransferRepository.findByFromLoanId(accountNumber);
        }
        if (acccountTransfers != null && acccountTransfers.size() > 0) {
            undoTransactions(acccountTransfers);
        }

    }
    
    @Override
    @Transactional
    public void reverseTransfersWithFromAccountTransactions(final Collection<Long> fromTransactionIds, final PortfolioAccountType accountTypeId) {
        List<AccountTransferTransaction> acccountTransfers = null;
        if (accountTypeId.isLoanAccount()) {
            acccountTransfers = this.accountTransferRepository.findByFromLoanTransactions(fromTransactionIds);
        }
        if (acccountTransfers != null && acccountTransfers.size() > 0) {
            undoTransactions(acccountTransfers);
        }

    }

    @Override
    @Transactional
    public void reverseAllTransactions(final Long accountId, final PortfolioAccountType accountTypeId) {
        List<AccountTransferTransaction> acccountTransfers = null;
        if (accountTypeId.isLoanAccount()) {
            acccountTransfers = this.accountTransferRepository.findAllByLoanId(accountId);
        }
        if (acccountTransfers != null && acccountTransfers.size() > 0) {
            undoTransactions(acccountTransfers);
        }
    }

    /**
     * @param acccountTransfers
     */
    private void undoTransactions(final List<AccountTransferTransaction> acccountTransfers) {
        for (final AccountTransferTransaction accountTransfer : acccountTransfers) {
            if (accountTransfer.getFromLoanTransaction() != null) {
                this.loanAccountDomainService.reverseTransfer(accountTransfer.getFromLoanTransaction());
            }
            if (accountTransfer.getToLoanTransaction() != null) {
                this.loanAccountDomainService.reverseTransfer(accountTransfer.getToLoanTransaction());
            }
            if (accountTransfer.getFromTransaction() != null) {
                this.savingsAccountWritePlatformService.undoTransaction(accountTransfer.accountTransferDetails().fromSavingsAccount()
                        .getId(), accountTransfer.getFromTransaction().getId(), true);
            }
            if (accountTransfer.getToSavingsTransaction() != null) {
                this.savingsAccountWritePlatformService.undoTransaction(
                        accountTransfer.accountTransferDetails().toSavingsAccount().getId(), accountTransfer.getToSavingsTransaction()
                                .getId(), true);
            }
            accountTransfer.reverse();
            this.accountTransferRepository.save(accountTransfer);
        }
    }

    @Override
    @Transactional
    public Long transferFunds(final AccountTransferDTO accountTransferDTO) {
        Long transferTransactionId = null;
        final boolean isAccountTransfer = true;
        final boolean isRegularTransaction = accountTransferDTO.isRegularTransaction();
        AccountTransferDetails accountTransferDetails = accountTransferDTO.getAccountTransferDetails();
        if (isSavingsToLoanAccountTransfer(accountTransferDTO.getFromAccountType(), accountTransferDTO.getToAccountType())) {
            //
            SavingsAccount fromSavingsAccount = null;
            Loan toLoanAccount = null;
            if (accountTransferDetails == null) {
                if (accountTransferDTO.getFromSavingsAccount() == null) {
                    fromSavingsAccount = this.savingsAccountAssembler.assembleFrom(accountTransferDTO.getFromAccountId());
                } else {
                    fromSavingsAccount = accountTransferDTO.getFromSavingsAccount();
                    this.savingsAccountAssembler.setHelpers(fromSavingsAccount);
                }
                if (accountTransferDTO.getLoan() == null) {
                    toLoanAccount = this.loanAccountAssembler.assembleFrom(accountTransferDTO.getToAccountId());
                } else {
                    toLoanAccount = accountTransferDTO.getLoan();
                    this.loanAccountAssembler.setHelpers(toLoanAccount);
                }

            } else {
                fromSavingsAccount = accountTransferDetails.fromSavingsAccount();
                this.savingsAccountAssembler.setHelpers(fromSavingsAccount);
                toLoanAccount = accountTransferDetails.toLoanAccount();
                this.loanAccountAssembler.setHelpers(toLoanAccount);
            }

            final SavingsTransactionBooleanValues transactionBooleanValues = new SavingsTransactionBooleanValues(isAccountTransfer,
                    isRegularTransaction, fromSavingsAccount.isWithdrawalFeeApplicableForTransfer(), AccountTransferType.fromInt(
                            accountTransferDTO.getTransferType()).isInterestTransfer(), accountTransferDTO.isExceptionForBalanceCheck());

            final SavingsAccountTransaction withdrawal = this.savingsAccountDomainService.handleWithdrawal(fromSavingsAccount,
                    accountTransferDTO.getFmt(), accountTransferDTO.getTransactionDate(), accountTransferDTO.getTransactionAmount(),
                    accountTransferDTO.getPaymentDetail(), transactionBooleanValues);

            LoanTransaction loanTransaction = null;

            if (AccountTransferType.fromInt(accountTransferDTO.getTransferType()).isChargePayment()) {
                loanTransaction = this.loanAccountDomainService.makeChargePayment(toLoanAccount, accountTransferDTO.getChargeId(),
                        accountTransferDTO.getTransactionDate(), accountTransferDTO.getTransactionAmount(),
                        accountTransferDTO.getPaymentDetail(), null, null, accountTransferDTO.getToTransferType(),
                        accountTransferDTO.getLoanInstallmentNumber());

            } else {
                final boolean isRecoveryRepayment = false;
                final Boolean isHolidayValidationDone=false;
                final HolidayDetailDTO holidayDetailDto=null;
                loanTransaction = this.loanAccountDomainService.makeRepayment(toLoanAccount, new CommandProcessingResultBuilder(),
                        accountTransferDTO.getTransactionDate(), accountTransferDTO.getTransactionAmount(),
                        accountTransferDTO.getPaymentDetail(), null, null, isRecoveryRepayment, isAccountTransfer,holidayDetailDto,isHolidayValidationDone);
            }

            accountTransferDetails = this.accountTransferAssembler.assembleSavingsToLoanTransfer(accountTransferDTO, fromSavingsAccount,
                    toLoanAccount, withdrawal, loanTransaction);
            this.accountTransferDetailRepository.saveAndFlush(accountTransferDetails);
            transferTransactionId = accountTransferDetails.getId();
        } else if (isSavingsToSavingsAccountTransfer(accountTransferDTO.getFromAccountType(), accountTransferDTO.getToAccountType())) {

            SavingsAccount fromSavingsAccount = null;
            SavingsAccount toSavingsAccount = null;
            PaymentDetail paymentDetailTo = null;
            PaymentDetail paymentDetailFrom = null;
            if (accountTransferDetails == null) {
                if (accountTransferDTO.getFromSavingsAccount() == null) {
                    fromSavingsAccount = this.savingsAccountAssembler.assembleFrom(accountTransferDTO.getFromAccountId());
                } else {
                    fromSavingsAccount = accountTransferDTO.getFromSavingsAccount();
                    this.savingsAccountAssembler.setHelpers(fromSavingsAccount);
                }
                if (accountTransferDTO.getToSavingsAccount() == null) {
                    toSavingsAccount = this.savingsAccountAssembler.assembleFrom(accountTransferDTO.getToAccountId());
                } else {
                    toSavingsAccount = accountTransferDTO.getToSavingsAccount();
                    this.savingsAccountAssembler.setHelpers(toSavingsAccount);
                }
            } else {
                fromSavingsAccount = accountTransferDetails.fromSavingsAccount();
                this.savingsAccountAssembler.setHelpers(fromSavingsAccount);
                toSavingsAccount = accountTransferDetails.toSavingsAccount();
                this.savingsAccountAssembler.setHelpers(toSavingsAccount);
            }
            
            if (accountTransferDTO.getPaymentDetailTo() != null) {
            	paymentDetailTo = accountTransferDTO.getPaymentDetailTo();
            } else {
            	if (accountTransferDTO.getPaymentDetail() != null) {
            		paymentDetailTo = accountTransferDTO.getPaymentDetail();
            	}
            	if ((accountTransferDTO.getToSavingsAccount() != null) && accountTransferDTO.getToSavingsAccount().depositAccountType().isFixedDeposit()) {
            		paymentDetailTo = null;
            	}
            	paymentDetailFrom = accountTransferDTO.getPaymentDetail();
            }

            final SavingsTransactionBooleanValues transactionBooleanValues = new SavingsTransactionBooleanValues(isAccountTransfer,
                    isRegularTransaction, fromSavingsAccount.isWithdrawalFeeApplicableForTransfer(), AccountTransferType.fromInt(
                            accountTransferDTO.getTransferType()).isInterestTransfer(), accountTransferDTO.isExceptionForBalanceCheck());

            final SavingsAccountTransaction withdrawal = this.savingsAccountDomainService.handleWithdrawal(fromSavingsAccount,
                    accountTransferDTO.getFmt(), accountTransferDTO.getTransactionDate(), accountTransferDTO.getTransactionAmount(),
                    paymentDetailFrom, transactionBooleanValues);

            final SavingsAccountTransaction deposit = this.savingsAccountDomainService.handleDeposit(toSavingsAccount,
                    accountTransferDTO.getFmt(), accountTransferDTO.getTransactionDate(), accountTransferDTO.getTransactionAmount(),
                    paymentDetailTo, isAccountTransfer, isRegularTransaction);

            accountTransferDetails = this.accountTransferAssembler.assembleSavingsToSavingsTransfer(accountTransferDTO, fromSavingsAccount,
                    toSavingsAccount, withdrawal, deposit);
            this.accountTransferDetailRepository.saveAndFlush(accountTransferDetails);
            transferTransactionId = accountTransferDetails.getId();

        } else if (isLoanToSavingsAccountTransfer(accountTransferDTO.getFromAccountType(), accountTransferDTO.getToAccountType())) {

            Loan fromLoanAccount = null;
            SavingsAccount toSavingsAccount = null;
            if (accountTransferDetails == null) {
                if (accountTransferDTO.getLoan() == null) {
                    fromLoanAccount = this.loanAccountAssembler.assembleFrom(accountTransferDTO.getFromAccountId());
                } else {
                    fromLoanAccount = accountTransferDTO.getLoan();
                    this.loanAccountAssembler.setHelpers(fromLoanAccount);
                }
                toSavingsAccount = this.savingsAccountAssembler.assembleFrom(accountTransferDTO.getToAccountId());
            } else {
                fromLoanAccount = accountTransferDetails.fromLoanAccount();
                this.loanAccountAssembler.setHelpers(fromLoanAccount);
                toSavingsAccount = accountTransferDetails.toSavingsAccount();
                this.savingsAccountAssembler.setHelpers(toSavingsAccount);
            }
            LoanTransaction loanTransaction = null;
            if (LoanTransactionType.DISBURSEMENT.getValue().equals(accountTransferDTO.getFromTransferType())) {
                loanTransaction = this.loanAccountDomainService.makeDisburseTransaction(accountTransferDTO.getFromAccountId(),
                        accountTransferDTO.getTransactionDate(), accountTransferDTO.getTransactionAmount(),
                        accountTransferDTO.getPaymentDetail(), accountTransferDTO.getNoteText(), accountTransferDTO.getTxnExternalId());
            } else {
                loanTransaction = this.loanAccountDomainService.makeRefund(accountTransferDTO.getFromAccountId(),
                        new CommandProcessingResultBuilder(), accountTransferDTO.getTransactionDate(),
                        accountTransferDTO.getTransactionAmount(), accountTransferDTO.getPaymentDetail(), accountTransferDTO.getNoteText(),
                        accountTransferDTO.getTxnExternalId());
            }

            final SavingsAccountTransaction deposit = this.savingsAccountDomainService.handleDeposit(toSavingsAccount,
                    accountTransferDTO.getFmt(), accountTransferDTO.getTransactionDate(), accountTransferDTO.getTransactionAmount(),
                    accountTransferDTO.getPaymentDetail(), isAccountTransfer, isRegularTransaction);
            accountTransferDetails = this.accountTransferAssembler.assembleLoanToSavingsTransfer(accountTransferDTO, fromLoanAccount,
                    toSavingsAccount, deposit, loanTransaction);
            this.accountTransferDetailRepository.saveAndFlush(accountTransferDetails);
            transferTransactionId = accountTransferDetails.getId();
        } else {
            throw new GeneralPlatformDomainRuleException("error.msg.accounttransfer.loan.to.loan.not.supported",
                    "Account transfer from loan to another loan is not supported");
        }

        return transferTransactionId;
    }
    
    @Override
    @Transactional
    public Long transferFundsClosureFD(final AccountTransferDTO accountTransferDTO, FixedDepositAccount fromFixedDepositAccount,
    		PaymentDetail paymentDetailPrincipal, PaymentDetail paymentDetailInterest) {
        Long transferTransactionId = null;
        final boolean isAccountTransfer = true;
        final boolean isRegularTransaction = accountTransferDTO.isRegularTransaction();
        AccountTransferDetails accountTransferDetails = accountTransferDTO.getAccountTransferDetails();
        
        if (isSavingsToSavingsAccountTransfer(accountTransferDTO.getFromAccountType(), accountTransferDTO.getToAccountType())) {

            SavingsAccount fromSavingsAccount = null;
            SavingsAccount toSavingsAccount = null;
            
            final BigDecimal principalAmount = fromFixedDepositAccount.getAccountTermAndPreClosure().depositAmount();
            final BigDecimal netInterestAmount = accountTransferDTO.getTransactionAmount().subtract(principalAmount);
            
            if (accountTransferDetails == null) {
                if (accountTransferDTO.getFromSavingsAccount() == null) {
                    fromSavingsAccount = this.savingsAccountAssembler.assembleFrom(accountTransferDTO.getFromAccountId());
                } else {
                    fromSavingsAccount = accountTransferDTO.getFromSavingsAccount();
                    this.savingsAccountAssembler.setHelpers(fromSavingsAccount);
                }
                if (accountTransferDTO.getToSavingsAccount() == null) {
                    toSavingsAccount = this.savingsAccountAssembler.assembleFrom(accountTransferDTO.getToAccountId());
                } else {
                    toSavingsAccount = accountTransferDTO.getToSavingsAccount();
                    this.savingsAccountAssembler.setHelpers(toSavingsAccount);
                }
            } else {
                fromSavingsAccount = accountTransferDetails.fromSavingsAccount();
                this.savingsAccountAssembler.setHelpers(fromSavingsAccount);
                toSavingsAccount = accountTransferDetails.toSavingsAccount();
                this.savingsAccountAssembler.setHelpers(toSavingsAccount);
            }

            final SavingsTransactionBooleanValues transactionBooleanValues = new SavingsTransactionBooleanValues(isAccountTransfer,
                    isRegularTransaction, fromSavingsAccount.isWithdrawalFeeApplicableForTransfer(), AccountTransferType.fromInt(
                            accountTransferDTO.getTransferType()).isInterestTransfer(), accountTransferDTO.isExceptionForBalanceCheck());

            final SavingsAccountTransaction withdrawalPrincipal = this.savingsAccountDomainService.handleWithdrawal(fromSavingsAccount,
                    accountTransferDTO.getFmt(), accountTransferDTO.getTransactionDate(), principalAmount,
                    paymentDetailPrincipal, transactionBooleanValues);
            
            final SavingsAccountTransaction withdrawalInterest = this.savingsAccountDomainService.handleWithdrawal(fromSavingsAccount,
                    accountTransferDTO.getFmt(), accountTransferDTO.getTransactionDate(), netInterestAmount,
                    paymentDetailInterest, transactionBooleanValues);

            final SavingsAccountTransaction depositPrincipal = this.savingsAccountDomainService.handleDeposit(toSavingsAccount,
                    accountTransferDTO.getFmt(), accountTransferDTO.getTransactionDate(), principalAmount,
                    null, isAccountTransfer, isRegularTransaction);
            
            final SavingsAccountTransaction depositInterest = this.savingsAccountDomainService.handleDeposit(toSavingsAccount,
                    accountTransferDTO.getFmt(), accountTransferDTO.getTransactionDate(), netInterestAmount,
                    null, isAccountTransfer, isRegularTransaction);

            accountTransferDetails = this.accountTransferAssembler.assembleSavingsToSavingsTransfer(accountTransferDTO, fromSavingsAccount,
                    toSavingsAccount, withdrawalPrincipal, depositPrincipal);
            this.accountTransferDetailRepository.saveAndFlush(accountTransferDetails);
            
            accountTransferDetails = this.accountTransferAssembler.assembleSavingsToSavingsTransfer(accountTransferDTO, fromSavingsAccount,
                    toSavingsAccount, withdrawalInterest, depositInterest);
            this.accountTransferDetailRepository.saveAndFlush(accountTransferDetails);
            
            transferTransactionId = accountTransferDetails.getId();

        } else {
            throw new GeneralPlatformDomainRuleException("error.msg.accounttransfer.loan.to.loan.not.supported",
                    "Account transfer from loan to another loan is not supported");
        }

        return transferTransactionId;
    }

    @Override
    public AccountTransferDetails repayLoanWithTopup(AccountTransferDTO accountTransferDTO) {
        final boolean isAccountTransfer = true;
        Loan fromLoanAccount = null;
        if (accountTransferDTO.getFromLoan() == null) {
            fromLoanAccount = this.loanAccountAssembler.assembleFrom(accountTransferDTO.getFromAccountId());
        } else {
            fromLoanAccount = accountTransferDTO.getFromLoan();
            this.loanAccountAssembler.setHelpers(fromLoanAccount);
        }
        Loan toLoanAccount = null;
        if (accountTransferDTO.getToLoan() == null) {
            toLoanAccount = this.loanAccountAssembler.assembleFrom(accountTransferDTO.getToAccountId());
        } else {
            toLoanAccount = accountTransferDTO.getToLoan();
            this.loanAccountAssembler.setHelpers(toLoanAccount);
        }

        LoanTransaction disburseTransaction = this.loanAccountDomainService.makeDisburseTransaction(accountTransferDTO.getFromAccountId(),
                accountTransferDTO.getTransactionDate(), accountTransferDTO.getTransactionAmount(),
                accountTransferDTO.getPaymentDetail(), accountTransferDTO.getNoteText(), accountTransferDTO.getTxnExternalId(), true);

        LoanTransaction repayTransaction = this.loanAccountDomainService.makeRepayment(toLoanAccount, new CommandProcessingResultBuilder(),
                accountTransferDTO.getTransactionDate(), accountTransferDTO.getTransactionAmount(),
                accountTransferDTO.getPaymentDetail(), null, null, false, isAccountTransfer,null,false, true);

        AccountTransferDetails accountTransferDetails = this.accountTransferAssembler.assembleLoanToLoanTransfer(accountTransferDTO, fromLoanAccount,
                toLoanAccount, disburseTransaction, repayTransaction);
        this.accountTransferDetailRepository.saveAndFlush(accountTransferDetails);

        return accountTransferDetails;
    }

    @Override
    @Transactional
    public void updateLoanTransaction(final Long loanTransactionId, final LoanTransaction newLoanTransaction) {
        final AccountTransferTransaction transferTransaction = this.accountTransferRepository.findByToLoanTransactionId(loanTransactionId);
        if (transferTransaction != null) {
            transferTransaction.updateToLoanTransaction(newLoanTransaction);
            this.accountTransferRepository.save(transferTransaction);
        }
    }

    private boolean isLoanToSavingsAccountTransfer(final PortfolioAccountType fromAccountType, final PortfolioAccountType toAccountType) {
        return fromAccountType.isLoanAccount() && toAccountType.isSavingsAccount();
    }

    private boolean isSavingsToLoanAccountTransfer(final PortfolioAccountType fromAccountType, final PortfolioAccountType toAccountType) {
        return fromAccountType.isSavingsAccount() && toAccountType.isLoanAccount();
    }

    private boolean isSavingsToSavingsAccountTransfer(final PortfolioAccountType fromAccountType, final PortfolioAccountType toAccountType) {
        return fromAccountType.isSavingsAccount() && toAccountType.isSavingsAccount();
    }
    
    @Override
    @Transactional
    public CommandProcessingResult refundByTransfer(JsonCommand command) {
        // TODO Auto-generated method stub
        this.accountTransfersDataValidator.validate(command);

        final LocalDate transactionDate = command.localDateValueOfParameterNamed(transferDateParamName);
        final BigDecimal transactionAmount = command.bigDecimalValueOfParameterNamed(transferAmountParamName);

        final Locale locale = command.extractLocale();
        final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);

        final PaymentDetail paymentDetail = null; 
        Long transferTransactionId = null;

        final Long fromLoanAccountId = command.longValueOfParameterNamed(fromAccountIdParamName);
        final Loan fromLoanAccount = this.loanAccountAssembler.assembleFrom(fromLoanAccountId);

        BigDecimal overpaid = this.loanReadPlatformService.retrieveTotalPaidInAdvance(fromLoanAccountId).getPaidInAdvance();

        if (overpaid == null || overpaid.equals(BigDecimal.ZERO) || transactionAmount.floatValue() > overpaid.floatValue()) {
            if(overpaid == null)
                overpaid = BigDecimal.ZERO;
            throw new InvalidPaidInAdvanceAmountException(overpaid.toPlainString());
        }

        final LoanTransaction loanRefundTransaction = this.loanAccountDomainService.makeRefundForActiveLoan(fromLoanAccountId,
                new CommandProcessingResultBuilder(), transactionDate, transactionAmount, paymentDetail, null, null);

        final Long toSavingsAccountId = command.longValueOfParameterNamed(toAccountIdParamName);
        final SavingsAccount toSavingsAccount = this.savingsAccountAssembler.assembleFrom(toSavingsAccountId);

        final SavingsAccountTransaction deposit = this.savingsAccountDomainService.handleDeposit(toSavingsAccount, fmt, transactionDate,
                transactionAmount, paymentDetail, true, true);

        final AccountTransferDetails accountTransferDetails = this.accountTransferAssembler.assembleLoanToSavingsTransfer(command,
                fromLoanAccount, toSavingsAccount, deposit, loanRefundTransaction);
        this.accountTransferDetailRepository.saveAndFlush(accountTransferDetails);
        transferTransactionId = accountTransferDetails.getId();

        final CommandProcessingResultBuilder builder = new CommandProcessingResultBuilder().withEntityId(transferTransactionId);

        // if (fromAccountType.isSavingsAccount()) {

        builder.withSavingsId(toSavingsAccountId);
        // }

        return builder.build();
    }
}