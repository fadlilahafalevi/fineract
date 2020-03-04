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
package org.apache.fineract.scheduledjobs.service;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.accounting.glaccount.domain.TrialBalance;
import org.apache.fineract.accounting.glaccount.domain.TrialBalanceRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.api.LoanApiConstants;
import org.apache.fineract.portfolio.loanaccount.collectibility.data.LoanCollectibilityData;
import org.apache.fineract.portfolio.loanaccount.collectibility.domain.LoanCollectibility;
import org.apache.fineract.portfolio.loanaccount.collectibility.domain.LoanCollectibilityRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.collectibility.service.LoanCollectibilityReadService;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepositoryWrapper;
import org.joda.time.LocalDate;
import org.joda.time.DateTime;
import org.apache.fineract.infrastructure.core.boot.JDBCDriverConfig;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.domain.FineractPlatformTenant;
import org.apache.fineract.infrastructure.core.domain.FineractPlatformTenantConnection;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.RoutingDataSourceServiceFactory;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.jobs.annotation.CronTarget;
import org.apache.fineract.infrastructure.jobs.exception.JobExecutionException;
import org.apache.fineract.infrastructure.jobs.service.JobName;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.provisioning.data.ProvisioningCriteriaData;
import org.apache.fineract.organisation.provisioning.data.ProvisioningCriteriaDefinitionData;
import org.apache.fineract.organisation.provisioning.service.ProvisioningCriteriaReadPlatformService;
import org.apache.fineract.portfolio.savings.DepositAccountType;
import org.apache.fineract.portfolio.savings.DepositAccountUtils;
import org.apache.fineract.portfolio.savings.data.DepositAccountData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountAnnualFeeData;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountRepository;
import org.apache.fineract.portfolio.savings.service.DepositAccountReadPlatformService;
import org.apache.fineract.portfolio.savings.service.DepositAccountWritePlatformService;
import org.apache.fineract.portfolio.savings.service.SavingsAccountChargeReadPlatformService;
import org.apache.fineract.portfolio.savings.service.SavingsAccountWritePlatformService;
import org.apache.fineract.portfolio.shareaccounts.service.ShareAccountDividendReadPlatformService;
import org.apache.fineract.portfolio.shareaccounts.service.ShareAccountSchedularService;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Service(value = "scheduledJobRunnerService")
public class ScheduledJobRunnerServiceImpl implements ScheduledJobRunnerService {
    @Autowired private JDBCDriverConfig driverConfig;

    private final static Logger logger = LoggerFactory.getLogger(ScheduledJobRunnerServiceImpl.class);
    private final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
    private final DateTimeFormatter formatterWithTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    private final RoutingDataSourceServiceFactory dataSourceServiceFactory;
    private final SavingsAccountWritePlatformService savingsAccountWritePlatformService;
    private final SavingsAccountChargeReadPlatformService savingsAccountChargeReadPlatformService;
    private final DepositAccountReadPlatformService depositAccountReadPlatformService;
    private final DepositAccountWritePlatformService depositAccountWritePlatformService;
    private final ShareAccountDividendReadPlatformService shareAccountDividendReadPlatformService;
    private final ShareAccountSchedularService shareAccountSchedularService;
    private final TrialBalanceRepositoryWrapper trialBalanceRepositoryWrapper;
    private final PlatformSecurityContext context;
    private final LoanCollectibilityRepositoryWrapper loanCollectibilityRepositoryWrapper; 
    private final LoanCollectibilityReadService loanCollectibilityReadService;
    private final ProvisioningCriteriaReadPlatformService provisioningCriteriaReadPlatformService;
    private final LoanRepositoryWrapper loanRepositoryWrapper;
    private final SavingsAccountRepository savingsAccountRepository;

    @Autowired
    public ScheduledJobRunnerServiceImpl(final RoutingDataSourceServiceFactory dataSourceServiceFactory,
            final SavingsAccountWritePlatformService savingsAccountWritePlatformService,
            final SavingsAccountChargeReadPlatformService savingsAccountChargeReadPlatformService,
            final DepositAccountReadPlatformService depositAccountReadPlatformService,
            final DepositAccountWritePlatformService depositAccountWritePlatformService,
            final ShareAccountDividendReadPlatformService shareAccountDividendReadPlatformService,
            final ShareAccountSchedularService shareAccountSchedularService, 
            final TrialBalanceRepositoryWrapper trialBalanceRepositoryWrapper,
            final PlatformSecurityContext context,
            final LoanCollectibilityRepositoryWrapper loanCollectibilityRepositoryWrapper,
            final LoanCollectibilityReadService loanCollectibilityReadService,
            final ProvisioningCriteriaReadPlatformService provisioningCriteriaReadPlatformService,
            final LoanRepositoryWrapper loanRepositoryWrapper,
            final SavingsAccountRepository savingsAccountRepository) {
        this.dataSourceServiceFactory = dataSourceServiceFactory;
        this.savingsAccountWritePlatformService = savingsAccountWritePlatformService;
        this.savingsAccountChargeReadPlatformService = savingsAccountChargeReadPlatformService;
        this.depositAccountReadPlatformService = depositAccountReadPlatformService;
        this.depositAccountWritePlatformService = depositAccountWritePlatformService;
        this.shareAccountDividendReadPlatformService = shareAccountDividendReadPlatformService;
        this.shareAccountSchedularService = shareAccountSchedularService;
        this.trialBalanceRepositoryWrapper=trialBalanceRepositoryWrapper;
        this.context = context;
        this.loanCollectibilityRepositoryWrapper = loanCollectibilityRepositoryWrapper;
        this.loanCollectibilityReadService = loanCollectibilityReadService;
        this.provisioningCriteriaReadPlatformService = provisioningCriteriaReadPlatformService;
        this.loanRepositoryWrapper = loanRepositoryWrapper;
        this.savingsAccountRepository = savingsAccountRepository;
    }

