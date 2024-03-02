SET GLOBAL event_scheduler = ON;

DELIMITER //

CREATE PROCEDURE delete_views()
BEGIN
    DELETE FROM history WHERE date < DATE_SUB(NOW(), INTERVAL 7 DAY);
END //

DELIMITER ;

CREATE EVENT clear_views_data
ON SCHEDULE EVERY 7 DAY
DO
    CALL delete_views();
