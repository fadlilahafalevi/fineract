-- Author : afad
insert
	into
		`job` (`id`,
		`name`,
		`display_name`,
		`cron_expression`,
		`create_time`,
		`task_priority`,
		`group_name`,
		`previous_run_start_time`,
		`next_run_time`,
		`job_key`,
		`initializing_errorlog`,
		`is_active`,
		`currently_running`,
		`updates_allowed`,
		`scheduler_group`,
		`is_misfired`)
	values (null,
	'Generate eStatement',
	'Generate eStatement',
	'0 50 22 1/1 * ? *',
	current_date(),
	'1',
	null,
	null,
	null,
	'Generate eStatementJobDetail1 _ DEFAULT',
	null,
	'1',
	'0',
	'1',
	'0',
	'0')