    @Transactional
    @Override
    @CronTarget(jobName = JobName.UPDATE_LOAN_SUMMARY)
    public void updateLoanSummaryDetails() {

        final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSourceServiceFactory.determineDataSourceService().retrieveDataSource());

        final StringBuilder updateSqlBuilder = new StringBuilder(900);
        updateSqlBuilder.append("update m_loan ");
        updateSqlBuilder.append("join (");
        updateSqlBuilder.append("SELECT ml.id AS loanId,");
        updateSqlBuilder.append("SUM(mr.principal_amount) as principal_disbursed_derived, ");
        updateSqlBuilder.append("SUM(IFNULL(mr.principal_completed_derived,0)) as principal_repaid_derived, ");
        updateSqlBuilder.append("SUM(IFNULL(mr.principal_writtenoff_derived,0)) as principal_writtenoff_derived,");
        updateSqlBuilder.append("SUM(IFNULL(mr.interest_amount,0)) as interest_charged_derived,");
        updateSqlBuilder.append("SUM(IFNULL(mr.interest_completed_derived,0)) as interest_repaid_derived,");
        updateSqlBuilder.append("SUM(IFNULL(mr.interest_waived_derived,0)) as interest_waived_derived,");
        updateSqlBuilder.append("SUM(IFNULL(mr.interest_writtenoff_derived,0)) as interest_writtenoff_derived,");
        updateSqlBuilder
                .append("SUM(IFNULL(mr.fee_charges_amount,0)) + IFNULL((select SUM(lc.amount) from  m_loan_charge lc where lc.loan_id=ml.id and lc.is_active=1 and lc.charge_time_enum=1),0) as fee_charges_charged_derived,");
        updateSqlBuilder
                .append("SUM(IFNULL(mr.fee_charges_completed_derived,0)) + IFNULL((select SUM(lc.amount_paid_derived) from  m_loan_charge lc where lc.loan_id=ml.id and lc.is_active=1 and lc.charge_time_enum=1),0) as fee_charges_repaid_derived,");
        updateSqlBuilder.append("SUM(IFNULL(mr.fee_charges_waived_derived,0)) as fee_charges_waived_derived,");
        updateSqlBuilder.append("SUM(IFNULL(mr.fee_charges_writtenoff_derived,0)) as fee_charges_writtenoff_derived,");
        updateSqlBuilder.append("SUM(IFNULL(mr.penalty_charges_amount,0)) as penalty_charges_charged_derived,");
        updateSqlBuilder.append("SUM(IFNULL(mr.penalty_charges_completed_derived,0)) as penalty_charges_repaid_derived,");
        updateSqlBuilder.append("SUM(IFNULL(mr.penalty_charges_waived_derived,0)) as penalty_charges_waived_derived,");
        updateSqlBuilder.append("SUM(IFNULL(mr.penalty_charges_writtenoff_derived,0)) as penalty_charges_writtenoff_derived ");
        updateSqlBuilder.append(" FROM m_loan ml ");
        updateSqlBuilder.append("INNER JOIN m_loan_repayment_schedule mr on mr.loan_id = ml.id ");
        updateSqlBuilder.append("WHERE ml.disbursedon_date is not null ");
        updateSqlBuilder.append("GROUP BY ml.id ");
        updateSqlBuilder.append(") x on x.loanId = m_loan.id ");

