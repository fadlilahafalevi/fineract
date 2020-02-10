create
	table
		`m_savings_summary_tax` ( 
		`id` bigint(50) not null AUTO_INCREMENT ,
		`client_account_no` varchar(40) not null ,
		`date` DATE NOT NULL,
		`total_balance` decimal(19,6) not null ,
		`is_tax_applicable` boolean not null ,
		primary key (`id`) );