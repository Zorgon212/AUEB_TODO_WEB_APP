-- two default account are created here for if someone does not want to do a registration

-- Default admin account
-- password: P@ssw0rd
-- email: admin@todoApp.gr
INSERT IGNORE INTO users (full_name, email, password, status, user_type)
VALUES ('Default Admin', 'admin@todoApp.gr', '$2a$10$alv.R4D8SCJuyGMSTgzkAuwh0P2ZgP0hQxEw54nJqhiw0TJw6dYA2', true, 'ADMIN');

-- Default user account
-- password: P@ssw0rd
-- email: user@todoApp.gr
INSERT IGNORE INTO users (full_name, email, password, status, user_type)
VALUES ('Default User', 'user@todoApp.gr', '$2a$10$alv.R4D8SCJuyGMSTgzkAuwh0P2ZgP0hQxEw54nJqhiw0TJw6dYA2', true, 'USER');