        updateSqlBuilder.append("SET m_loan.principal_disbursed_derived = x.principal_disbursed_derived,");
        updateSqlBuilder.append("m_loan.principal_repaid_derived = x.principal_repaid_derived,");
        updateSqlBuilder.append("m_loan.principal_writtenoff_derived = x.principal_writtenoff_derived,");
        updateSqlBuilder
                .append("m_loan.principal_outstanding_derived = (x.principal_disbursed_derived - (x.principal_repaid_derived + x.principal_writtenoff_derived)),");
        updateSqlBuilder.append("m_loan.interest_charged_derived = x.interest_charged_derived,");
        updateSqlBuilder.append("m_loan.interest_repaid_derived = x.interest_repaid_derived,");
        updateSqlBuilder.append("m_loan.interest_waived_derived = x.interest_waived_derived,");
        updateSqlBuilder.append("m_loan.interest_writtenoff_derived = x.interest_writtenoff_derived,");
        updateSqlBuilder
                .append("m_loan.interest_outstanding_derived = (x.interest_charged_derived - (x.interest_repaid_derived + x.interest_waived_derived + x.interest_writtenoff_derived)),");
        updateSqlBuilder.append("m_loan.fee_charges_charged_derived = x.fee_charges_charged_derived,");
        updateSqlBuilder.append("m_loan.fee_charges_repaid_derived = x.fee_charges_repaid_derived,");
        updateSqlBuilder.append("m_loan.fee_charges_waived_derived = x.fee_charges_waived_derived,");
        updateSqlBuilder.append("m_loan.fee_charges_writtenoff_derived = x.fee_charges_writtenoff_derived,");
        updateSqlBuilder
                .append("m_loan.fee_charges_outstanding_derived = (x.fee_charges_charged_derived - (x.fee_charges_repaid_derived + x.fee_charges_waived_derived + x.fee_charges_writtenoff_derived)),");
        updateSqlBuilder.append("m_loan.penalty_charges_charged_derived = x.penalty_charges_charged_derived,");
        updateSqlBuilder.append("m_loan.penalty_charges_repaid_derived = x.penalty_charges_repaid_derived,");
        updateSqlBuilder.append("m_loan.penalty_charges_waived_derived = x.penalty_charges_waived_derived,");
        updateSqlBuilder.append("m_loan.penalty_charges_writtenoff_derived = x.penalty_charges_writtenoff_derived,");
        updateSqlBuilder
                .append("m_loan.penalty_charges_outstanding_derived = (x.penalty_charges_charged_derived - (x.penalty_charges_repaid_derived + x.penalty_charges_waived_derived + x.penalty_charges_writtenoff_derived)),");
        updateSqlBuilder
                .append("m_loan.total_expected_repayment_derived = (x.principal_disbursed_derived + x.interest_charged_derived + x.fee_charges_charged_derived + x.penalty_charges_charged_derived),");
        updateSqlBuilder
                .append("m_loan.total_repayment_derived = (x.principal_repaid_derived + x.interest_repaid_derived + x.fee_charges_repaid_derived + x.penalty_charges_repaid_derived),");
        updateSqlBuilder
                .append("m_loan.total_expected_costofloan_derived = (x.interest_charged_derived + x.fee_charges_charged_derived + x.penalty_charges_charged_derived),");
        updateSqlBuilder
                .append("m_loan.total_costofloan_derived = (x.interest_repaid_derived + x.fee_charges_repaid_derived + x.penalty_charges_repaid_derived),");
        updateSqlBuilder
                .append("m_loan.total_waived_derived = (x.interest_waived_derived + x.fee_charges_waived_derived + x.penalty_charges_waived_derived),");
        updateSqlBuilder
                .append("m_loan.total_writtenoff_derived = (x.interest_writtenoff_derived +  x.fee_charges_writtenoff_derived + x.penalty_charges_writtenoff_derived),");
        updateSqlBuilder.append("m_loan.total_outstanding_derived=");
        updateSqlBuilder.append(" (x.principal_disbursed_derived - (x.principal_repaid_derived + x.principal_writtenoff_derived)) + ");
        updateSqlBuilder
                .append(" (x.interest_charged_derived - (x.interest_repaid_derived + x.interest_waived_derived + x.interest_writtenoff_derived)) +");
        updateSqlBuilder
                .append(" (x.fee_charges_charged_derived - (x.fee_charges_repaid_derived + x.fee_charges_waived_derived + x.fee_charges_writtenoff_derived)) +");
        updateSqlBuilder
                .append(" (x.penalty_charges_charged_derived - (x.penalty_charges_repaid_derived + x.penalty_charges_waived_derived + x.penalty_charges_writtenoff_derived))");

        final int result = jdbcTemplate.update(updateSqlBuilder.toString());

