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
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.exception.UnrecognizedQueryParamException;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.portfolio.savings.SavingsApiConstants;
import org.apache.fineract.portfolio.savings.data.SavingsAccountData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/savingsaccounts2")
@Component
@Scope("singleton")
@Api(value = "Savings Account", description = "Savings accounts are instances of a particular savings product created for an individual or group. An application process around the creation of accounts is also supported.")
public class SavingsAccountsApiResource2 {

    private final DefaultToApiJsonSerializer<SavingsAccountData> toApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    @Autowired
    public SavingsAccountsApiResource2(final DefaultToApiJsonSerializer<SavingsAccountData> toApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService
            ) {
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
    }

    

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @ApiOperation(value = "Submit new savings application", httpMethod = "POST", notes = "Submits new savings application\n\n" + "Mandatory Fields: clientId or groupId, productId, submittedOnDate\n\n" + "Optional Fields: accountNo, externalId, fieldOfficerId\n\n" + "Inherited from Product (if not provided): nominalAnnualInterestRate, interestCompoundingPeriodType, interestCalculationType, interestCalculationDaysInYearType, minRequiredOpeningBalance, lockinPeriodFrequency, lockinPeriodFrequencyType, withdrawalFeeForTransfers, allowOverdraft, overdraftLimit, withHoldTax\n\n" + "Additional Mandatory Field if Entity-Datatable Check is enabled for the entity of type Savings: datatables")
    @ApiImplicitParams({@ApiImplicitParam(value = "body", required = true, paramType = "body", dataType = "body", format = "body", dataTypeClass = SavingsAccountsApiResourceSwagger.PostSavingsAccountsRequest.class)})
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = SavingsAccountsApiResourceSwagger.PostSavingsAccountsResponse.class)})
    public String submitApplication(@ApiParam(hidden = true) final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createSavingsAccount2().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }
    
    @POST
    @Path("{accountNumber}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @ApiOperation(value = "Approve savings application | Undo approval savings application | Assign Savings Officer | Unassign Savings Officer | Reject savings application | Withdraw savings application | Activate a savings account | Close a savings account | Calculate Interest on Savings Account | Post Interest on Savings Account | Block Savings Account | Unblock Savings Account | Block Savings Account Credit transactions | Unblock Savings Account Credit transactions | Block Savings Account Debit transactions | Unblock Savings Account debit transactions", httpMethod = "POST", notes = "Approve savings application:\n\n" + "Approves savings application so long as its in 'Submitted and pending approval' state.\n\n" + "Undo approval savings application:\n\n" + "Will move 'approved' savings application back to 'Submitted and pending approval' state.\n\n" + "Assign Savings Officer:\n\n" + "Allows you to assign Savings Officer for existing Savings Account.\n\n" + "Unassign Savings Officer:\n\n" + "Allows you to unassign the Savings Officer.\n\n" + "Reject savings application:\n\n" + "Rejects savings application so long as its in 'Submitted and pending approval' state.\n\n" + "Withdraw savings application:\n\n" + "Used when an applicant withdraws from the savings application. It must be in 'Submitted and pending approval' state.\n\n" + "Activate a savings account:\n\n" + "Results in an approved savings application being converted into an 'active' savings account.\n\n" + "Close a savings account:\n\n" + "Results in an Activated savings application being converted into an 'closed' savings account.\n" + "\n" + "closedOnDate is closure date of savings account\n" + "\n" + "withdrawBalance is a boolean value, true value of this field performs a withdrawal transaction with account's running balance.\n\n" + "Mandatory Fields: dateFormat,locale,closedOnDate\n\n" + "Optional Fields: note, withdrawBalance, paymentTypeId, accountNumber, checkNumber, routingCode, receiptNumber, bankNumber\n\n" + "Calculate Interest on Savings Account:\n\n" + "Calculates interest earned on a savings account based on todays date. It does not attempt to post or credit the interest on the account. That is responsibility of the Post Interest API that will likely be called by overnight process.\n\n" + "Post Interest on Savings Account:\n\n" + "Calculates and Posts interest earned on a savings account based on today's date and whether an interest posting or crediting event is due.\n\n" + "Block Savings Account:\n\n" + "Blocks Savings account from all types of credit and debit transactions\n\n" + "Unblock Savings Account:\n\n" + "Unblock a blocked account. On unblocking account, user can perform debit and credit transactions\n\n" + "Block Savings Account Credit transactions:\n\n" + "Savings account will be blocked from all types of credit transactions.\n\n" + "Unblock Savings Account Credit transactions:\n\n" + "It unblocks the Saving account's credit operations. Now all types of credits can be transacted to Savings account\n\n" + "Block Savings Account Debit transactions:\n\n" + "All types of debit operations from Savings account wil be blocked\n\n" + "Unblock Savings Account debit transactions:\n\n" + "It unblocks the Saving account's debit operations. Now all types of debits can be transacted from Savings account\n\n" + "Showing request/response for 'Unassign Savings Officer'")
    @ApiImplicitParams({@ApiImplicitParam(value = "body", required = true, paramType = "body", dataType = "body", format = "body", dataTypeClass = SavingsAccountsApiResourceSwagger.PostSavingsAccountsAccountIdRequest.class)})
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = SavingsAccountsApiResourceSwagger.PostSavingsAccountsAccountIdResponse.class)})
    public String handleCommands(@PathParam("accountNumber") @ApiParam(value = "accountNumber") final String accountNumber, @QueryParam("command") @ApiParam(value = "command") final String commandParam,
           @ApiParam(hidden = true) final String apiRequestBodyAsJson) {

        String jsonApiRequest = apiRequestBodyAsJson;
        if (StringUtils.isBlank(jsonApiRequest)) {
            jsonApiRequest = "{}";
        }

        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(jsonApiRequest);

        CommandProcessingResult result = null;
        if (is(commandParam, "close")) {
            final CommandWrapper commandRequest = builder.closeSavingsAccountApplication(accountNumber).buildWithAccountNumber();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        }

        if (result == null) {
            //
            throw new UnrecognizedQueryParamException("command", commandParam, new Object[] { "reject", "withdrawnByApplicant", "approve",
                    "undoapproval", "activate", "calculateInterest", "postInterest", "close", "assignSavingsOfficer",
                    "unassignSavingsOfficer", SavingsApiConstants.COMMAND_BLOCK_DEBIT, SavingsApiConstants.COMMAND_UNBLOCK_DEBIT,
                    SavingsApiConstants.COMMAND_BLOCK_CREDIT, SavingsApiConstants.COMMAND_UNBLOCK_CREDIT,
                    SavingsApiConstants.COMMAND_BLOCK_ACCOUNT, SavingsApiConstants.COMMAND_UNBLOCK_ACCOUNT });
        }

        return this.toApiJsonSerializer.serialize(result);
    }
    
    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }
}