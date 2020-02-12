alter table `acc_gl_journal_entry`
	change `organization_running_balance` `overdraft_amount_derived` decimal(40, 6) null default null,
	change `office_running_balance` `running_balance_derived` decimal(40, 6) null default null,
	change `amount` `amount` decimal(40, 6) null default null;
