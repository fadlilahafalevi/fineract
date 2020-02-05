-- Author : fanny.raharjo

ALTER TABLE `m_provisioning_criteria_definition` ADD `is_npl` BOOLEAN;
ALTER TABLE `m_loan` ADD `accrual_amount` NUMERIC(19,6);
ALTER TABLE `m_loan` ADD `accrual_type` INTEGER;