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
package org.apache.fineract.organisation.provisioning.data;

import java.io.Serializable;
import java.math.BigDecimal;

public final class ProvisioningCriteriaDefinitionData implements Comparable<ProvisioningCriteriaDefinitionData>, Serializable {

    private final Long id;
    private final Long categoryId;
    private final String categoryName;
    private final Long minAge;
    private final Long maxAge;
    private final BigDecimal provisioningPercentage;
    private final Long assetAccount;
    private final String assetCode;
    private final String assetName ;
    private final Long expenseAccount;
    private final String expenseCode;
    private final String expenseName ;
    private final Boolean isNPL;
    
    public ProvisioningCriteriaDefinitionData(Long id, Long categoryId, String categoryName, Long minAge, Long maxAge,
            BigDecimal provisioningPercentage, Long assetAccount, final String assetCode, String assetName, Long expenseAccount, 
            final String expenseCode, final String expenseName, Boolean isNPL) {
        this.id = id;
        this.categoryId = categoryId;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.provisioningPercentage = provisioningPercentage;
        this.assetAccount = assetAccount;
        this.expenseAccount = expenseAccount;
        this.categoryName = categoryName;
        this.assetCode = assetCode;
        this.expenseCode = expenseCode;
        this.assetName = assetName ;
        this.expenseName =  expenseName ;
        this.isNPL = isNPL;
    }

    public static ProvisioningCriteriaDefinitionData template(Long categoryId, String categoryName) {
        return new ProvisioningCriteriaDefinitionData(null, categoryId, categoryName, null, null, null, null, null, null, null, null, null, null);
    }

    public Long getId() {
        return this.id;
    }

    public Long getCategoryId() {
        return this.categoryId;
    }

    public String getCategoryName() {
        return this.categoryName;
    }

    public Long getMinAge() {
        return this.minAge;
    }

    public Long getMaxAge() {
        return this.maxAge;
    }

    public BigDecimal getProvisioningPercentage() {
        return this.provisioningPercentage;
    }

    public Long getExpenseAccount() {
        return this.expenseAccount;
    }

    public String getExpenseCode() {
        return this.expenseCode;
    }

    /**
	 * @return the assetAccount
	 */
	public Long getAssetAccount() {
		return assetAccount;
	}

	/**
	 * @return the assetCode
	 */
	public String getAssetCode() {
		return assetCode;
	}

	/**
	 * @return the assetName
	 */
	public String getAssetName() {
		return assetName;
	}

	/**
	 * @return the expenseName
	 */
	public String getExpenseName() {
		return expenseName;
	}

	/**
	 * @return the isNPL
	 */
	public Boolean getIsNPL() {
		return isNPL;
	}

	@Override
    public int compareTo(ProvisioningCriteriaDefinitionData obj) {
        if (obj == null) { return -1; }
        return obj.id.compareTo(this.id);
    }

}
