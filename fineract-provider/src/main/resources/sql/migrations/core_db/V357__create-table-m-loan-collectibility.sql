-- Author : fanny agustiawan
-- Table m_loan_collectibility

DROP TABLE IF EXISTS `m_loan_collectibility`;

CREATE TABLE `m_loan_collectibility` (
	`id` bigint(20) NOT NULL AUTO_INCREMENT,
	`loan_id` bigint,
	`collectibility_account` int(11) DEFAULT NULL,
	`collectibility_cif` int(11) DEFAULT NULL,
	`manual_collectibility` int(11) DEFAULT NULL,
	`number_overdues_day_account` int(11) DEFAULT NULL,
	`number_overdues_day_cif` int(11) DEFAULT NULL,
	`submitted_on` datetime DEFAULT NULL,
	`submitted_by` BIGINT,
	`updated_on` datetime DEFAULT NULL,
	`updated_by` BIGINT,
	`delete_on` datetime DEFAULT NULL,
	`delete_by` BIGINT,
	`is_deleted` boolean default false,
	`loss_date` date,
	PRIMARY KEY(`id`)
);

CREATE INDEX `m_loan_collectibility_loan_id_idx` ON `m_loan_collectibility` (`loan_id`);