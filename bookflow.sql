CREATE TABLE users
(
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    name       TEXT NOT NULL,
    email      TEXT NOT NULL UNIQUE,
    password   TEXT NOT NULL,
    role       TEXT DEFAULT 'USER',
    created_at TEXT DEFAULT (date('now'))
);

CREATE TABLE books
(
    isbn      TEXT PRIMARY KEY,
    title     TEXT    NOT NULL,
    author    TEXT    NOT NULL,
    genre     TEXT,
    available INTEGER DEFAULT 0,
    price     INTEGER NOT NULL
);

CREATE TABLE loaned_books
(
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id     INTEGER NOT NULL,
    isbn        TEXT    NOT NULL,
    issue_date  TEXT DEFAULT (date('now')),
    return_date TEXT,
    status      TEXT DEFAULT 'ISSUED',

    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (isbn) REFERENCES books (isbn) ON DELETE CASCADE
);

INSERT INTO users (name, email, password, role)
VALUES ('Sahir Ul Hassan', 'sahirulhasan2002@gmail.com', 'admin', 'ADMIN');
INSERT INTO users (name, email, password)
VALUES ('Ali Khan', 'ali.khan@example.com', 'pass123'),
       ('Fatima Noor', 'fatima.noor@example.com', '123456'),
       ('Zain Ahmed', 'zain.ahmed@example.com', 'mypassword'),
       ('Hira Ali', 'hira.ali@example.com', 'letmein'),
       ('Usman Tariq', 'usman.tariq@example.com', 'usmanpass'),
       ('Sara Naveed', 'sara.naveed@example.com', 'sara2023'),
       ('Hamza Shah', 'hamza.shah@example.com', 'qwerty'),
       ('Ayesha Malik', 'ayesha.malik@example.com', 'ayeshapass'),
       ('Bilal Asif', 'bilal.asif@example.com', 'bilal123'),
       ('Mehwish Raza', 'mehwish.raza@example.com', 'mehwishpass'),
       ('Tariq Jameel', 'tariq.jameel@example.com', 'tariq321'),
       ('Rabia Iqbal', 'rabia.iqbal@example.com', 'rabia456'),
       ('Hasnain Raza', 'hasnain.raza@example.com', 'hasnain789'),
       ('Anam Qureshi', 'anam.qureshi@example.com', 'anampass'),
       ('Saad Farooq', 'saad.farooq@example.com', 'farooqpass'),
       ('Mahnoor Zia', 'mahnoor.zia@example.com', 'mahnoor12'),
       ('Imran Yousaf', 'imran.yousaf@example.com', 'imranpass'),
       ('Sana Mir', 'sana.mir@example.com', 'sana321'),
       ('Asad Umer', 'asad.umer@example.com', 'passumer'),
       ('Nida Hussain', 'nida.hussain@example.com', 'nida456');

INSERT INTO books (isbn, title, author, genre, available, price)
VALUES ('9780321356680', 'Effective C++', 'Scott Meyers', 'Programming', 6, 4200),
       ('9780131103627', 'The C Programming Language', 'Kernighan & Ritchie', 'Programming', 4, 3700),
       ('9780132350884', 'Clean Code', 'Robert C. Martin', 'Programming', 9, 4600),
       ('9780262033848', 'Introduction to Algorithms', 'Cormen et al.', 'Programming', 3, 5500),
       ('9781491957660', 'Designing Data-Intensive Applications', 'Martin Kleppmann', 'Data Systems', 8, 4900),
       ('9780134494166', 'Computer Networking', 'James F. Kurose', 'Networking', 7, 4300),
       ('9780596007126', 'Head First Design Patterns', 'Eric Freeman', 'Programming', 5, 4100),
       ('9781449355739', 'Python for Data Analysis', 'Wes McKinney', 'Data Science', 6, 4400),
       ('9780201633610', 'Design Patterns', 'Erich Gamma', 'Programming', 2, 5000),
       ('9780262038003', 'Deep Learning', 'Ian Goodfellow', 'AI', 10, 5300),
       ('9780134685991', 'Effective Java', 'Joshua Bloch', 'Programming', 5, 4500),
       ('9780596009205', 'Head First Java', 'Kathy Sierra', 'Programming', 5, 3500),
       ('9780441172719', 'Dune', 'Frank Herbert', 'Science Fiction', 7, 4000),
       ('9780062316097', 'Sapiens', 'Yuval Noah Harari', 'History', 12, 3000),
       ('9780143127741', 'Thinking, Fast and Slow', 'Daniel Kahneman', 'Psychology', 8, 3200),
       ('9781501124020', 'Grit', 'Angela Duckworth', 'Psychology', 6, 3100),
       ('9780141988511', 'Homo Deus', 'Yuval Noah Harari', 'History', 4, 3300),
       ('9781250272098', 'The Psychology of Money', 'Morgan Housel', 'Finance', 5, 2700),
       ('9780345803481', 'Quiet', 'Susan Cain', 'Psychology', 9, 2900),
       ('9780062457714', '21 Lessons for the 21st Century', 'Yuval Noah Harari', 'History', 7, 3400);

INSERT INTO loaned_books (user_id, isbn, issue_date, return_date, status)
VALUES (1, '9780134685991', '2025-05-01', '2025-05-08', 'RETURNED'),
       (2, '9780596009205', '2025-06-01', '2025-06-08', 'ISSUED'),
       (3, '9780441172719', '2025-06-02', '2025-06-09', 'ISSUED'),
       (4, '9780062316097', '2025-05-20', '2025-05-27', 'RETURNED'),
       (5, '9780143127741', '2025-06-05', '2025-06-12', 'ISSUED'),
       (6, '9780321356680', '2025-06-01', '2025-06-08', 'ISSUED'),
       (7, '9780131103627', '2025-05-28', '2025-06-04', 'RETURNED'),
       (8, '9780132350884', '2025-06-02', '2025-06-09', 'ISSUED'),
       (9, '9780262033848', '2025-06-03', '2025-06-10', 'ISSUED'),
       (10, '9780596007126', '2025-05-15', '2025-05-22', 'RETURNED'),
       (11, '9781449355739', '2025-06-01', '2025-06-08', 'ISSUED'),
       (12, '9780201633610', '2025-05-25', '2025-06-01', 'RETURNED'),
       (13, '9780262038003', '2025-06-05', '2025-06-12', 'ISSUED'),
       (14, '9781491957660', '2025-06-02', '2025-06-09', 'ISSUED'),
       (15, '9780134494166', '2025-06-03', '2025-06-10', 'ISSUED'),
       (16, '9781501124020', '2025-05-10', '2025-05-17', 'RETURNED'),
       (17, '9780141988511', '2025-06-01', '2025-06-08', 'ISSUED'),
       (18, '9781250272098', '2025-06-04', '2025-06-11', 'ISSUED'),
       (19, '9780345803481', '2025-05-30', '2025-06-06', 'RETURNED'),
       (20, '9780062457714', '2025-06-06', '2025-06-13', 'ISSUED');
