package org.apache.fineract.portfolio.savings.data;

import java.util.Collection;

public class SavingsAccountTransactionHistoryData {

	Long lastId;
	Collection<SavingsAccountTransactionData> transactionHistory;
	
	public SavingsAccountTransactionHistoryData() {
		
	}

	public Long getLastId() {
		return lastId;
	}

	public void setLastId(Long lastId) {
		this.lastId = lastId;
	}

	public Collection<SavingsAccountTransactionData> getTransactionHistory() {
		return transactionHistory;
	}

	public void setTransactionHistory(Collection<SavingsAccountTransactionData> transactionHistory) {
		this.transactionHistory = transactionHistory;
	}
	
	
}
