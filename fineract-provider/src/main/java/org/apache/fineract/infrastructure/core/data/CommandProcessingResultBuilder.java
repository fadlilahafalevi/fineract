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
package org.apache.fineract.infrastructure.core.data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Represents the successful result of an REST API call that results in
 * processing a command.
 */
public class CommandProcessingResultBuilder {

    private Date createdDate;
	private Long commandId;
    private Long officeId;
    private Long groupId;
    private Long clientId;
    private Long loanId;
    private Long savingsId;
    private String resourceIdentifier;
    private Long entityId;
    private Long subEntityId;
    private String accountNumber;
    private String transactionId;
    private Map<String, Object> changes;
    private Long productId;
    private boolean rollbackTransaction = false;
    private List<String> transactionIds;
    private Long transactionBatchId;

    public CommandProcessingResult build() {
        return CommandProcessingResult.fromDetails(this.createdDate, this.commandId, this.accountNumber, this.officeId, this.groupId, this.clientId, this.loanId, this.savingsId,
                this.resourceIdentifier, this.entityId, this.transactionId, this.changes, this.productId, this.rollbackTransaction,
                this.subEntityId, this.transactionIds);
    }
    public CommandProcessingResultBuilder withCreatedDate(Date withCreatedDate) {
        this.createdDate = withCreatedDate;
        return this;
    }
    
    public CommandProcessingResultBuilder withAccNo(final String withAccNo) {
        this.accountNumber = withAccNo;
        return this;
    }
    public CommandProcessingResultBuilder withCommandId(final Long withCommandId) {
        this.commandId = withCommandId;
        return this;
    }

    public CommandProcessingResultBuilder with(final Map<String, Object> withChanges) {
        this.changes = withChanges;
        return this;
    }

    public CommandProcessingResultBuilder withResourceIdAsString(final String withResourceIdentifier) {
        this.resourceIdentifier = withResourceIdentifier;
        return this;
    }

    public CommandProcessingResultBuilder withEntityId(final Long withEntityId) {
        this.entityId = withEntityId;
        return this;
    }
    
    public CommandProcessingResultBuilder withSubEntityId(final Long withSubEntityId) {
        this.subEntityId = withSubEntityId;
        return this;
    }

    public CommandProcessingResultBuilder withOfficeId(final Long withOfficeId) {
        this.officeId = withOfficeId;
        return this;
    }

    public CommandProcessingResultBuilder withClientId(final Long withClientId) {
        this.clientId = withClientId;
        return this;
    }

    public CommandProcessingResultBuilder withGroupId(final Long withGroupId) {
        this.groupId = withGroupId;
        return this;
    }

    public CommandProcessingResultBuilder withLoanId(final Long withLoanId) {
        this.loanId = withLoanId;
        return this;
    }

    public CommandProcessingResultBuilder withSavingsId(final Long withSavingsId) {
        this.savingsId = withSavingsId;
        return this;
    }
    
    public CommandProcessingResultBuilder withAccountNumber(final String withAccountNumber) {
        this.accountNumber = withAccountNumber;
        return this;
    }

    public CommandProcessingResultBuilder withTransactionId(final String withTransactionId) {
        this.transactionId = withTransactionId;
        return this;
    }
    
    public CommandProcessingResultBuilder withTransactionIds(final List<String> withTransactionIds) {
        this.transactionIds = withTransactionIds;
        return this;
    }

    public CommandProcessingResultBuilder withProductId(final Long productId) {
        this.productId = productId;
        return this;
    }
    
    public CommandProcessingResultBuilder setRollbackTransaction(final boolean rollbackTransaction) {
        this.rollbackTransaction = this.rollbackTransaction || rollbackTransaction;
        return this;
    }
    
	public Long getTransactionBatchId() {
		return transactionBatchId;
	}
	public void setTransactionBatchId(Long transactionBatchId) {
		this.transactionBatchId = transactionBatchId;
	}
}