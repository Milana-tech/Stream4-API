#!/bin/bash
# StreamFlix – DBMS Employee Access
# Creates internal MySQL users on first database initialisation.
# GRANTs and final passwords are applied by DbEmployeeAccessInitializer
# after the backend starts and Hibernate has created all tables.

mysql -u root -p"${MYSQL_ROOT_PASSWORD}" <<MYSQL
CREATE USER IF NOT EXISTS 'junior_employee'@'%' IDENTIFIED BY 'Junior@Stream4!';
CREATE USER IF NOT EXISTS 'mid_employee'@'%'    IDENTIFIED BY 'Mid@Stream4!';
CREATE USER IF NOT EXISTS 'senior_employee'@'%' IDENTIFIED BY 'Senior@Stream4!';
FLUSH PRIVILEGES;
MYSQL
