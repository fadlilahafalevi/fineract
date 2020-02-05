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
package org.apache.fineract.portfolio.savings.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

@Entity
@Table(name = "m_savings_summary_tax")
public class SavingsSummaryTax extends AbstractPersistableCustom<Long> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "client_account_no", length = 40)
    private String clientAccountNumber;

    @Column(name = "total_balance", scale = 6, precision = 19, nullable = false)
    private BigDecimal totalBalance;

    @Column(name = "date")
    private Date date;

    @Column(name = "is_tax_applicable")
    private Boolean isTaxApplicable;
    
    public SavingsSummaryTax() {
    	
    }

	public String getClientAccountNumber() {
		return clientAccountNumber;
	}

	public void setClientAccountNumber(String clientAccountNumber) {
		this.clientAccountNumber = clientAccountNumber;
	}

	public BigDecimal getTotalBalance() {
		return totalBalance;
	}

	public void setTotalBalance(BigDecimal totalBalance) {
		this.totalBalance = totalBalance;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Boolean getIsTaxApplicable() {
		return isTaxApplicable;
	}

	public void setIsTaxApplicable(Boolean isTaxApplicable) {
		this.isTaxApplicable = isTaxApplicable;
	}
}
