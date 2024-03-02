INSERT INTO address (city, street, post_code, street_number)
VALUES
    ('City A', 'Main Street', '12345', '1'),
    ('City B', 'Broadway', '54321', '22'),
    ('City C', 'Oak Avenue', '67890', '10'),
    ('City D', 'Maple Lane', '98765', '5');

INSERT INTO user (first_name, last_name, password, email, address_id, profile_picture, role_id, phone)
VALUES
    ('John', 'Doe', 'password123', 'john.doe@example.com', 1, 'profile1.jpg', 1, '1234567890'),
    ('Jane', 'Doe', 'pass456', 'jane.doe@example.com', 2, 'profile2.jpg', 2, '9876543210'),
    ('Alice', 'Smith', 'secret123', 'alice.smith@example.com', 3, 'profile3.jpg', 1, '5551234567');

INSERT INTO item (name, description, price, category_id, user_id, posted_date, deposit, address_id)
VALUES
    ('Item 1', 'Description for Item 1', 29.99, 1, 1, NOW(), 5.00, 1),
    ('Item 2', 'Description for Item 2', 19.99, 2, 2, NOW(), 3.50, 2),
    ('Item 3', 'Description for Item 3', 39.99, 1, 3, NOW(), 7.00, 3);