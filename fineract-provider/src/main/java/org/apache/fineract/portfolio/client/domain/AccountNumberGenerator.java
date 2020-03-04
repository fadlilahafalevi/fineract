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
package org.apache.fineract.portfolio.client.domain;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.accountnumberformat.domain.AccountNumberFormat;
import org.apache.fineract.infrastructure.accountnumberformat.domain.AccountNumberFormatEnumerations.AccountNumberPrefixType;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.service.RoutingDataSourceServiceFactory;
import org.apache.fineract.infrastructure.security.service.RandomPasswordGenerator;
import org.apache.fineract.portfolio.group.domain.Group;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsProductRepository;
import org.apache.fineract.portfolio.savings.service.SavingsProductWritePlatformService;
import org.apache.fineract.portfolio.shareaccounts.domain.ShareAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;

/**
 * Example {@link AccountNumberGenerator} for clients that takes an entities
 * auto generated database id and zero fills it ensuring the identifier is
 * always of a given <code>maxLength</code>.
 */
@Component
public class AccountNumberGenerator {
	private SavingsProductWritePlatformService savingsProductWritePlatformService;
	private final RoutingDataSourceServiceFactory dataSourceServiceFactory;
	
	@Autowired
	public AccountNumberGenerator(final SavingsProductWritePlatformService savingsProductWritePlatformService,
			final RoutingDataSourceServiceFactory dataSourceServiceFactory) {
		this.savingsProductWritePlatformService = savingsProductWritePlatformService;
		this.dataSourceServiceFactory = dataSourceServiceFactory;
	}

    private final static int maxLength = 9;
    private final static int maxLengthDeposito = 12;

    private final static String ID = "id";
    private final static String CLIENT_TYPE = "clientType";
    private final static String OFFICE_NAME = "officeName";
    private final static String LOAN_PRODUCT_SHORT_NAME = "loanProductShortName";
    private final static String SAVINGS_PRODUCT_SHORT_NAME = "savingsProductShortName";
    private final static String SHARE_PRODUCT_SHORT_NAME = "sharesProductShortName" ;
    private final static String LAST_SEQUENCE = "lastSequence";
    private final static String IS_SAVINGS_ACCOUNT = "isSavingsAccount";
    
    public String generate(Client client, AccountNumberFormat accountNumberFormat) {
        Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put(ID, client.getId().toString());
        propertyMap.put(OFFICE_NAME, client.getOffice().getName());
        CodeValue clientType = client.clientType();
        if (clientType != null) {
            propertyMap.put(CLIENT_TYPE, clientType.label());
        }
        return generateAccountNumber(propertyMap, accountNumberFormat);
    }

    public String generate(Loan loan, AccountNumberFormat accountNumberFormat) {
        Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put(ID, loan.getId().toString());
        propertyMap.put(OFFICE_NAME, loan.getOffice().getName());
        propertyMap.put(LOAN_PRODUCT_SHORT_NAME, loan.loanProduct().getShortName());
        return generateAccountNumber(propertyMap, accountNumberFormat);
    }

    public String generate(SavingsAccount savingsAccount, AccountNumberFormat accountNumberFormat) {
        Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put(ID, savingsAccount.getId().toString());
        propertyMap.put(OFFICE_NAME, savingsAccount.office().getName());
        propertyMap.put(SAVINGS_PRODUCT_SHORT_NAME, savingsAccount.savingsProduct().getShortName());
		/*
		 * Integer lastSequence =
		 * savingsAccount.savingsProduct().getLastSequenceAccountNumber(); if
		 * ((lastSequence != null) && lastSequence >= 0) { lastSequence++; } else {
		 * lastSequence = 1; }
		 * savingsAccount.savingsProduct().setLastSequenceAccountNumber(lastSequence);
		 * this.savingsProductRepository.saveAndFlush(savingsAccount.savingsProduct());
		 */
        
        String accountNumber = null;
        
        try {
        	accountNumber = assembleAccountNumber(savingsAccount.savingsProduct().getShortName(), accountNumberFormat, propertyMap, savingsAccount.depositAccountType().isFixedDeposit());
        } catch (SQLException e) {
        	e.printStackTrace();
        }
        
        return accountNumber;
    }

	public String assembleAccountNumber(String shortProductName, AccountNumberFormat accountNumberFormat,
			Map<String, String> propertyMap, Boolean isDepositoAccount) throws SQLException {
		final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSourceServiceFactory.determineDataSourceService().retrieveDataSource());
        
    	final Connection connection = DataSourceUtils.getConnection(this.dataSourceServiceFactory.determineDataSourceService().retrieveDataSource());
    	DatabaseMetaData md = connection.getMetaData();
    	ResultSet rs = md.getTables(null, null, "s_sp_" + shortProductName, null);
    	
    	if (!rs.next()) {
    		this.savingsProductWritePlatformService.createTableForSequenceAccountNumber(shortProductName);
    	}
    	
        final StringBuilder insertSqlBuilder = new StringBuilder(900);
        insertSqlBuilder.append("INSERT INTO s_sp_").append(shortProductName).append(" (`account_number`) VALUES ('");
        insertSqlBuilder.append(new RandomPasswordGenerator(19).generate()).append("')");
        jdbcTemplate.update(insertSqlBuilder.toString());
    	
