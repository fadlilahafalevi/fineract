DELIMITER //
CREATE PROCEDURE `p_terbilang`(IN `angka` bigint, OUT `retval` TEXT)
BEGIN
	DECLARE `tmp1` TEXT;
    DECLARE `tmp2` TEXT;
     
    SET max_sp_recursion_depth := 20;
 
    IF(angka = 0) THEN
        SET retval = '';
  ELSEIF(angka < 12) THEN
        SET retval = ELT(angka,'satu','dua','tiga','empat','lima','enam','tujuh','delapan','sembilan','sepuluh','sebelas');
  ELSEIF (angka < 20) THEN
        CALL p_terbilang((angka-10),tmp1);
        SET retval = CONCAT(tmp1,' belas');
  ELSEIF (angka < 100) THEN
        CALL p_terbilang(FLOOR(angka/10),tmp1);
        CALL p_terbilang((angka%10),tmp2);
        SET retval = CONCAT(tmp1,' puluh ',tmp2);
  ELSEIf (angka < 200) THEN
        CALL p_terbilang((angka-100),tmp1);
        SET retval = CONCAT('seratus ',tmp1);
  ELSEIF (angka < 1000) THEN
        CALL p_terbilang(FLOOR(angka/100),tmp1);
        CALL p_terbilang((angka%100),tmp2);
        SET retval = CONCAT(tmp1,' ratus ',tmp2);
  ELSEIF (angka < 2000) THEN
        CALL p_terbilang((angka-1000),tmp1);
        SET retval = CONCAT('seribu ',tmp1);
  ELSEIF (angka < 1000000) THEN
        CALL p_terbilang(FLOOR(angka/1000),tmp1);
        CALL p_terbilang((angka%1000),tmp2);
        SET retval = CONCAT(tmp1,' ribu ',tmp2);
  ELSEIF (angka < 1000000000) THEN
        CALL p_terbilang(FLOOR(angka/1000000),tmp1);
        CALL p_terbilang((angka%1000000),tmp2);
        SET retval = CONCAT(tmp1,' juta ',tmp2);
  ELSEIF (angka < 1000000000000) THEN
        CALL p_terbilang(FLOOR(angka/1000000000),tmp1);
        CALL p_terbilang((angka%1000000000),tmp2);
        SET retval = CONCAT(tmp1,' milyar ',tmp2);
  ELSE SET retval = 'GIA';
  END IF;
END //
DELIMITER;