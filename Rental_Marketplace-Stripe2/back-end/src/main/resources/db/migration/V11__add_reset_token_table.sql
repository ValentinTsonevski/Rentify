CREATE TABLE reset_token (
    id int AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255),
    expiration_date_time TIMESTAMP,
    user_id int NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id)
);
