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
package org.apache.fineract.portfolio.savings.api;

import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.exception.UnrecognizedQueryParamException;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.paymenttype.service.PaymentTypeReadPlatformService;
import org.apache.fineract.portfolio.savings.SavingsApiConstants;
import org.apache.fineract.portfolio.savings.data.SavingsAccountTransactionData;
import org.apache.fineract.portfolio.savings.exception.SavingsAccountNumberNotFoundException;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

@Path("/savingsaccounts/batchtrx")
@Component
@Scope("singleton")
public class SavingsAccountTransactionsBatchApiResource {

    private final DefaultToApiJsonSerializer<SavingsAccountTransactionData> toApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final SavingsAccountReadPlatformService savingsAccountReadPlatformService;

    @Autowired
    public SavingsAccountTransactionsBatchApiResource(final PlatformSecurityContext context,
            final DefaultToApiJsonSerializer<SavingsAccountTransactionData> toApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final SavingsAccountReadPlatformService savingsAccountReadPlatformService,
            PaymentTypeReadPlatformService paymentTypeReadPlatformService) {
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.savingsAccountReadPlatformService = savingsAccountReadPlatformService;
    }

    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }

    
  //################################################################################################################################
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String transaction(@QueryParam("command") final String commandParam,
            final String apiRequestBodyAsJson, @Context final HttpHeaders requestHeader) throws JSONException {
        try {
            /*String clientAccountNumber = entityHeader;*/
        	JSONObject jsonObject = new JSONObject(apiRequestBodyAsJson);
        	JSONArray jsonArray =  jsonObject.getJSONArray("batch");
        	String accountNumber = jsonObject.getString("accountNo");
        	Long savingsId = this.savingsAccountReadPlatformService.retrieveSavingsIdByAccountNumber(accountNumber);     

        	String clientAccountNumberHeader = requestHeader.getRequestHeaders().getFirst("client-account-number");
        	String clientAccountNumber = this.savingsAccountReadPlatformService.retrieveClientsAccountNumberBySavingsId(savingsId);
        	if (!(clientAccountNumberHeader.equals(clientAccountNumber))) {
        		throw new SavingsAccountNumberNotFoundException(accountNumber);
        	}
        			
        	Collection<CommandProcessingResult> resultArray = new ArrayList<CommandProcessingResult>();
        	for (int i = 0; i < jsonArray.length(); i++) {
                String apiRequestBodyAsJsonArray = jsonArray.getJSONObject(i).toString();
                
            final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJsonArray);

            CommandProcessingResult result = null;
            if (is(commandParam, "deposit")) {
                final CommandWrapper commandRequest = builder.savingsAccountDeposit(savingsId).build();
                result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
            } else if (is(commandParam, "withdrawal")) {
                final CommandWrapper commandRequest = builder.savingsAccountWithdrawal(savingsId).build();
                result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
            } else if (is(commandParam, "deposit2")) {
                final CommandWrapper commandRequest = builder.savingsAccountDeposit2(savingsId).build();
                result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
            } else if (is(commandParam, "withdrawal2")) {
                final CommandWrapper commandRequest = builder.savingsAccountWithdrawal2(savingsId).build();
                result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
            } else if (is(commandParam, "postInterestAsOn")) {
                final CommandWrapper commandRequest = builder.savingsAccountInterestPosting(savingsId).build();
                result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
            } else if (is(commandParam, SavingsApiConstants.COMMAND_HOLD_AMOUNT)) {
                final CommandWrapper commandRequest = builder.holdAmount(savingsId).build();
                result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
            }

            if (result == null) {
                //
                throw new UnrecognizedQueryParamException("command", commandParam, new Object[] { "deposit", "withdrawal", SavingsApiConstants.COMMAND_HOLD_AMOUNT });
            }
                       
            resultArray.add(result);
        	}
            return this.toApiJsonSerializer.serialize(resultArray);
        	
        } catch (ObjectOptimisticLockingFailureException lockingFailureException) {
            throw new PlatformDataIntegrityException("error.msg.savings.concurrent.operations",
                    "Concurrent Transactions being made on this savings account: " + lockingFailureException.getMessage());
        } catch (CannotAcquireLockException cannotAcquireLockException) {
            throw new PlatformDataIntegrityException("error.msg.savings.concurrent.operations.unable.to.acquire.lock",
                    "Unable to acquir lock for this transaction: " + cannotAcquireLockException.getMessage());
        }
    }
    
  //################################################################################################################################
}