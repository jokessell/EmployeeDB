-- Insert Skills
INSERT INTO SKILL_TBL (NAME) VALUES
 ('Java'),
 ('Spring Boot'),
 ('React'),
 ('SQL'),
 ('Docker'),
 ('Kubernetes'),
 ('AWS'),
 ('Python'),
 ('Machine Learning'),
 ('UI/UX Design'),
 ('DevOps'),
 ('Project Management'),
 ('Quality Assurance'),
 ('Git'),
 ('Jenkins'),
 ('Agile Methodologies'),
 ('REST APIs'),
 ('Microservices'),
 ('NoSQL'),
 ('Security'),
 ('JavaScript'),
 ('TypeScript'),
 ('C#'),
 ('C++'),
 ('Ruby'),
 ('Go'),
 ('PHP'),
 ('Swift'),
 ('Scala'),
 ('Perl');

-- Insert Employees
INSERT INTO EMPLOYEE_TBL (NAME, DATE_OF_BIRTH, AVATAR_URL, JOB_ROLE, GENDER, AGE, EMAIL) VALUES
 ('Alice Johnson', '1990-05-21', 'https://api.dicebear.com/6.x/adventurer/png?seed=Alice%20Johnson', 'Software Engineer', 'Female', 34, 'ajohnson@example.com'),
 ('Bob Smith', '1985-08-10', 'https://api.dicebear.com/6.x/adventurer/png?seed=Bob%20Smith', 'DevOps Engineer', 'Male', 39, 'bsmith@example.com'),
 ('Charlie Davis', '1992-03-15', 'https://api.dicebear.com/6.x/adventurer/png?seed=Charlie%20Davis', 'QA Engineer', 'Non-binary', 32, 'cdavis@example.com'),
 ('Diana Evans', '1988-11-05', 'https://api.dicebear.com/6.x/adventurer/png?seed=Diana%20Evans', 'Project Manager', 'Female', 35, 'devans@example.com'),
 ('Evan Green', '1993-07-25', 'https://api.dicebear.com/6.x/adventurer/png?seed=Evan%20Green', 'UX Designer', 'Male', 31, 'egreen@example.com'),
 ('Fiona Harris', '1991-02-17', 'https://api.dicebear.com/6.x/adventurer/png?seed=Fiona%20Harris', 'Frontend Developer', 'Female', 33, 'fharris@example.com'),
 ('George King', '1987-09-12', 'https://api.dicebear.com/6.x/adventurer/png?seed=George%20King', 'Backend Developer', 'Male', 36, 'gking@example.com'),
 ('Hannah Lee', '1994-04-30', 'https://api.dicebear.com/6.x/adventurer/png?seed=Hannah%20Lee', 'Data Scientist', 'Female', 30, 'hlee@example.com'),
 ('Ian Moore', '1989-06-22', 'https://api.dicebear.com/6.x/adventurer/png?seed=Ian%20Moore', 'DevOps Engineer', 'Male', 34, 'imoore@example.com'),
 ('Jasmine Patel', '1995-01-08', 'https://api.dicebear.com/6.x/adventurer/png?seed=Jasmine%20Patel', 'QA Analyst', 'Female', 29, 'jpatel@example.com'),
 ('Kevin Quinn', '1990-10-14', 'https://api.dicebear.com/6.x/adventurer/png?seed=Kevin%20Quinn', 'Software Engineer', 'Male', 33, 'kquinn@example.com'),
 ('Laura Roberts', '1986-12-03', 'https://api.dicebear.com/6.x/adventurer/png?seed=Laura%20Roberts', 'Product Manager', 'Female', 37, 'lroberts@example.com'),
 ('Michael Scott', '1984-07-07', 'https://api.dicebear.com/6.x/adventurer/png?seed=Michael%20Scott', 'Scrum Master', 'Male', 39, 'mscott@example.com'),
 ('Nina Turner', '1993-03-19', 'https://api.dicebear.com/6.x/adventurer/png?seed=Nina%20Turner', 'UI/UX Designer', 'Female', 31, 'nturner@example.com'),
 ('Oscar Vega', '1988-05-27', 'https://api.dicebear.com/6.x/adventurer/png?seed=Oscar%20Vega', 'Security Analyst', 'Male', 35, 'ovega@example.com'),
 ('Paula White', '1992-11-11', 'https://api.dicebear.com/6.x/adventurer/png?seed=Paula%20White', 'Frontend Developer', 'Female', 31, 'pwhite@example.com'),
 ('Quentin Brown', '1987-08-19', 'https://api.dicebear.com/6.x/adventurer/png?seed=Quentin%20Brown', 'Backend Developer', 'Male', 36, 'qbrown@example.com'),
 ('Rachel Adams', '1991-09-23', 'https://api.dicebear.com/6.x/adventurer/png?seed=Rachel%20Adams', 'Data Analyst', 'Female', 32, 'radams@example.com'),
 ('Steven Clark', '1985-04-05', 'https://api.dicebear.com/6.x/adventurer/png?seed=Steven%20Clark', 'DevOps Engineer', 'Male', 38, 'sclark@example.com'),
 ('Tina Davis', '1994-02-28', 'https://api.dicebear.com/6.x/adventurer/png?seed=Tina%20Davis', 'Quality Assurance', 'Female', 30, 'tdavis@example.com');

-- Insert Projects
INSERT INTO PROJECT_TBL (PROJECT_NAME, DESCRIPTION) VALUES
    ('Project Gamma', 'Implementing Gamma module'),
    ('Project Delta', 'Testing Delta features'),
    ('Project Epsilon', 'Designing Epsilon UI'),
    ('Project Zeta', 'Deploying Zeta infrastructure'),
    ('Project Eta', 'Maintaining Eta services'),
    ('Project Theta', 'Researching Theta technologies'),
    ('Project Iota', 'Developing Iota analytics'),
    ('Project Kappa', 'Integrating Kappa systems'),
    ('Project Lambda', 'Optimizing Lambda performance'),
    ('Project Mu', 'Launching Mu marketing campaign');

