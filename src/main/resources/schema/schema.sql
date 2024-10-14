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

-- New Project Table Creation
CREATE TABLE IF NOT EXISTS PROJECT_TBL (
                                           PROJECT_ID BIGINT PRIMARY KEY AUTO_INCREMENT,
                                           PROJECT_NAME VARCHAR(255) NOT NULL,
                                           DESCRIPTION VARCHAR(1024),
                                           EMPLOYEE_ID BIGINT NOT NULL,
                                           CONSTRAINT FK_EMPLOYEE_PROJECT FOREIGN KEY (EMPLOYEE_ID) REFERENCES EMPLOYEE_TBL(EMPLOYEE_ID) ON DELETE CASCADE
);
