CREATE TABLE IF NOT EXISTS EMPLOYEE_TBL (
                              EMPLOYEE_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                              NAME VARCHAR(100) NOT NULL,
                              DATE_OF_BIRTH DATE NOT NULL,
                              AVATAR_URL VARCHAR(255),
                              JOB_ROLE VARCHAR(100) NOT NULL,
                              GENDER VARCHAR(10) NOT NULL,
                              AGE INT NOT NULL,
                              EMAIL VARCHAR(255) NOT NULL
);

CREATE TABLE AI_DATA (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY, -- Changed to BIGINT to match Java Long
                         topic VARCHAR(255) NOT NULL,
                         data CLOB NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
