---Create User Table
create table IF NOT EXISTS users
(
    id SERIAL PRIMARY KEY,
    username VARCHAR(100),
    password VARCHAR(50),
    created_date TIMESTAMP NOT NULL,
    updated_date TIMESTAMP NOT NULL,
    deleted_date TIMESTAMP
);

---Create Role Table
create table IF NOT EXISTS roles
(
    id SERIAL PRIMARY KEY,
    role_name VARCHAR(100),
    description_role VARCHAR(250),
    created_date TIMESTAMP NOT NULL,
    updated_date TIMESTAMP NOT NULL,
    deleted_date TIMESTAMP
);

INSERT INTO roles (role_name,description_role, created_date, updated_date, deleted_date)
VALUES ('Admin', 'All permissions across the entire system', LOCALTIMESTAMP,LOCALTIMESTAMP, NULL);

INSERT INTO roles (role_name,description_role, created_date, updated_date, deleted_date)
VALUES ('Cashier ', 'Responsible for processing customer payments, issuing receipts, and reconciling daily cash and electronic transactions to ensure accuracy at the point of sale.',
        LOCALTIMESTAMP,LOCALTIMESTAMP, NULL);


---Create User Permissions Table
create table IF NOT EXISTS user_permissions
(
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id),
    role_id INTEGER REFERENCES roles(id),
    created_date TIMESTAMP NOT NULL,
    updated_date TIMESTAMP NOT NULL,
    deleted_date TIMESTAMP
);