-- author : Fadlilah Achmad Falevi

-- add max length account number in some tables

ALTER TABLE `m_client` CHANGE COLUMN `account_no` `account_no` VARCHAR(40);

ALTER TABLE `m_group` CHANGE COLUMN `account_no` `account_no` VARCHAR(40);

ALTER TABLE `m_share_account` CHANGE COLUMN `account_no` `account_no` VARCHAR(70);

ALTER TABLE `m_loan` CHANGE COLUMN `account_no` `account_no` VARCHAR(40);

ALTER TABLE `m_savings_account` CHANGE COLUMN `account_no` `account_no` VARCHAR(40);
