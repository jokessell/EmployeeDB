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
 ('Alice Johnson', '1990-05-21', 'https://example.com/avatar/alice', 'Software Engineer', 'Female', 34, 'ajohnson@example.com'),
 ('Bob Smith', '1985-08-10', 'https://example.com/avatar/bob', 'DevOps Engineer', 'Male', 39, 'bsmith@example.com'),
 ('Charlie Davis', '1992-03-15', 'https://example.com/avatar/charlie', 'QA Engineer', 'Non-binary', 32, 'cdavis@example.com'),
 ('Diana Evans', '1988-11-05', 'https://example.com/avatar/diana', 'Project Manager', 'Female', 35, 'devans@example.com'),
 ('Evan Green', '1993-07-25', 'https://example.com/avatar/evan', 'UX Designer', 'Male', 31, 'egreen@example.com'),
 ('Fiona Harris', '1991-02-17', 'https://example.com/avatar/fiona', 'Frontend Developer', 'Female', 33, 'fharris@example.com'),
 ('George King', '1987-09-12', 'https://example.com/avatar/george', 'Backend Developer', 'Male', 36, 'gking@example.com'),
 ('Hannah Lee', '1994-04-30', 'https://example.com/avatar/hannah', 'Data Scientist', 'Female', 30, 'hlee@example.com'),
 ('Ian Moore', '1989-06-22', 'https://example.com/avatar/ian', 'DevOps Engineer', 'Male', 34, 'imoore@example.com'),
 ('Jasmine Patel', '1995-01-08', 'https://example.com/avatar/jasmine', 'QA Analyst', 'Female', 29, 'jpatel@example.com'),
 ('Kevin Quinn', '1990-10-14', 'https://example.com/avatar/kevin', 'Software Engineer', 'Male', 33, 'kquinn@example.com'),
 ('Laura Roberts', '1986-12-03', 'https://example.com/avatar/laura', 'Product Manager', 'Female', 37, 'lroberts@example.com'),
 ('Michael Scott', '1984-07-07', 'https://example.com/avatar/michael', 'Scrum Master', 'Male', 39, 'mscott@example.com'),
 ('Nina Turner', '1993-03-19', 'https://example.com/avatar/nina', 'UI/UX Designer', 'Female', 31, 'nturner@example.com'),
 ('Oscar Vega', '1988-05-27', 'https://example.com/avatar/oscar', 'Security Analyst', 'Male', 35, 'ovega@example.com'),
 ('Paula White', '1992-11-11', 'https://example.com/avatar/paula', 'Frontend Developer', 'Female', 31, 'pwhite@example.com'),
 ('Quentin Brown', '1987-08-19', 'https://example.com/avatar/quentin', 'Backend Developer', 'Male', 36, 'qbrown@example.com'),
 ('Rachel Adams', '1991-09-23', 'https://example.com/avatar/rachel', 'Data Analyst', 'Female', 32, 'radams@example.com'),
 ('Steven Clark', '1985-04-05', 'https://example.com/avatar/steven', 'DevOps Engineer', 'Male', 38, 'sclark@example.com'),
 ('Tina Davis', '1994-02-28', 'https://example.com/avatar/tina', 'Quality Assurance', 'Female', 30, 'tdavis@example.com');

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
   (1, 1),
   (1, 2),
   (2, 4),
   (3, 2),
   (4, 3),
   (5, 3),
   (6, 1),
   (7, 1),
   (8, 5),
   (9, 4),
   (10, 2),
   (11, 1),
   (12, 6),
   (13, 6),
   (14, 7),
   (15, 1),
   (16, 8),
   (17, 9),
   (18, 10),
   (19, 4),
   (20, 5);

-- Associate Employees with Skills
INSERT INTO EMPLOYEE_SKILL_TBL (EMPLOYEE_ID, SKILL_ID) VALUES
   (1, 1),
   (1, 2),
   (1, 3),
   (2, 2),
   (2, 5),
   (2, 6),
   (3, 4),
   (3, 13),
   (4, 12),
   (4, 16),
   (5, 3),
   (5, 10),
   (6, 1),
   (6, 17),
   (7, 1),
   (7, 2),
   (8, 8),
   (8, 9),
   (9, 2),
   (9, 5),
   (10, 4),
   (10, 13),
   (11, 1),
   (11, 2),
   (12, 12),
   (12, 16),
   (13, 12),
   (13, 16),
   (14, 3),
   (14, 10),
   (15, 1),
   (15, 2),
   (16, 1),
   (16, 2),
   (17, 1),
   (17, 2),
   (18, 2),
   (18, 5),
   (19, 2),
   (19, 5),
   (20, 5),
   (20, 12);

-- Associate Projects with Skills
INSERT INTO PROJECT_SKILL_TBL (PROJECT_ID, SKILL_ID) VALUES
 (1, 1), -- Java
 (1, 2), -- Spring Boot
 (2, 4), -- SQL
 (2, 13), -- Quality Assurance
 (3, 3), -- React
 (3, 10), -- UI/UX Design
 (4, 2), -- Spring Boot
 (4, 5), -- Docker
 (5, 2), -- Spring Boot
 (5, 6), -- Kubernetes
 (6, 2), -- Spring Boot
 (6, 16), -- Agile Methodologies
 (7, 8), -- Python
 (7, 9), -- Machine Learning
 (8, 1), -- Java
 (8, 2), -- Spring Boot
 (9, 1), -- Java
 (9, 17), -- REST APIs
 (10, 5), -- Docker
 (10, 12); -- Project Management
