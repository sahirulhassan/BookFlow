-- Create USERS table
CREATE TABLE users (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    name       TEXT NOT NULL,
    email      TEXT NOT NULL UNIQUE,
    password   TEXT NOT NULL,
    role       TEXT DEFAULT 'USER', -- ENUM not supported in SQLite
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert an admin user
INSERT INTO users (name, email, password, role)
VALUES ('Sahir Ul Hassan', 'sahirulhasan2002@gmail.com', 'admin', 'ADMIN');

-- Create BOOKS table
CREATE TABLE books (
    isbn      TEXT PRIMARY KEY,
    title     TEXT NOT NULL,
    author    TEXT NOT NULL,
    genre     TEXT,
    available INTEGER DEFAULT 0,
    price     INTEGER NOT NULL
);

-- Insert some books
INSERT INTO books (isbn, title, author, genre, available, price)
VALUES ('9780134685991', 'Effective Java', 'Joshua Bloch', 'Programming', 10, 4500),
       ('9780596009205', 'Head First Java', 'Kathy Sierra', 'Programming', 5, 3500),
       ('9780441172719', 'Dune', 'Frank Herbert', 'Science Fiction', 7, 4000),
       ('9780062316097', 'Sapiens', 'Yuval Noah Harari', 'History', 12, 3000),
       ('9780143127741', 'Thinking, Fast and Slow', 'Daniel Kahneman', 'Psychology', 8, 3200);

-- Create LOANED_BOOKS table
CREATE TABLE loaned_books (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id     INTEGER NOT NULL,
    isbn        TEXT NOT NULL,
    issue_date  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    return_date DATE,
    status      TEXT DEFAULT 'ISSUED', -- ENUM not supported in SQLite

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (isbn) REFERENCES books(isbn) ON DELETE CASCADE
);
