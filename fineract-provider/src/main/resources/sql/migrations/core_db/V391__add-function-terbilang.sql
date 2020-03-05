DELIMITER //
CREATE FUNCTION `f_terbilang`(angka BIGINT) RETURNS text DETERMINISTIC 
BEGIN
    DECLARE v_result TEXT;
    CALL p_terbilang(angka, v_result);
    RETURN REPLACE(v_result,'  ',' ');
END //
DELIMITER;