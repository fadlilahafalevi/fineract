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
package org.apache.fineract.portfolio.savings.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.savings.data.SavingsSummaryTaxData;
import org.apache.fineract.portfolio.savings.exception.DepositAccountInterestRateChartNotFoundException;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class SavingsSummaryTaxReadPlatformServiceImpl implements SavingsSummaryTaxReadPlatformService {

	private final PlatformSecurityContext context;
	private final JdbcTemplate jdbcTemplate;
	private final SavingsSummaryTaxMapper chartRowMapper = new SavingsSummaryTaxMapper();

	@Autowired
	public SavingsSummaryTaxReadPlatformServiceImpl(PlatformSecurityContext context,
			final RoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public SavingsSummaryTaxData retrieveOne(Long clientId) {
		try {
			this.context.authenticatedUser();
			final String sql = "select " + this.chartRowMapper.schema() + " where c.id = ? order by sst.id DESC limit 1";
			return this.jdbcTemplate.queryForObject(sql, this.chartRowMapper, new Object[] { clientId });
		} catch (final EmptyResultDataAccessException e) {
			return null;
		}
	}

	public static final class SavingsSummaryTaxMapper implements RowMapper<SavingsSummaryTaxData> {

		private final String schemaSql;

		public String schema() {
			return this.schemaSql;
		}

		private SavingsSummaryTaxMapper() {

			final StringBuilder sqlBuilder = new StringBuilder(400);

			sqlBuilder.append("sst.id as id, sst.client_account_no as clientAccountNumber, ");
			sqlBuilder.append(
					"sst.date as date, sst.total_balance as totalBalance, sst.is_tax_applicable as isTaxApplicable ");
			sqlBuilder.append("from m_savings_summary_tax sst ");
			sqlBuilder.append("left join m_client c on c.account_no = sst.client_account_no ");
			this.schemaSql = sqlBuilder.toString();
		}

		@Override
		public SavingsSummaryTaxData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
			final Long id = rs.getLong("id");
			final String clientAccountNumber = rs.getString("clientAccountNumber");
			final LocalDate date = JdbcSupport.getLocalDate(rs, "date");
			final BigDecimal totalBalance = rs.getBigDecimal("totalBalance");
			final Boolean isTaxApplicable = rs.getBoolean("isTaxApplicable");

			SavingsSummaryTaxData savingsSummaryTaxData = new SavingsSummaryTaxData();
			savingsSummaryTaxData.setId(id);
			savingsSummaryTaxData.setClientAccountNumber(clientAccountNumber);
			savingsSummaryTaxData.setDate(date);
			savingsSummaryTaxData.setTotalBalance(totalBalance);
			savingsSummaryTaxData.setIsTaxApplicable(isTaxApplicable);

			return savingsSummaryTaxData;
		}

	}

}