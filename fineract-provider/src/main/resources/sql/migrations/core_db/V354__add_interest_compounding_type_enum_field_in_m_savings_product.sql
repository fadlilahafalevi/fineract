-- author : Fadlilah

-- adding interest_compounding_type_enum

ALTER TABLE `m_savings_product` DROP IF EXISTS `interest_compounding_type_enum`;

ALTER TABLE `m_savings_product` ADD `interest_compounding_type_enum` INT(1) DEFAULT '1';