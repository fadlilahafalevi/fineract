-- Author : fanny.raharjo
DELETE FROM `m_permission` WHERE `code`='CREATE_LOAN2';

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) 
VALUES ('portfolio', 'CREATE_LOAN2', 'LOAN2', 'CREATE', 0);