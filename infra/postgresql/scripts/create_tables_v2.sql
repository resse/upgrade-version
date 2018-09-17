CREATE TABLE subjects (
    uid varchar PRIMARY KEY,
    subject varchar
);

CREATE TABLE tickets (
    uid varchar PRIMARY KEY,
    c_date bigint,
    subject varchar REFERENCES subjects (uid),
    description varchar
);

