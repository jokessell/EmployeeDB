-- src/main/resources/schema/schema.sql

-- Existing Employee Table Creation
CREATE TABLE IF NOT EXISTS EMPLOYEE_TBL (
                                            EMPLOYEE_ID BIGINT PRIMARY KEY AUTO_INCREMENT,
                                            NAME VARCHAR(255) NOT NULL,
                                            DATE_OF_BIRTH DATE NOT NULL,
                                            AVATAR_URL VARCHAR(512),
                                            JOB_ROLE VARCHAR(255) NOT NULL,
                                            GENDER VARCHAR(50) NOT NULL,
                                            AGE INTEGER,
                                            EMAIL VARCHAR(255)
);

-- Existing Project Table Creation
CREATE TABLE IF NOT EXISTS PROJECT_TBL (
                                           PROJECT_ID BIGINT PRIMARY KEY AUTO_INCREMENT,
                                           PROJECT_NAME VARCHAR(255) NOT NULL,
                                           DESCRIPTION VARCHAR(1024)
);

-- Skill Table Creation
CREATE TABLE IF NOT EXISTS SKILL_TBL (
                                         SKILL_ID BIGINT PRIMARY KEY AUTO_INCREMENT,
                                         NAME VARCHAR(255) NOT NULL UNIQUE
);

-- Employee_Project Join Table
CREATE TABLE IF NOT EXISTS EMPLOYEE_PROJECT_TBL (
                                                    EMPLOYEE_ID BIGINT NOT NULL,
                                                    PROJECT_ID BIGINT NOT NULL,
                                                    PRIMARY KEY (EMPLOYEE_ID, PROJECT_ID),
                                                    CONSTRAINT FK_EMPLOYEE_PROJECT_EMPLOYEE FOREIGN KEY (EMPLOYEE_ID) REFERENCES EMPLOYEE_TBL(EMPLOYEE_ID) ON DELETE CASCADE,
                                                    CONSTRAINT FK_EMPLOYEE_PROJECT_PROJECT FOREIGN KEY (PROJECT_ID) REFERENCES PROJECT_TBL(PROJECT_ID) ON DELETE CASCADE
);

-- Employee_Skill Join Table
CREATE TABLE IF NOT EXISTS EMPLOYEE_SKILL_TBL (
                                                  EMPLOYEE_ID BIGINT NOT NULL,
                                                  SKILL_ID BIGINT NOT NULL,
                                                  PRIMARY KEY (EMPLOYEE_ID, SKILL_ID),
                                                  CONSTRAINT FK_EMPLOYEE_SKILL_EMPLOYEE FOREIGN KEY (EMPLOYEE_ID) REFERENCES EMPLOYEE_TBL(EMPLOYEE_ID) ON DELETE CASCADE,
                                                  CONSTRAINT FK_EMPLOYEE_SKILL_SKILL FOREIGN KEY (SKILL_ID) REFERENCES SKILL_TBL(SKILL_ID) ON DELETE CASCADE
);

-- Project_Skill Join Table
CREATE TABLE IF NOT EXISTS PROJECT_SKILL_TBL (
                                                 PROJECT_ID BIGINT NOT NULL,
                                                 SKILL_ID BIGINT NOT NULL,
                                                 PRIMARY KEY (PROJECT_ID, SKILL_ID),
                                                 CONSTRAINT FK_PROJECT_SKILL_PROJECT FOREIGN KEY (PROJECT_ID) REFERENCES PROJECT_TBL(PROJECT_ID) ON DELETE CASCADE,
                                                 CONSTRAINT FK_PROJECT_SKILL_SKILL FOREIGN KEY (SKILL_ID) REFERENCES SKILL_TBL(SKILL_ID) ON DELETE CASCADE
);

-- Create the 'roles' table
CREATE TABLE ROLE_TBL (
                       ROLE_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                       ROLE_NAME VARCHAR(50) NOT NULL UNIQUE
);

-- Create the 'users' table
CREATE TABLE USER_TBL (
                       USER_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                       USER_NAME VARCHAR(50) NOT NULL UNIQUE,
                       USER_PASSWORD VARCHAR(100) NOT NULL
);

-- Create the 'user_roles' join table to establish many-to-many relationships
CREATE TABLE USER_ROLE_TBL (
                            USER_ID BIGINT NOT NULL,
                            ROLE_ID BIGINT NOT NULL,
                            PRIMARY KEY (USER_ID, ROLE_ID),
                            FOREIGN KEY (USER_ID) REFERENCES USER_TBL(USER_ID) ON DELETE CASCADE,
                            FOREIGN KEY (ROLE_ID) REFERENCES ROLE_TBL(ROLE_ID) ON DELETE CASCADE
);