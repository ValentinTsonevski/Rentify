CREATE TABLE verification_token (
    id int AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255),
    expiration_date_time TIMESTAMP,
    user_id int NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

ALTER TABLE user
ADD COLUMN is_verified BOOLEAN DEFAULT FALSE;