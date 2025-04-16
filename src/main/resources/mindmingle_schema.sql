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
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    avatar VARCHAR(255),
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

--Create the quiz_result tabel
CREATE TABLE quiz_results (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL, 
    quiz_id INTEGER NOT NULL,
    score NUMERIC(5,2),
    user_answers JSONB NOT NULL, 
    completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
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

-- Create the challenge_participation table 
CREATE TABLE challenge_participation (
    participation_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    challenges_id INTEGER REFERENCES challenges(challenges_id) ON DELETE CASCADE,
    start_date TIMESTAMP DEFAULT NOW(),
    progress INTEGER DEFAULT 0,
    completed BOOLEAN DEFAULT FALSE
);

--Create the reward table
CREATE TABLE rewards (
    id SERIAL PRIMARY KEY,
    challenge_id INTEGER NOT NULL,
    name VARCHAR(100) NOT NULL, 
    description TEXT, 
    icon_url TEXT, 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--create the user_reward table
CREATE TABLE user_rewards (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    reward_id INTEGER NOT NULL,
    obtained_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create the community_posts table
CREATE TABLE community_posts (
    post_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    post_title TEXT NOT NULL,
    post_content TEXT NOT NULL,
    image_url TEXT,
    likes INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW()
);


-- Change challenge table with two more column

ALTER TABLE challenges
ADD COLUMN gameIdentifier VARCHAR(255),
ADD COLUMN storageUrl TEXT;

UPDATE challenges
SET gameIdentifier = 'confidence_2025_001'
WHERE challenges_id = 1;

ALTER TABLE challenges
ALTER COLUMN gameIdentifier SET NOT NULL;

CREATE UNIQUE INDEX idx_game_identifier_unique
ON challenges(gameIdentifier);
