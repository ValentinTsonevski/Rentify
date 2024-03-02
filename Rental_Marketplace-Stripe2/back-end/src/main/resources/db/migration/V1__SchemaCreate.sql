CREATE TABLE IF NOT EXISTS user_role (
    id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(10) NOT NULL,
    role_description VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS address (
    id INT AUTO_INCREMENT PRIMARY KEY,
    city VARCHAR(100) NOT NULL,
    street VARCHAR(100) NOT NULL,
    post_code VARCHAR(10) NOT NULL,
    street_number VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    password VARCHAR(255),
    email VARCHAR(100) UNIQUE NOT NULL,
    address_id INT,
    iban VARCHAR(255),
    stripe_account_id VARCHAR(255) UNIQUE,
    FOREIGN KEY (address_id) REFERENCES address(id),
    profile_picture VARCHAR(512),
    role_id INT,
    FOREIGN KEY (role_id) REFERENCES user_role(id),
    phone VARCHAR(13)
);

CREATE TABLE IF NOT EXISTS item_category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS item (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(1024) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    category_id INT,
    item_stripe_id VARCHAR(512) UNIQUE,
    thumbnail VARCHAR(512),
    FOREIGN KEY (category_id) REFERENCES item_category(id),
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES user(id),
    posted_date DATETIME NOT NULL,
    deposit FLOAT(10,2) NOT NULL,
    address_id INT,
    FOREIGN KEY (address_id) REFERENCES address(id)
);

CREATE TABLE IF NOT EXISTS picture (
    id INT AUTO_INCREMENT PRIMARY KEY,
    url VARCHAR(512) NOT NULL,
    item_id INT,
    FOREIGN KEY (item_id) REFERENCES item(id)
);

CREATE TABLE IF NOT EXISTS liked_item (
    id INT AUTO_INCREMENT PRIMARY KEY,
    item_id INT,
    FOREIGN KEY (item_id) REFERENCES item(id),
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE IF NOT EXISTS rent (
    id INT AUTO_INCREMENT PRIMARY KEY,
    item_id INT,
    FOREIGN KEY (item_id) REFERENCES item(id),
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES user(id),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS payment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    date DATETIME NOT NULL,
    owner_id INT,
    FOREIGN KEY (owner_id) REFERENCES user(id),
    receiver_id INT,
    FOREIGN KEY (receiver_id) REFERENCES user(id),
    method VARCHAR(50) NOT NULL,
    rent_id INT,
    FOREIGN KEY (rent_id) REFERENCES rent(id)
);

CREATE TABLE IF NOT EXISTS review (
    id INT AUTO_INCREMENT PRIMARY KEY,
    rating SMALLINT,
    comment VARCHAR(255),
    item_id INT,
    FOREIGN KEY (item_id) REFERENCES item(id),
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE IF NOT EXISTS history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES user(id),
    item_id INT,
    FOREIGN KEY (item_id) REFERENCES item(id),
    date DATETIME
);