        logger.info(ThreadLocalContextUtil.getTenant().getName() + ": Results affected by update: " + result);
    }

    @Transactional
    @Override
    @CronTarget(jobName = JobName.UPDATE_LOAN_PAID_IN_ADVANCE)
    public void updateLoanPaidInAdvance() {

        final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSourceServiceFactory.determineDataSourceService().retrieveDataSource());

        jdbcTemplate.execute("truncate table m_loan_paid_in_advance");

        final StringBuilder updateSqlBuilder = new StringBuilder(900);

        updateSqlBuilder
                .append("INSERT INTO m_loan_paid_in_advance(loan_id, principal_in_advance_derived, interest_in_advance_derived, fee_charges_in_advance_derived, penalty_charges_in_advance_derived, total_in_advance_derived)");
        updateSqlBuilder.append(" select ml.id as loanId,");
        updateSqlBuilder.append(" SUM(ifnull(mr.principal_completed_derived, 0)) as principal_in_advance_derived,");
        updateSqlBuilder.append(" SUM(ifnull(mr.interest_completed_derived, 0)) as interest_in_advance_derived,");
        updateSqlBuilder.append(" SUM(ifnull(mr.fee_charges_completed_derived, 0)) as fee_charges_in_advance_derived,");
        updateSqlBuilder.append(" SUM(ifnull(mr.penalty_charges_completed_derived, 0)) as penalty_charges_in_advance_derived,");
        updateSqlBuilder
                .append(" (SUM(ifnull(mr.principal_completed_derived, 0)) + SUM(ifnull(mr.interest_completed_derived, 0)) + SUM(ifnull(mr.fee_charges_completed_derived, 0)) + SUM(ifnull(mr.penalty_charges_completed_derived, 0))) as total_in_advance_derived");
        updateSqlBuilder.append(" FROM m_loan ml ");
        updateSqlBuilder.append(" INNER JOIN m_loan_repayment_schedule mr on mr.loan_id = ml.id ");
        updateSqlBuilder.append(" WHERE ml.loan_status_id = 300 ");
        updateSqlBuilder.append(" and mr.duedate >= CURDATE() ");
        updateSqlBuilder.append(" GROUP BY ml.id");
        updateSqlBuilder
                .append(" HAVING (SUM(ifnull(mr.principal_completed_derived, 0)) + SUM(ifnull(mr.interest_completed_derived, 0)) +");
        updateSqlBuilder
                .append(" SUM(ifnull(mr.fee_charges_completed_derived, 0)) + SUM(ifnull(mr.penalty_charges_completed_derived, 0))) > 0.0");

        final int result = jdbcTemplate.update(updateSqlBuilder.toString());

        logger.info(ThreadLocalContextUtil.getTenant().getName() + ": Results affected by update: " + result);
    }

    @Override
    @CronTarget(jobName = JobName.APPLY_ANNUAL_FEE_FOR_SAVINGS)
    public void applyAnnualFeeForSavings() {

        final Collection<SavingsAccountAnnualFeeData> annualFeeData = this.savingsAccountChargeReadPlatformService
                .retrieveChargesWithAnnualFeeDue();

        for (final SavingsAccountAnnualFeeData savingsAccountReference : annualFeeData) {
            try {
                this.savingsAccountWritePlatformService.applyAnnualFee(savingsAccountReference.getId(),
                        savingsAccountReference.getAccountId());
            } catch (final PlatformApiDataValidationException e) {
                final List<ApiParameterError> errors = e.getErrors();
                for (final ApiParameterError error : errors) {
                    logger.error("Apply annual fee failed for account:" + savingsAccountReference.getAccountNo() + " with message "
                            + error.getDeveloperMessage());
                }
            } catch (final Exception ex) {
                // need to handle this scenario
            }
        }

        logger.info(ThreadLocalContextUtil.getTenant().getName() + ": Savings accounts affected by update: " + annualFeeData.size());
    }

    @Override
    @CronTarget(jobName = JobName.PAY_DUE_SAVINGS_CHARGES)
    public void applyDueChargesForSavings() throws JobExecutionException {
        final Collection<SavingsAccountAnnualFeeData> chargesDueData = this.savingsAccountChargeReadPlatformService
                .retrieveChargesWithDue();
        final StringBuilder errorMsg = new StringBuilder();

        for (final SavingsAccountAnnualFeeData savingsAccountReference : chargesDueData) {
            try {
                this.savingsAccountWritePlatformService.applyChargeDue(savingsAccountReference.getId(),
                        savingsAccountReference.getAccountId());
            } catch (final PlatformApiDataValidationException e) {
                final List<ApiParameterError> errors = e.getErrors();
                for (final ApiParameterError error : errors) {
                    logger.error("Apply Charges due for savings failed for account:" + savingsAccountReference.getAccountNo()
                            + " with message " + error.getDeveloperMessage());
                    errorMsg.append("Apply Charges due for savings failed for account:").append(savingsAccountReference.getAccountNo())
                            .append(" with message ").append(error.getDeveloperMessage());
                }
            }
        }

        logger.info(ThreadLocalContextUtil.getTenant().getName() + ": Savings accounts affected by update: " + chargesDueData.size());

        /*
         * throw exception if any charge payment fails.
         */
        if (errorMsg.length() > 0) { throw new JobExecutionException(errorMsg.toString()); }
    }

    @Transactional
    @Override
    @CronTarget(jobName = JobName.UPDATE_NPA)
    public void updateNPA() {

        final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSourceServiceFactory.determineDataSourceService().retrieveDataSource());

