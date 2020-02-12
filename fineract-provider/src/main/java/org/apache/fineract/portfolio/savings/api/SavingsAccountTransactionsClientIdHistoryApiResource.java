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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.paymenttype.service.PaymentTypeReadPlatformService;
import org.apache.fineract.portfolio.savings.DepositAccountType;
import org.apache.fineract.portfolio.savings.SavingsApiConstants;
import org.apache.fineract.portfolio.savings.data.SavingsAccountTransactionData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountTransactionHistoryData;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/clients/transactionsHistory")
@Component
@Scope("singleton")
public class SavingsAccountTransactionsClientIdHistoryApiResource {

    private final PlatformSecurityContext context;
    private final DefaultToApiJsonSerializer<SavingsAccountTransactionHistoryData> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final SavingsAccountReadPlatformService savingsAccountReadPlatformService;
    @Autowired
    public SavingsAccountTransactionsClientIdHistoryApiResource(final PlatformSecurityContext context,
            final DefaultToApiJsonSerializer<SavingsAccountTransactionHistoryData> toApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final SavingsAccountReadPlatformService savingsAccountReadPlatformService,
            PaymentTypeReadPlatformService paymentTypeReadPlatformService) {
        this.context = context;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.savingsAccountReadPlatformService = savingsAccountReadPlatformService;
    }

    @SuppressWarnings("null")
	@POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(final String apiRequestBodyAsJson, @Context final UriInfo uriInfo) throws JSONException, ParseException {
    	Long pageSize = null; 
    	Long lastId = null;
    	JSONObject jsonObject = new JSONObject(apiRequestBodyAsJson);
    	String clientId = jsonObject.getString("clientId");
    	String startdate = jsonObject.getString("startDate");
    	String enddate = jsonObject.getString("endDate");
    	String lastid = jsonObject.getString("lastId");
    	String pagesize = jsonObject.getString("pageSize");
    	String transactionTypeValue = jsonObject.getString("transactionTypeValue");
    	Long lastIdReponse = null;
    	//Date startDate=new SimpleDateFormat("yyyy-MM-dd").parse(startdate);
    	//Date endDate=new SimpleDateFormat("yyyy-MM-dd").parse(enddate);
    	
    	if (pagesize.isEmpty()) {
    		pageSize = 15L;
    	} else {
    		pageSize = new Long(pagesize);
    	}
    	
    	
    	if (lastid.isEmpty()) {
    		lastId = null;
    	} else {
    		lastIdReponse = new Long(lastid);
    		lastId = new Long(lastid);
    	}
    	
    	
    	
        this.context.authenticatedUser().validateHasReadPermission(SavingsApiConstants.SAVINGS_ACCOUNT_RESOURCE_NAME);
        
        
        
        Collection<SavingsAccountTransactionData> transactionDataHistory = this.savingsAccountReadPlatformService.retrieveSavingsTransactionsHistoryByClientId(clientId,
        		startdate, enddate, DepositAccountType.SAVINGS_DEPOSIT, lastId, pageSize,transactionTypeValue);
        if (transactionDataHistory != null && (!(transactionDataHistory.isEmpty()))) {
	        List<Long> listTransactionId = new ArrayList<Long>();
	        for (SavingsAccountTransactionData transactionData : transactionDataHistory) {
	        	listTransactionId.add(transactionData.getId());
	        }
	        lastIdReponse = Collections.min(listTransactionId);
        }
        
        SavingsAccountTransactionHistoryData savingsAccountTransactionHistoryData = new SavingsAccountTransactionHistoryData();
        savingsAccountTransactionHistoryData.setLastId(lastIdReponse);
        savingsAccountTransactionHistoryData.setTransactionHistory(transactionDataHistory);
        
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, savingsAccountTransactionHistoryData,
                SavingsApiSetConstants.SAVINGS_TRANSACTION_RESPONSE_DATA_PARAMETERS);
    }
}