-- Author : fanny.raharjo
DELETE FROM `m_permission` WHERE `code`='HOLDAMOUNT_SAVINGSACCOUNT2';

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) 
VALUES ('transaction_savings', 'HOLDAMOUNT_SAVINGSACCOUNT2', 'SAVINGSACCOUNT2', 'HOLDAMOUNT', 0);