-- Author : afad

INSERT INTO `m_code_value` (`code_id`, `code_value`, `code_description`, `order_position`, `code_score`, `is_active`, `is_mandatory`) 
VALUES ((select id from m_code where code_name = 'TaxThreshold'), '7500000', NULL, '0', NULL, '1', '0');