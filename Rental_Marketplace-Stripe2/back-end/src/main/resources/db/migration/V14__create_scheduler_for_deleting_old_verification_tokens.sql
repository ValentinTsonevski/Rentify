SET GLOBAL event_scheduler = ON;

DELIMITER //

CREATE PROCEDURE delete_old_verification_tokens()
BEGIN
    DELETE FROM verification_token WHERE expiration_date_time < DATE_SUB(NOW(), INTERVAL 7 DAY);
END //

DELIMITER ;

CREATE EVENT clear_verification_tokens
ON SCHEDULE EVERY 7 DAY
DO
    CALL delete_old_verification_tokens();