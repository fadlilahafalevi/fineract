-- Author : fanny
ALTER TABLE `m_savings_account` DROP `payment_type_deposito_withdraw`;
ALTER TABLE `m_savings_account` ADD `payment_type_deposito_withdraw` INT NULL;