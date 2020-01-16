-- Author : fanny.raharjo
DELETE FROM `m_permission` WHERE `code`='CREATE_CLIENT2';

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) 
VALUES ('portfolio', 'CREATE_CLIENT2', 'CLIENT2', 'CREATE', 0);