CREATE TABLE EMPLOYEE_TBL (
                              EMPLOYEE_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                              NAME VARCHAR(100) NOT NULL,
                              DATE_OF_BIRTH DATE NOT NULL,
                              AVATAR_URL VARCHAR(255),
                              JOB_ROLE VARCHAR(100) NOT NULL,
                              GENDER VARCHAR(10) NOT NULL,
                              AGE INT NOT NULL,
                              EMAIL VARCHAR(255) NOT NULL
);
