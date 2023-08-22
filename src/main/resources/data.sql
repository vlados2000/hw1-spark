drop table if exists doll;

create table if not exists doll
(
    id   INT PRIMARY KEY AUTO_INCREMENT,
    nume VARCHAR(50) unique,
    pret INT,
    stoc INT
);