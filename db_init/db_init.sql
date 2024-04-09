CREATE TABLE Departments (
    id SERIAL PRIMARY KEY,
    department_name TEXT,
    last_operation_time TIMESTAMP
);

CREATE TABLE Users (
    id SERIAL PRIMARY KEY,
    username TEXT,
    password TEXT,
    role TEXT
);

CREATE TABLE Employees (
    id SERIAL PRIMARY KEY,
    username TEXT,
    name TEXT,
    image BYTEA,
    gender TEXT,
    position TEXT,
    hire_date DATE,
    last_operation_time TIMESTAMP,
    department_id INT
);

CREATE TABLE Classes (
    id SERIAL PRIMARY KEY,
    class_name TEXT,
    classroom TEXT,
    start_time DATE,
    end_time DATE,
    head_teacher_id INT
);

CREATE TABLE Students (
    id SERIAL PRIMARY KEY,
    name TEXT,
    student_number TEXT,
    class_id INT,
    gender TEXT,
    phone_number TEXT,
    highest_education TEXT,
    disciplinary_actions INT,
    disciplinary_points INT,
    last_operation_time TIMESTAMP
);