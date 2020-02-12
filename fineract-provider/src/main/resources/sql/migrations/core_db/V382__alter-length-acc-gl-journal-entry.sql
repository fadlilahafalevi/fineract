alter table `acc_gl_journal_entry`
	change `organization_running_balance` `organization_running_balance` decimal(40, 6) null default null,
	change `office_running_balance` `office_running_balance` decimal(40, 6) null default null,
	change `amount` `amount` decimal(40, 6) null default null;
