-- CREATE DATABASE todo_mysql_tasos;
USE todo_mysql_tasos;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    status BOOLEAN,
    user_type VARCHAR(50));

-- CREATE TABLE IF NOT EXISTS time (
--     id INT AUTO_INCREMENT PRIMARY KEY,
--     full_date_time DATETIME NOT NULL,
--     time_td TIME,
--     date_td DATE,
--     day_td VARCHAR(30),
--     month_td VARCHAR(30),
--     year_td INT);

CREATE TABLE IF NOT EXISTS tasks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    status BOOLEAN NOT NULL,
    user_id INT NOT NULL,
    declared_time_id DATETIME,
    completion_time_id DATETIME ,
    -- foreign keys
    CONSTRAINT fk_tasks_users FOREIGN KEY (user_id) REFERENCES users(id)
--     , CONSTRAINT fk_tasks_declared_time FOREIGN KEY (declared_time_id) REFERENCES time(id)
--     , CONSTRAINT fk_tasks_completion_time FOREIGN KEY (completion_time_id) REFERENCES time(id)
    );

