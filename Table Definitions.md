# Table Definitions

## Users
~~~~
CREATE TABLE users (
    id int auto_increment primary key not null,  
    username varchar(30) not null unique,
    password varchar(1024) not null,
    firstName varchar(30) not null,
    lastName varchar(30) not null,
    addDate datetime not null
);
~~~~

## Tasks
~~~~
CREATE TABLE tasks (
	id int auto_increment primary key not null,
    userID int not null,
    task text not null,
    addDate datetime not null,
    taskDueDate datetime,
    taskStatus boolean
);
~~~~