/*        final StringBuilder resetNPASqlBuilder = new StringBuilder(900);
        resetNPASqlBuilder.append("update m_loan loan ");
        resetNPASqlBuilder.append("left join m_loan_arrears_aging laa on laa.loan_id = loan.id ");
        resetNPASqlBuilder.append("inner join m_product_loan mpl on mpl.id = loan.product_id and mpl.overdue_days_for_npa is not null ");
        resetNPASqlBuilder.append("set loan.is_npa = 0 ");
        resetNPASqlBuilder.append("where  loan.loan_status_id = 300 and mpl.account_moves_out_of_npa_only_on_arrears_completion = 0 ");
        resetNPASqlBuilder
                .append("or (mpl.account_moves_out_of_npa_only_on_arrears_completion = 1 and laa.overdue_since_date_derived is null)");

        jdbcTemplate.update(resetNPASqlBuilder.toString());

        final StringBuilder updateSqlBuilder = new StringBuilder(900);

        updateSqlBuilder.append("UPDATE m_loan as ml,");
        updateSqlBuilder.append(" (select loan.id ");
        updateSqlBuilder.append("from m_loan_arrears_aging laa");
        updateSqlBuilder.append(" INNER JOIN  m_loan loan on laa.loan_id = loan.id ");
        updateSqlBuilder.append(" INNER JOIN m_product_loan mpl on mpl.id = loan.product_id AND mpl.overdue_days_for_npa is not null ");
        updateSqlBuilder.append("WHERE loan.loan_status_id = 300  and ");
        updateSqlBuilder.append("laa.overdue_since_date_derived < SUBDATE(CURDATE(),INTERVAL  ifnull(mpl.overdue_days_for_npa,0) day) ");
        updateSqlBuilder.append("group by loan.id) as sl ");
        updateSqlBuilder.append("SET ml.is_npa=1 where ml.id=sl.id ");*/
        
        final StringBuilder resetNPASqlBuilder = new StringBuilder(900);
        resetNPASqlBuilder.append("update m_loan ");
        resetNPASqlBuilder.append("set is_npa = '0', npl_date = null");

        jdbcTemplate.update(resetNPASqlBuilder.toString());
        
        final StringBuilder updateSqlBuilder = new StringBuilder(900);

        updateSqlBuilder.append("UPDATE m_loan as ml, ");
        updateSqlBuilder.append("(select distinct l.id as loanId from m_loan l ");
        updateSqlBuilder.append("join m_loan_repayment_schedule sch on sch.loan_id = l.id ");
        updateSqlBuilder.append("join m_loanproduct_provisioning_mapping lpm on lpm.product_id = l.product_id ");
        updateSqlBuilder.append("join m_loan_collectibility lc on lc.loan_id = l.id ");
        updateSqlBuilder.append("left join m_provisioning_criteria_definition pcd1 on pcd1.criteria_id = lpm.criteria_id and pcd1.category_id = lc.collectibility_account ");
        updateSqlBuilder.append("LEFT JOIN m_client mclient ON mclient.id = l.client_id ");
        updateSqlBuilder.append("LEFT JOIN m_group mgroup ON mgroup.id = l.group_id ");
        updateSqlBuilder.append("where l.loan_status_id=300 and sch.duedate = (select MIN(sch1.duedate) from m_loan_repayment_schedule sch1 where sch1.loan_id=l.id and sch1.completed_derived=false) ");
        updateSqlBuilder.append("and pcd1.is_npl = true) as sl ");
        updateSqlBuilder.append("SET is_npa = '1', npl_date = CURDATE() ");
        updateSqlBuilder.append("where ml.id=sl.loanId ");
        
        final int result = jdbcTemplate.update(updateSqlBuilder.toString());

        logger.info(ThreadLocalContextUtil.getTenant().getName() + ": Results affected by update: " + result);
    }

    @Override
    @CronTarget(jobName = JobName.UPDATE_DEPOSITS_ACCOUNT_MATURITY_DETAILS)
    public void updateMaturityDetailsOfDepositAccounts() {

        final Collection<DepositAccountData> depositAccounts = this.depositAccountReadPlatformService.retrieveForMaturityUpdate();

        for (final DepositAccountData depositAccount : depositAccounts) {
            try {
                final DepositAccountType depositAccountType = DepositAccountType.fromInt(depositAccount.depositType().getId().intValue());
                this.depositAccountWritePlatformService.updateMaturityDetails(depositAccount.id(), depositAccountType);
            } catch (final PlatformApiDataValidationException e) {
                final List<ApiParameterError> errors = e.getErrors();
                for (final ApiParameterError error : errors) {
                    logger.error("Update maturity details failed for account:" + depositAccount.accountNo() + " with message "
                            + error.getDeveloperMessage());
                }
            } catch (final Exception ex) {
                // need to handle this scenario
            }
        }

        logger.info(ThreadLocalContextUtil.getTenant().getName() + ": Deposit accounts affected by update: " + depositAccounts.size());
    }

    @Override
    @CronTarget(jobName = JobName.GENERATE_RD_SCEHDULE)
    public void generateRDSchedule() {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSourceServiceFactory.determineDataSourceService().retrieveDataSource());
        final Collection<Map<String, Object>> scheduleDetails = this.depositAccountReadPlatformService.retriveDataForRDScheduleCreation();
        String insertSql = "INSERT INTO `m_mandatory_savings_schedule` (`savings_account_id`, `duedate`, `installment`, `deposit_amount`, `completed_derived`, `created_date`, `lastmodified_date`) VALUES ";
        StringBuilder sb = new StringBuilder();
        String currentDate = formatterWithTime.print(DateUtils.getLocalDateTimeOfTenant());
        int iterations = 0;
        for (Map<String, Object> details : scheduleDetails) {
            Long count = (Long) details.get("futureInstallemts");
            if (count == null) {
                count = 0l;
            }
            final Long savingsId = (Long) details.get("savingsId");
            final BigDecimal amount = (BigDecimal) details.get("amount");
            final String recurrence = (String) details.get("recurrence");
            Date date = (Date) details.get("dueDate");
            LocalDate lastDepositDate = new LocalDate(date);
            Integer installmentNumber = (Integer) details.get("installment");
            while (count < DepositAccountUtils.GENERATE_MINIMUM_NUMBER_OF_FUTURE_INSTALMENTS) {
                count++;
                installmentNumber++;
                lastDepositDate = DepositAccountUtils.calculateNextDepositDate(lastDepositDate, recurrence);

                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append("(");
                sb.append(savingsId);
                sb.append(",'");
                sb.append(formatter.print(lastDepositDate));
                sb.append("',");
                sb.append(installmentNumber);
                sb.append(",");
                sb.append(amount);
                sb.append(", b'0','");
                sb.append(currentDate);
                sb.append("','");
                sb.append(currentDate);
                sb.append("')");
                iterations++;
                if (iterations > 200) {
                    jdbcTemplate.update(insertSql + sb.toString());
                    sb = new StringBuilder();
                }

            }
        }

        if (sb.length() > 0) {
            jdbcTemplate.update(insertSql + sb.toString());
        }

    }

    @Override
    @CronTarget(jobName = JobName.POST_DIVIDENTS_FOR_SHARES)
    public void postDividends() throws JobExecutionException {
        List<Map<String, Object>> dividendDetails = this.shareAccountDividendReadPlatformService.retriveDividendDetailsForPostDividents();
        StringBuilder errorMsg = new StringBuilder();
        for (Map<String, Object> dividendMap : dividendDetails) {
        	Long id = null ;
        	Long savingsId = null ;
        	if(dividendMap.get("id") instanceof BigInteger) { //Drizzle is returning BigInteger
        		id = ((BigInteger)dividendMap.get("id")).longValue() ;
        		savingsId = ((BigInteger)dividendMap.get("savingsAccountId")).longValue() ;
        	}else { //MySQL connector is returning Long
        		id = (Long) dividendMap.get("id") ;
        		savingsId = (Long) dividendMap.get("savingsAccountId") ;
        	}
            try {
                this.shareAccountSchedularService.postDividend(id, savingsId);
            } catch (final PlatformApiDataValidationException e) {
                final List<ApiParameterError> errors = e.getErrors();
                for (final ApiParameterError error : errors) {
                    logger.error("Post Dividends to savings failed for Divident detail Id:" + id + " and savings Id: " + savingsId
                            + " with message " + error.getDeveloperMessage());
                    errorMsg.append("Post Dividends to savings failed for Divident detail Id:").append(id).append(" and savings Id:")
                            .append(savingsId).append(" with message ").append(error.getDeveloperMessage());
                }
            } catch (final Exception e) {
                logger.error("Post Dividends to savings failed for Divident detail Id:" + id + " and savings Id: " + savingsId
                        + " with message " + e.getLocalizedMessage());
                errorMsg.append("Post Dividends to savings failed for Divident detail Id:").append(id).append(" and savings Id:")
                        .append(savingsId).append(" with message ").append(e.getLocalizedMessage());
            }
        }

        if (errorMsg.length() > 0) { throw new JobExecutionException(errorMsg.toString()); }
    }

    @CronTarget(jobName = JobName.UPDATE_TRAIL_BALANCE_DETAILS)
    public void updateTrialBalanceDetails() throws JobExecutionException {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSourceServiceFactory.determineDataSourceService().retrieveDataSource());
        final StringBuilder tbGapSqlBuilder = new StringBuilder(500);
        tbGapSqlBuilder.append("select distinct(je.transaction_date) ")
                .append("from acc_gl_journal_entry je ")
                .append("where je.transaction_date > (select IFNULL(MAX(created_date),'2010-01-01') from m_trial_balance)");

        final List<Date> tbGaps = jdbcTemplate.queryForList(tbGapSqlBuilder.toString(), Date.class);

        for(Date tbGap : tbGaps) {
            LocalDate convDate = new DateTime(tbGap).toLocalDate();
            int days = Days.daysBetween(convDate, DateUtils.getLocalDateOfTenant()).getDays();
            if(days < 1)
                continue;
            final String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(tbGap);
            final StringBuilder sqlBuilder = new StringBuilder(600);
            sqlBuilder.append("Insert Into m_trial_balance(office_id, account_id, Amount, entry_date, created_date,closing_balance) ")
                    .append("Select je.office_id, je.account_id, sum(if(je.type_enum=1, (-1) * je.amount, je.amount)) ")
                    .append("as Amount, Date(je.entry_date) as 'Entry_Date', je.transaction_date as 'Created_Date',sum(je.amount) as closing_balance ")
                    .append("from acc_gl_journal_entry je WHERE je.transaction_date = ? ")
                    .append("group by je.account_id, je.office_id, je.transaction_date, Date(je.entry_date)");

            final int result = jdbcTemplate.update(sqlBuilder.toString(), new Object[] {
                    formattedDate
            });
            logger.info(ThreadLocalContextUtil.getTenant().getName() + ": Results affected by update: " + result);
        }

        // Updating closing balance
        String distinctOfficeQuery = "select distinct(office_id) from m_trial_balance where closing_balance is null group by office_id";
        final List<Long> officeIds = jdbcTemplate.queryForList(distinctOfficeQuery, new Object[] {}, Long.class);


        for(Long officeId : officeIds) {
            String distinctAccountQuery = "select distinct(account_id) from m_trial_balance where office_id=? and closing_balance is null group by account_id";
            final List<Long> accountIds = jdbcTemplate.queryForList(distinctAccountQuery, new Object[] {officeId}, Long.class);
            for(Long accountId : accountIds) {
                final String closingBalanceQuery = "select closing_balance from m_trial_balance where office_id=? and account_id=? and closing_balance " +
                        "is not null order by created_date desc, entry_date desc limit 1";
                List<BigDecimal> closingBalanceData = jdbcTemplate.queryForList(closingBalanceQuery, new Object[] {officeId, accountId}, BigDecimal.class);
                List<TrialBalance> tbRows = this.trialBalanceRepositoryWrapper.findNewByOfficeAndAccount(officeId, accountId);
                BigDecimal closingBalance = null;
                if(!CollectionUtils.isEmpty(closingBalanceData))
                    closingBalance = closingBalanceData.get(0);
                if(CollectionUtils.isEmpty(closingBalanceData)) {
                    closingBalance = BigDecimal.ZERO;
                    for(TrialBalance row : tbRows) {
                        closingBalance = closingBalance.add(row.getAmount());
                        row.setClosingBalance(closingBalance);
                    }
                } else {
                    for(TrialBalance tbRow : tbRows) {
                        closingBalance = closingBalance.add(tbRow.getAmount());
                        tbRow.setClosingBalance(closingBalance);
                    }
                }
                this.trialBalanceRepositoryWrapper.save(tbRows);
            }
        }

    }
    
	@Override
	@CronTarget(jobName = JobName.GENERATE_LOAN_COLLECTIBILITY)
	public void generateLoanCollectibility() {
		
		final AppUser appUser = this.context.authenticatedUser();
		final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSourceServiceFactory.determineDataSourceService().retrieveDataSource());
		
		Collection<LoanCollectibilityData> loanCollectibilityData = this.loanCollectibilityReadService.retrieveLoanCollectibility();
		
		for(LoanCollectibilityData loanCollect : loanCollectibilityData){
			
			try {
				LoanCollectibility loanCollectibility = this.loanCollectibilityRepositoryWrapper.findOneByLoanIdWithNotFoundDetection(loanCollect.getLoanId());
				
				if (loanCollectibility == null){
					loanCollectibility = new LoanCollectibility();
				}
				
				loanCollectibility.setLoanId(loanCollect.getLoanId());
				loanCollectibility.setNumberOverduesDayAccount(loanCollect.getNumberOverduesDayAccount());
				loanCollectibility.setNumberOverduesDayCif(loanCollect.getNumberOverduesDayCif());
				
				final ProvisioningCriteriaData provisioningCriteriaData = this.provisioningCriteriaReadPlatformService.retrieveProvisioningCriteria(loanCollect.getCriteriaId());
				
				for (ProvisioningCriteriaDefinitionData provisioningCriteriaDefinitionData : provisioningCriteriaData.getDefinitions()){
					if (loanCollect.getNumberOverduesDayAccount() >= provisioningCriteriaDefinitionData.getMinAge() && loanCollect.getNumberOverduesDayAccount() <= provisioningCriteriaDefinitionData.getMaxAge()){
						loanCollectibility.setCollectibilityAccount(provisioningCriteriaDefinitionData.getCategoryId());
						if (provisioningCriteriaDefinitionData.getCategoryId().equals(Long.valueOf(5))){
							if (loanCollectibility.getLossDate() == null){
								loanCollectibility.setLossDate(new Date());
							}
						}
						break;
					} else {
						loanCollectibility.setCollectibilityAccount(5L);
						if (loanCollectibility.getLossDate() == null){
							loanCollectibility.setLossDate(new Date());
						}
					}
				}

				loanCollectibility.setManualCollectibility(loanCollect.getManualCollectibility());
				
				if (loanCollectibility.getSubmittedById() != null){
					loanCollectibility.setUpdatedById(appUser.getId());
					loanCollectibility.setUpdatedOn(new Date());
				} else {
					loanCollectibility.setSubmittedById(appUser.getId());
					loanCollectibility.setSubmittedOn(new Date());
				}
				
				this.loanCollectibilityRepositoryWrapper.save(loanCollectibility);
				
				LoanCollectibilityData loanCollectibilityCifData = this.loanCollectibilityReadService.retrieveLoanCollectibilityCifByClientId(loanCollect.getClientId());
				final String sqlCif = "update m_loan_collectibility lc " + 
						"left join m_loan l on lc.loan_id = l.id " + 
						"left join m_client c on c.id = l.client_id " + 
						"join m_loanproduct_provisioning_mapping lpm on lpm.product_id = l.product_id " + 
						"set collectibility_cif = ? " + 
						"where lc.loan_id = l.id and c.id = ? and lc.collectibility_account is not null and l.loan_status_id = 300 ";
				
				jdbcTemplate.update(sqlCif, loanCollectibilityCifData.getCollectibilityCif(), loanCollect.getClientId());
				
			} catch (final PlatformApiDataValidationException e) {
				final List<ApiParameterError> errors = e.getErrors();
				for (final ApiParameterError error : errors) {
					logger.error("Generate Loan Collectibility failed for account:" + loanCollect.getLoanId() + " with message " + error.getDeveloperMessage());
				}
			}
		}
	}
	
	@Override
	@CronTarget(jobName = JobName.GENERATE_ESTATEMENT)
	public void generateEstatement() throws JobExecutionException {
		Collection<String> listMainSavingsAccountNumber = this.savingsAccountRepository.findMainSavingAccountNumberByStatus(300);
		Collection<String> listSubSavingsAccountNumber = this.savingsAccountRepository.findSubSavingAccountNumberByStatus(300);
		
		LocalDate today = DateUtils.getLocalDateOfTenant();
		LocalDate startDate = today.withDayOfMonth(1);
		LocalDate endDate = today.plusMonths(1).withDayOfMonth(1).minusDays(1);
		String currentMonth = String.valueOf(today.monthOfYear().getAsText());
		String currentYear = String.valueOf(today.getYear());
		StringBuilder errorMsg = new StringBuilder();
		String reportUrl = null;
		String reportNameMainSavings = "eStatement_R.jrxml";
		String reportNameSubSavings = "eStatementSub_R.jrxml";
		
		String outUrl = System.getProperty("user.home") + File.separator + "eStatement" + File.separator + currentMonth + "-" + currentYear;
		File outDir = new File(outUrl);
		outDir.mkdirs();
		
		FineractPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
		final FineractPlatformTenantConnection tenantConnection = tenant.getConnection();
		String tenantUrl = driverConfig.constructProtocol(tenantConnection.getSchemaServer(),
				tenantConnection.getSchemaServerPort(), tenantConnection.getSchemaName());

		String dbUrl = tenantUrl;
		String dbDriver = driverConfig.getDriverClassName();
		String dbUname = tenantConnection.getSchemaUsername();
		String dbPwd = tenantConnection.getSchemaPassword();
		
		// Load the JDBC driver
		Connection conn = null;
		try {
			Class.forName(dbDriver);
			conn = DriverManager.getConnection(dbUrl, dbUname, dbPwd);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		if (tenantConnection.getSchemaServer().contains("localhost") || tenantConnection.getSchemaServer().contains("127.0.0.1")) {
			reportUrl = System.getProperty("user.dir") + File.separator + "fineract-provider" + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "report" + File.separator;
		} else {
			reportUrl = System.getProperty("user.dir") + File.separator + "fineract-provider" + File.separator + "WEB-INF" + File.separator + "classes" + File.separator + "report" + File.separator;
			reportUrl = reportUrl.replace("bin", "webapps");
		}

		for (String accountNumber : listMainSavingsAccountNumber) {
			try {
				logger.debug("Start ...." + accountNumber);
				
				String pdfFileName = outUrl + File.separator + "Main_" + accountNumber + ".pdf";
				String reportMainUrl = reportUrl + reportNameMainSavings;
				
				JasperReport jasperReport = JasperCompileManager.compileReport(reportMainUrl);
				
				// Create arguments
				HashMap<String, Object> hm = new HashMap<String, Object>();
				hm.put("accountNumber", accountNumber);
				hm.put("startDate", startDate.toString());
				hm.put("endDate", endDate.toString());
				hm.put("period", currentMonth + " " + currentYear);

				// Generate jasper print
				JasperPrint jprint = (JasperPrint) JasperFillManager.fillReport(jasperReport, hm, conn);

				// Export pdf file
				JasperExportManager.exportReportToPdfFile(jprint, pdfFileName);

				logger.debug("Done exporting reports to pdf for " + accountNumber);
			} catch (Exception e) {
				errorMsg.append("Error for ").append(accountNumber).append(e.getMessage().toString());
			}
		}
		
		for (String accountNumber : listSubSavingsAccountNumber) {
			try {
				logger.debug("Start ...." + accountNumber);
				
				String pdfFileName = outUrl + File.separator + "Sub_" + accountNumber + ".pdf";
				String reportSubUrl = reportUrl + reportNameSubSavings;
				
				JasperReport jasperReport = JasperCompileManager.compileReport(reportSubUrl);
				
				// Create arguments
				HashMap<String, Object> hm = new HashMap<String, Object>();
				hm.put("accountNumber", accountNumber);
				hm.put("startDate", startDate.toString());
				hm.put("endDate", endDate.toString());
				hm.put("period", currentMonth + " " + currentYear);

				// Generate jasper print
				JasperPrint jprint = (JasperPrint) JasperFillManager.fillReport(jasperReport, hm, conn);

				// Export pdf file
				JasperExportManager.exportReportToPdfFile(jprint, pdfFileName);

				logger.debug("Done exporting reports to pdf for " + accountNumber);
			} catch (Exception e) {
				errorMsg.append("Error for ").append(accountNumber).append(e.getMessage().toString());
			}
		}
		
		if (errorMsg.length() > 0) {
			throw new JobExecutionException(errorMsg.toString());
		}
	}
}
