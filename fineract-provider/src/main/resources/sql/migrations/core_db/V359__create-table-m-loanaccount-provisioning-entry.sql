-- Author : fanny agustiawan
-- Table m_loanaccount_provisioning_entry

DROP TABLE IF EXISTS `m_loanaccount_provisioning_entry`;

CREATE TABLE `m_loanaccount_provisioning_entry` (
	`id` bigint(20) NOT NULL AUTO_INCREMENT,
	`history_id` bigint,
	`criteria_id` bigint,
	`office_id` bigint,
	`currency_code` character varying(3),
	`loan_id` bigint,
	`category_id` bigint,
	`category_id_by_cif` bigint,
	`overdue_in_days` bigint,
	`reserved_amount` numeric(21,2),
	`old_reserved_amount` numeric(21,2),
	`reserved_amount_by_cif` numeric(21,2),
	`old_reserved_amount_by_cif` numeric(21,2),
	`asset_account` bigint,
	`expense_account` bigint,
	`provision_deduct_amount` numeric(21,2),
	
	PRIMARY KEY (`id`)
);