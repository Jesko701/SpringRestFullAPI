CREATE TABLE users (
	username VARCHAR(255) NOT NULL PRIMARY KEY,
	password VARCHAR(100) NOT NULL,
	name VARCHAR(100) NOT NULL,
	token VARCHAR(100) UNIQUE,
	token_expired_at BIGINT
);

select * from users;

CREATE TABLE contacts (
	id VARCHAR(100) NOT NULL PRIMARY KEY,
	username VARCHAR(100) NOT NULL,
	first_name VARCHAR(100) NOT NULL,
	last_name VARCHAR(100),
	phone VARCHAR(100),
	email VARCHAR(100),
	CONSTRAINT fk_users_contacts FOREIGN KEY (username) REFERENCES users(username)
);

CREATE TABLE addresses (
	id VARCHAR(100) NOT NULL PRIMARY KEY,
	contact_id VARCHAR(100) NOT NULL,
	street VARCHAR(200),
	city VARCHAR(100),
	province VARCHAR(100),
	country VARCHAR(100) NOT NULL,
	postal_code VARCHAR(10),
	COSNTRAINT fk_contacts_addresses FOREIGN KEY (contact_id) REFERENCES contacts(id)
);