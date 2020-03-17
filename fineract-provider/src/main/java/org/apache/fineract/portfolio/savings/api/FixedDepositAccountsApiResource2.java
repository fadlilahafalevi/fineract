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

import io.swagger.annotations.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.JsonQuery;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.exception.UnrecognizedQueryParamException;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.portfolio.savings.DepositAccountType;
import org.apache.fineract.portfolio.savings.DepositsApiConstants;
import org.apache.fineract.portfolio.savings.data.DepositAccountData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;


@Path("/fixeddepositaccounts2")
@Component
@Scope("singleton")
@Api(value = "Fixed Deposit Account", description = "Fixed Deposit accounts are instances of a praticular fixed deposit product created. An application process around the creation of accounts is also supported.")
public class FixedDepositAccountsApiResource2 {

    private final DefaultToApiJsonSerializer<DepositAccountData> toApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;


    @Autowired
    public FixedDepositAccountsApiResource2(final DefaultToApiJsonSerializer<DepositAccountData> toApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService
            ) {
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @ApiOperation(value = "Submit new fixed deposit application", httpMethod = "POST", notes = "Submits a new fixed deposit application"+"Mandatory Fields: clientId or groupId, productId, submittedOnDate, depositAmount, depositPeriod, depositPeriodFrequencyId\n\n" + "Optional Fields: accountNo, externalId, fieldOfficerId,linkAccountId(if provided initial deposit amount will be collected from this account),transferInterestToSavings(By enabling this flag all interest postings will be transferred to linked saving account )")
    @ApiImplicitParams({@ApiImplicitParam(value = "body", required = true, paramType = "body", dataType = "body", format = "body", dataTypeClass = FixedDepositAccountsApiResourceSwagger.PostFixedDepositAccountsRequest.class)})
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = FixedDepositAccountsApiResourceSwagger.PostFixedDepositAccountsResponse.class)})
    public String submitApplication(@ApiParam(hidden = true) final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createFixedDepositAccount2().withJson(apiRequestBodyAsJson)
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }
    
    @POST
    @Path("{accountNo}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @ApiOperation(value = "Approve fixed deposit application | Undo approval fixed deposit application | Reject fixed deposit application | Withdraw fixed deposit application | Activate a fixed deposit account | Close a fixed deposit account | Premature Close a fixed deposit account | Calculate Premature amount on Fixed deposit account | Calculate Interest on Fixed Deposit Account | Post Interest on Fixed Deposit Account", httpMethod = "POST", notes = "Approve fixed deposit application:\n\n" + "Approves fixed deposit application so long as its in 'Submitted and pending approval' state.\n\n" + "Undo approval fixed deposit application:\n\n" + "Will move 'approved' fixed deposit application back to 'Submitted and pending approval' state.\n\n" + "Reject fixed deposit application:\n\n" + "Rejects fixed deposit application so long as its in 'Submitted and pending approval' state.\n\n" + "Withdraw fixed deposit application:\n\n" + "Used when an applicant withdraws from the fixed deposit application. It must be in 'Submitted and pending approval' state.\n\n" + "Close a fixed deposit account:\n\n" + "Results in a Matured fixed deposit account being converted into a 'closed' fixed deposit account.\n\n" + "Premature Close a fixed deposit account:\n\n" + "Results in an Active fixed deposit account being converted into a 'Premature Closed' fixed deposit account with options to withdraw prematured amount. (premature amount is calculated using interest rate chart applicable along with penal interest if any.)\n\n" + "Calculate Premature amount on Fixed deposit account:\n\n" + "Calculate premature amount on fixed deposit account till premature close date. Premature amount is calculated based on interest chart and penal interest applicable.\n\n" + "Calculate Interest on Fixed Deposit Account:\n\n" + "Calculates interest earned on a fixed deposit account based on todays date. It does not attempt to post or credit the interest on the account. That is responsibility of the Post Interest API that will likely be called by overnight process.\n\n" + "Post Interest on Fixed Deposit Account:\n\n" + "Calculates and Posts interest earned on a fixed deposit account based on today's date and whether an interest posting or crediting event is due.\n\n" + "Showing request/response for Calculate Interest on Fixed Deposit Account")
    @ApiImplicitParams({@ApiImplicitParam(value = "body", required = true, paramType = "body", dataType = "body", format = "body", dataTypeClass = FixedDepositAccountsApiResourceSwagger.PostFixedDepositAccountsAccountIdRequest.class)})
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = FixedDepositAccountsApiResourceSwagger.PostFixedDepositAccountsAccountIdResponse.class)})
    public String handleCommands(@PathParam("accountNo") @ApiParam(value = "accountNo") final String accountNo, @QueryParam("command") @ApiParam(value = "command") final String commandParam,
            @Context final UriInfo uriInfo, @ApiParam(hidden = true) final String apiRequestBodyAsJson) {

        String jsonApiRequest = apiRequestBodyAsJson;
        if (StringUtils.isBlank(jsonApiRequest)) {
            jsonApiRequest = "{}";
        }

        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(jsonApiRequest);

        CommandProcessingResult result = null;
        if (is(commandParam, "close")) {
            final CommandWrapper commandRequest = builder.closeFixedDepositAccount(accountNo).buildWithAccountNumber();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        }

        if (result == null) { throw new UnrecognizedQueryParamException("command", commandParam, new Object[] { "reject",
                "withdrawnByApplicant", "approve", "undoapproval", "activate", "calculateInterest", "postInterest", "close",
                "prematureClose", "calculatePrematureAmount" }); }

        return this.toApiJsonSerializer.serialize(result);
    }

    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }
}