    	final Long autoIncrement = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        propertyMap.put(LAST_SEQUENCE, String.valueOf(autoIncrement));
        
        int maxLengthSequence = AccountNumberGenerator.maxLength;
        if (isDepositoAccount) {
        	maxLengthSequence = AccountNumberGenerator.maxLengthDeposito;
        }
        
        String accountNumberGenerated = generateSavingsAccountNumber(propertyMap, accountNumberFormat, maxLengthSequence);
        
        final StringBuilder updateSqlBuilder = new StringBuilder(900);
        updateSqlBuilder.append("UPDATE s_sp_").append(shortProductName);
        updateSqlBuilder.append(" SET `account_number` = '").append(accountNumberGenerated).append("'");
        updateSqlBuilder.append(" WHERE `id` = ").append(autoIncrement);
        
        jdbcTemplate.update(updateSqlBuilder.toString());

		return accountNumberGenerated;
	}

    public String generate(ShareAccount shareaccount, AccountNumberFormat accountNumberFormat) {
    	Map<String, String> propertyMap = new HashMap<>();
    	propertyMap.put(ID, shareaccount.getId().toString());
    	propertyMap.put(SHARE_PRODUCT_SHORT_NAME, shareaccount.getShareProduct().getShortName());
    	return generateAccountNumber(propertyMap, accountNumberFormat) ;
    }
    
    private String generateAccountNumber(Map<String, String> propertyMap, AccountNumberFormat accountNumberFormat) {
        String accountNumber = StringUtils.leftPad(propertyMap.get(ID), AccountNumberGenerator.maxLength, '0');
        if (accountNumberFormat != null && accountNumberFormat.getPrefixEnum() != null) {
            AccountNumberPrefixType accountNumberPrefixType = AccountNumberPrefixType.fromInt(accountNumberFormat.getPrefixEnum());
            String prefix = null;
            switch (accountNumberPrefixType) {
                case CLIENT_TYPE:
                    prefix = propertyMap.get(CLIENT_TYPE);
                break;

                case OFFICE_NAME:
                    prefix = propertyMap.get(OFFICE_NAME);
                break;

                case LOAN_PRODUCT_SHORT_NAME:
                    prefix = propertyMap.get(LOAN_PRODUCT_SHORT_NAME);
                break;

                case SAVINGS_PRODUCT_SHORT_NAME:
                    prefix = propertyMap.get(SAVINGS_PRODUCT_SHORT_NAME);
                break;

                default:
                break;

            }

            // FINERACT-590
            // Because account_no is limited to 40 chars, we can only use the first 25 chars of prefix - trim if necessary
            if (prefix != null) {
                prefix = prefix.substring(0, Math.min(prefix.length(), 25));
            }

            accountNumber = StringUtils.overlay(accountNumber, prefix, 0, 0);
        }
        return accountNumber;
    }
    
    private String generateSavingsAccountNumber(Map<String, String> propertyMap, AccountNumberFormat accountNumberFormat, int maxLengthSequence) {
    	String shortName = propertyMap.get(SAVINGS_PRODUCT_SHORT_NAME);
    	if (!StringUtils.isNumeric(shortName)) {
    		throw new PlatformApiDataValidationException("error.msg.short.product.name.must.numeric", "Short Product Name must numeric", null);
    	}
    	String accountNumber = propertyMap.get(SAVINGS_PRODUCT_SHORT_NAME) + StringUtils.leftPad(propertyMap.get(LAST_SEQUENCE), maxLengthSequence, '0');
    	accountNumber = accountNumber + generateCheckDigit(accountNumber);
    	return accountNumber;
    }
    
    private String generateCheckDigit(String param) {
		int[] arrWeightDigit = {5,3,2,7,5,3,2,7,5,3,2,7,5,3,2,7,5,3,2,7,5,3,2,7};
		String[] arrCheckDigit = param.split("(?!^)");
		int[] arrConvertCheckDigit = parseIntArray(arrCheckDigit);
		int result = 0;
		int dump = 0;
		
		for (int i = 0; i < arrConvertCheckDigit.length; i++){
			dump = arrWeightDigit[i] * arrConvertCheckDigit[i];
			result += dump;
		}
		
		result = result % 10;
		return String.valueOf(result);
	}
    
    private static int[] parseIntArray(String[] arr){
		return Stream.of(arr).mapToInt(Integer::parseInt).toArray();
    }
    
    public String generateGroupAccountNumber(Group group, AccountNumberFormat accountNumberFormat) {
    	Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put(ID, group.getId().toString());
        propertyMap.put(OFFICE_NAME, group.getOffice().getName());        
        return generateAccountNumber(propertyMap, accountNumberFormat);
    }
    
    public String generateCenterAccountNumber(Group group, AccountNumberFormat accountNumberFormat) {
    	Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put(ID, group.getId().toString());
        propertyMap.put(OFFICE_NAME, group.getOffice().getName());        
        return generateAccountNumber(propertyMap, accountNumberFormat);
    }


}