-- Author : fanny.raharjo
DELETE FROM `m_permission` WHERE `code`='CLOSE_FIXEDDEPOSITACCOUNT2';

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) 
VALUES ('transaction_savings', 'CLOSE_FIXEDDEPOSITACCOUNT2', 'FIXEDDEPOSITACCOUNT2', 'CLOSE', 0);