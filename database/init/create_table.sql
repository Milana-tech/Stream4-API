USE stream4;

DROP TABLE IF EXISTS WatchHistory;
DROP TABLE IF EXISTS Watchlist;
DROP TABLE IF EXISTS Episode;
DROP TABLE IF EXISTS Season;
DROP TABLE IF EXISTS Film;
DROP TABLE IF EXISTS Series;
DROP TABLE IF EXISTS Title;
DROP TABLE IF EXISTS Preference;
DROP TABLE IF EXISTS Invitation;
DROP TABLE IF EXISTS Subscription;
DROP TABLE IF EXISTS SubscriptionPackage;
DROP TABLE IF EXISTS VerificationLink;
DROP TABLE IF EXISTS Profile;
DROP TABLE IF EXISTS Account;
DROP TABLE IF EXISTS Employee;
DROP TABLE IF EXISTS RoleRights;
DROP TABLE IF EXISTS Quality;
DROP TABLE IF EXISTS AgeRating;

CREATE TABLE Account (
    AccountID VARCHAR(36) PRIMARY KEY,
    EmailAddress VARCHAR(255) NOT NULL UNIQUE,
    Password VARCHAR(255) NOT NULL,
    Status ENUM('ACTIVE', 'INACTIVE', 'BLOCKED', 'PENDING_VERIFICATION') NOT NULL DEFAULT 'PENDING_VERIFICATION',
    
    -- Account failed login
    FailedLoginAttempts INT NOT NULL DEFAULT 0,
    
    -- Timestamp
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Constraints
    CHECK (FailedLoginAttempts >= 0 AND FailedLoginAttempts <= 10),
    
    -- Indexes
    INDEX idx_email (EmailAddress),
    INDEX idx_status (Status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE VerificationLink (
    VerificationLinkID BIGINT AUTO_INCREMENT PRIMARY KEY,
    AccountID VARCHAR(36) NOT NULL,
    LinkCode VARCHAR(255) NOT NULL UNIQUE,
    LinkType ENUM('ACCOUNT_ACTIVATION', 'PASSWORD_RESET') NOT NULL,
    ExpiryDate DATETIME NOT NULL,
    Used BOOLEAN NOT NULL DEFAULT FALSE,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign keys
    CONSTRAINT fk_verification_account 
        FOREIGN KEY (AccountID) REFERENCES Account(AccountID) 
        ON DELETE CASCADE,
    
    -- Indexes
    INDEX idx_link_code (LinkCode),
    INDEX idx_account_id (AccountID),
    INDEX idx_expiry (ExpiryDate)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE Profile (
    ProfileID BIGINT AUTO_INCREMENT PRIMARY KEY,
    AccountID VARCHAR(36) NOT NULL,
    Name VARCHAR(100) NOT NULL,
    AgeCategory ENUM('KIDS', 'TEENS', 'ADULTS') NOT NULL,
    ProfileImage VARCHAR(500),
    IsPrimary BOOLEAN NOT NULL DEFAULT FALSE,
    
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign keys
    CONSTRAINT fk_profile_account 
        FOREIGN KEY (AccountID) REFERENCES Account(AccountID) 
        ON DELETE CASCADE,
    
    -- Constraints
    UNIQUE KEY unique_profile_name (AccountID, Name),
    
    -- Indexes
    INDEX idx_account_id (AccountID),
    INDEX idx_age_category (AgeCategory)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
