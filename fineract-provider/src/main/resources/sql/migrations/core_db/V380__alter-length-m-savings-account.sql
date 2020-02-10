alter table `m_savings_account` change 
	`total_deposits_derived` `total_deposits_derived` decimal(40, 6) null default null,
	change `total_withdrawals_derived` `total_withdrawals_derived` decimal(40, 6) null default null,
	change `total_interest_earned_derived` `total_interest_earned_derived` decimal(40, 6) null default null,
	change `account_balance_derived` `account_balance_derived` decimal(40, 	6) not null default '0.000000';