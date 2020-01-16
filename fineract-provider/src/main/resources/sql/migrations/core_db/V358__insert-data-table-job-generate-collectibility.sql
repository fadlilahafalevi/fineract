-- author : fanny agustiawan

DELETE FROM `job` WHERE `name` in ('Generate Loan Collectibility');

INSERT INTO `job` 
(`name`, `display_name`, `cron_expression`, `create_time`, `task_priority`, `group_name`, `previous_run_start_time`, `next_run_time`, `job_key`, `initializing_errorlog`, `is_active`, `currently_running`, `updates_allowed`, `scheduler_group`) 
VALUES ('Generate Loan Collectibility', 'Generate Loan Collectibility', '0 59 23 1/1 * ? *', now(), 4, NULL, NULL, NULL, 'Generate Loan Collectibility __ Default', NULL, 1, 0, 1, 0);
