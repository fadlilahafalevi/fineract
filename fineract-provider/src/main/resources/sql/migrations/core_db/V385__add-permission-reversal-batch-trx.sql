DELETE FROM `m_permission` WHERE code in ('UNDOTRANSACTIONBATCHTRX_SAVINGSACCOUNT');

INSERT INTO `m_permission` (`id`, `grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES
(NULL, 'portfolio', 'UNDOTRANSACTIONBATCHTRX_SAVINGSACCOUNT', 'SAVINGSACCOUNT', 'UNDOTRANSACTIONBATCHTRX', '1');