-- Associate Employees with Projects
INSERT INTO EMPLOYEE_PROJECT_TBL (EMPLOYEE_ID, PROJECT_ID) VALUES
-- Project Gamma
(1, 1),
(11, 1),
-- Project Delta
(2, 2),
(12, 2),
(13, 2),
-- Project Epsilon
(4, 3),
(14, 3),
(16, 3),
-- Project Zeta
(2, 4),
-- Project Eta
(7, 5),
(8, 5),
(17, 5),
(18, 5),
-- Project Theta
(3, 6),
(14, 6),
-- Project Iota
(8, 7),
(9, 7),
(19, 7),
-- Project Kappa
(10, 8),
(11, 8),
-- Project Lambda
(5, 9),
-- Project Mu
(4, 10),
(15, 10),
(16, 10);


-- Associate Employees with Skills
INSERT INTO EMPLOYEE_SKILL_TBL (EMPLOYEE_ID, SKILL_ID) VALUES
-- Alice Johnson
(1, 1),  -- Java
(1, 2),  -- Spring Boot
(1, 3),  -- React
-- Bob Smith
(2, 4),  -- SQL
(2, 5),  -- Docker
(2, 6),  -- Kubernetes
(2, 7),  -- AWS
-- Charlie Davis
(3, 8),  -- Python
(3, 9),  -- Machine Learning
-- Diana Evans
(4, 10), -- UI/UX Design
(4, 11), -- DevOps
(4, 12), -- Project Management
(4, 13), -- Quality Assurance
(4, 14), -- Git
-- Evan Green
(5, 15), -- Jenkins
-- Fiona Harris
(6, 16), -- Agile Methodologies
(6, 17), -- REST APIs
-- George King
(7, 18), -- Microservices
(7, 19), -- NoSQL
(7, 20), -- Security
-- Hannah Lee
(8, 21), -- JavaScript
(8, 22), -- TypeScript
(8, 23), -- C#
(8, 24), -- C++
-- Ian Moore
(9, 25), -- Ruby
(9, 26), -- Go
-- Jasmine Patel
(10, 27), -- PHP
-- Kevin Quinn
(11, 28), -- Swift
(11, 29), -- Scala
(11, 30), -- Perl
(11, 1),  -- Java
(11, 2),  -- Spring Boot
-- Laura Roberts
(12, 3),  -- React
(12, 4),  -- SQL
-- Michael Scott
(13, 5),  -- Docker
(13, 6),  -- Kubernetes
(13, 7),  -- AWS
-- Nina Turner
(14, 8),  -- Python
(14, 9),  -- Machine Learning
(14, 10), -- UI/UX Design
(14, 11), -- DevOps
-- Oscar Vega
(15, 12), -- Project Management
-- Paula White
(16, 13), -- Quality Assurance
(16, 14), -- Git
(16, 15), -- Jenkins
(16, 16), -- Agile Methodologies
(16, 17), -- REST APIs
-- Quentin Brown
(17, 18), -- Microservices
(17, 19), -- NoSQL
(17, 20), -- Security
-- Rachel Adams
(18, 21), -- JavaScript
(18, 22), -- TypeScript
-- Steven Clark
(19, 23), -- C#
(19, 24), -- C++
(19, 25), -- Ruby
(19, 26), -- Go
-- Tina Davis
(20, 27); -- PHP


-- Associate Projects with Skills
INSERT INTO PROJECT_SKILL_TBL (PROJECT_ID, SKILL_ID) VALUES
 -- Project Gamma
 (1, 1),  -- Java
 (1, 2),  -- Spring Boot
 (1, 3),  -- React
 -- Project Delta
 (2, 4),  -- SQL
 (2, 5),  -- Docker
 -- Project Epsilon
 (3, 10), -- UI/UX Design
 (3, 11), -- DevOps
 (3, 14), -- Git
 (3, 16), -- Agile Methodologies
 -- Project Zeta
 (4, 7),  -- AWS
 -- Project Eta
 (5, 18), -- Microservices
 (5, 19), -- NoSQL
 (5, 20), -- Security
 (5, 21), -- JavaScript
 (5, 22), -- TypeScript
 -- Project Theta
 (6, 8),  -- Python
 (6, 9),  -- Machine Learning
 -- Project Iota
 (7, 23), -- C#
 (7, 24), -- C++
 (7, 26), -- Go
 -- Project Kappa
 (8, 27), -- PHP
 (8, 28), -- Swift
 -- Project Lambda
 (9, 15), -- Jenkins
 -- Project Mu
 (10, 11), -- DevOps
 (10, 12), -- Project Management
 (10, 13), -- Quality Assurance
 (10, 14); -- Git

-- Insert Roles
INSERT INTO ROLE_TBL (ROLE_NAME) VALUES
                             ('ADMIN'),
                             ('ELEVATED'),
                             ('BASIC');

-- Insert Users
INSERT INTO USER_TBL (USER_NAME, USER_PASSWORD) VALUES ('admin', '$2a$10$Dow1cJZhfT3.Ec0Vv6qSPOJB76EhIHTEU3gJvgeqYPrGToSD0iLuK');

-- Assign Role to User
-- Assuming the 'ADMIN' role has id=1 and the 'admin' user has id=1
INSERT INTO USER_ROLE_TBL (USER_ID, ROLE_ID) VALUES (1, 1);