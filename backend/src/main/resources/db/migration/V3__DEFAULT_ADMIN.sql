-- Default admin account, created if it doesn't already exist.
-- password: P@ssw0rd (BCrypt-hashed, matches the app's PasswordEncoder)
INSERT IGNORE INTO users (full_name, email, password, status, user_type)
VALUES ('Default Admin', 'admin@todoApp.gr', '$2a$10$alv.R4D8SCJuyGMSTgzkAuwh0P2ZgP0hQxEw54nJqhiw0TJw6dYA2', true, 'ADMIN');
