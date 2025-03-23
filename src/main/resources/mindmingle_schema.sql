-- Create the database
-- CREATE DATABASE mindmingle;

-- Connect to the mindmingle database 
-- \c mindmingle;

-- Create the users table
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    user_type VARCHAR(10) CHECK (user_type IN ('user', 'admin')) NOT NULL DEFAULT 'user',
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    profile_data JSONB
);

-- Create the quizzes table
CREATE TABLE quizzes (
    quiz_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Create the challenges table
CREATE TABLE challenges (
    challenges_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    duration INTEGER CHECK (duration IN (7, 15, 30)),
    created_at TIMESTAMP DEFAULT NOW()
);

-- Create the community_posts table
CREATE TABLE community_posts (
    post_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    post_title TEXT NOT NULL,
    post_content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Insert some test data
INSERT INTO users (user_type, username, email, password, profile_data) VALUES
('user', 'alice', 'alice@example.com', 'hashed_password_1', '{"interests": ["math", "coding"]}'),
('admin', 'bob', 'bob@example.com', 'hashed_password_2', '{"role": "superadmin"}');

INSERT INTO quizzes (user_id, title, description) VALUES
(1, 'Math Quiz', 'A simple math test'),
(2, 'Coding Quiz', 'Basic programming questions');

INSERT INTO challenges (user_id, title, description, duration) VALUES
(1, '30-Day Coding Challenge', 'Improve coding skills in 30 days', 30),
(2, '15-Day Writing Challenge', 'Write every day for 15 days', 15);

INSERT INTO community_posts (user_id, post_title, post_content) VALUES
(1, 'Excited about the challenge!', 'Looking forward to it.'),
(2, 'Best way to study for quizzes?', 'Any suggestions?');
