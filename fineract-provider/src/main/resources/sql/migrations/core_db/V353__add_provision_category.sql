-- author : Fanny

-- adding m_provision_category

INSERT INTO m_provision_category (id, category_name, description)
VALUES (5, 'LOSS', 'Principal and/or Interest overdue by y days');

UPDATE m_provision_category
SET category_name = 'SPECIAL MENTION', description = 'Principal and/or Interest overdue by x days'
WHERE id = 2;

UPDATE m_provision_category
SET category_name='SUB-STANDARD', description = 'Principal and/or Interest overdue by x days'
WHERE id = 3;

UPDATE m_provision_category
SET category_name = 'DOUBTFUL', description = 'Principal and/or Interest overdue by x days and less than y'
WHERE id = 4;
