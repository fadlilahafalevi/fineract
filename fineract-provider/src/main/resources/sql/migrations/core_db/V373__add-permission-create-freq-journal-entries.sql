-- Author : fanny.raharjo
DELETE FROM `m_permission` WHERE `code`='CREATE_FREQUENTJOURNALENTRY';

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) 
VALUES ('accounting', 'CREATE_FREQUENTJOURNALENTRY', 'FREQUENTJOURNALENTRY', 'CREATE', 0);