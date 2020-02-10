alter table `m_savings_account_transaction`
	change `overdraft_amount_derived` `overdraft_amount_derived` decimal(40, 6) null default null,
	change `running_balance_derived` `running_balance_derived` decimal(40, 6) null default null,
	change `amount` `amount` decimal(40, 6) null default null,
	change `cumulative_balance_derived` `cumulative_balance_derived` decimal(40, 6) null default null;