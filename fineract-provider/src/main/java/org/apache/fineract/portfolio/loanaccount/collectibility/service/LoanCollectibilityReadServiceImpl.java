package org.apache.fineract.portfolio.loanaccount.collectibility.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.loanaccount.collectibility.data.LoanCollectibilityData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class LoanCollectibilityReadServiceImpl implements LoanCollectibilityReadService {

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;

	@Autowired
	public LoanCollectibilityReadServiceImpl(final RoutingDataSource dataSource,
			final PlatformSecurityContext context) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.context = context;
	}

	@Override
	public Collection<LoanCollectibilityData> retrieveLoanCollectibility() {

		LoanCollectibilityEntryMapper mapper = new LoanCollectibilityEntryMapper();
		final String sql = mapper.schema();
		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
	}
	
	@Override
	public LoanCollectibilityData retrieveLoanCollectibilityByLoanId(Long loanId) {
		LoanCollectibilityEntryMapper mapper = new LoanCollectibilityEntryMapper();
		final String sql = mapper.schema() + " and l.id = ? ";
		return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] {loanId});
	}
	
	@Override
	public LoanCollectibilityData retrieveLoanCollectibilityCifByClientId(Long clientId) {
		LoanCollectibilityCifEntryMapper mapper = new LoanCollectibilityCifEntryMapper();
		final String sql = mapper.schema();
		return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] {clientId});
	}

	private static final class LoanCollectibilityEntryMapper implements RowMapper<LoanCollectibilityData> {

		private final StringBuilder sqlQuery;

		protected LoanCollectibilityEntryMapper() {
			sqlQuery = new StringBuilder()
					.append("select distinct l.id as loanId, l.product_id as productId, l.client_id as clientId, l.group_id as groupId, pcd.criteria_id as criteriaId, GREATEST(datediff(sch.duedate, now()),0) as numberoverduesaccount, Greatest(T1.numberoverduescif,T2.numberoverduescif) numberoverduescif, now() > l.expected_maturedon_date as ismatured ")
					.append("from m_loan l ")
					.append("join m_loan_repayment_schedule sch on sch.loan_id=l.id ")
					.append("join m_loanproduct_provisioning_mapping lpm on lpm.product_id = l.product_id ")
					.append("join m_provisioning_criteria_definition pcd on pcd.criteria_id = lpm.criteria_id ")
					.append("left join (SELECT lo.client_id, Greatest(Datediff(Min(sch1.duedate), now()), 0) AS numberoverduescif FROM m_loan_repayment_schedule sch1 left join m_loan lo ON lo.id = sch1.loan_id join m_loanproduct_provisioning_mapping lpm ON lpm.product_id = lo.product_id WHERE sch1.completed_derived = false AND lo.loan_status_id = 300 group by lo.client_id) T1 on T1.client_id = l.client_id ")
					.append("left join (SELECT lo.group_id, Greatest(Datediff(Min(sch1.duedate), now()), 0) AS numberoverduescif FROM m_loan_repayment_schedule sch1 left join m_loan lo ON lo.id = sch1.loan_id join m_loanproduct_provisioning_mapping lpm ON lpm.product_id = lo.product_id WHERE sch1.completed_derived = false AND lo.loan_status_id = 300 group by lo.group_id) T2 on T2.group_id = l.group_id ")
					.append("where l.loan_status_id=300 and sch.duedate = (select MIN(sch1.duedate) from m_loan_repayment_schedule sch1 where sch1.loan_id=l.id and sch1.completed_derived=false) ");
		}

		@Override
		@SuppressWarnings("unused")
		public LoanCollectibilityData mapRow(ResultSet rs, int rowNum) throws SQLException {
			Long loanId = rs.getLong("loanId");
			Long productId = rs.getLong("productId");
			Long clientId = rs.getLong("clientId");
			Long groupId = rs.getLong("groupId");
			Long criteriaId = rs.getLong("criteriaId");
			Long numberOverduesAccount = rs.getLong("numberoverduesaccount");
			Long numberOverduesCif = rs.getLong("numberoverduescif");
			Boolean isMatured = rs.getBoolean("ismatured");

			LoanCollectibilityData loanCollectibilityData = new LoanCollectibilityData();
			loanCollectibilityData.setLoanId(loanId);
			loanCollectibilityData.setNumberOverduesDayAccount(numberOverduesAccount);
			loanCollectibilityData.setNumberOverduesDayCif(numberOverduesCif);
			loanCollectibilityData.setCriteriaId(criteriaId);
			loanCollectibilityData.setClientId(clientId);
			loanCollectibilityData.setIsMatured(isMatured);
			return loanCollectibilityData;
		}

		public String schema() {
			return sqlQuery.toString();
		}
	}
	
	private static final class LoanCollectibilityCifEntryMapper implements RowMapper<LoanCollectibilityData> {

		private final StringBuilder sqlQuery;

		protected LoanCollectibilityCifEntryMapper() {
			sqlQuery = new StringBuilder()
					.append("select max(collect.collectibilityCif) as collectibilityCif from (")
					.append("select GREATEST(lc.collectibility_account, IFNULL(lc.manual_collectibility,0)) as collectibilityCif ")
					.append("from m_loan_collectibility lc  ")
					.append("left join m_loan l on lc.loan_id = l.id ")
					.append("join m_loanproduct_provisioning_mapping lpm on lpm.product_id = l.product_id ")
					.append("left join m_client c on c.id = l.client_id ")
					.append("where l.loan_status_id = 300 and c.id = ? group by c.id, lc.collectibility_account, lc.manual_collectibility order by c.id ) collect");
		}

		@Override
		@SuppressWarnings("unused")
		public LoanCollectibilityData mapRow(ResultSet rs, int rowNum) throws SQLException {
			Long collectibilityCif = rs.getLong("collectibilityCif");

			LoanCollectibilityData loanCollectibilityData = new LoanCollectibilityData();
			loanCollectibilityData.setCollectibilityCif(collectibilityCif);
			return loanCollectibilityData;
		}

		public String schema() {
			return sqlQuery.toString();
		}
	}
}
