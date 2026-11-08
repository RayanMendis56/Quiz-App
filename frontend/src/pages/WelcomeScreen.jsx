import React, { useState } from 'react';
import './WelcomeScreen.css';

const DEFAULT_NUM_QUESTIONS = 5;

function WelcomeScreen({ onStartQuiz , onViewLeaderboard}) {
  const [name, setName] = useState('');
  const [category, setCategory] = useState('java');
  const [level, setLevel] = useState('easy');
  const [error, setError] = useState('');

  const handleStart = () => {
    if (!name.trim()) {
      setError('Please enter your name to continue.');
      return;
    }
    setError('');
    onStartQuiz({ name: name.trim(), category, level, numQ: DEFAULT_NUM_QUESTIONS });
  };

  return (
    <div className="welcome-container">
      <h1>Quiz App</h1>
      <p>Test your knowledge — enter your name and pick a category to begin.</p>

      <div className="form-group">
        <label htmlFor="name">Your Name</label>
        <input
          id="name"
          type="text"
          placeholder="Enter your name"
          value={name}
          onChange={(e) => setName(e.target.value)}
        />
      </div>

      <div className="form-group">
        <label htmlFor="category">Category</label>
        <select
          id="category"
          value={category}
          onChange={(e) => setCategory(e.target.value)}
        >
          <option value="java">Java</option>
          <option value="python">Python</option>
          <option value="javascript">JavaScript</option>
        </select>
      </div>

      <div className="form-group">
        <label htmlFor="level">Difficulty</label>
        <select
          id="level"
          value={level}
          onChange={(e) => setLevel(e.target.value)}
        >
          <option value="easy">Easy</option>
          <option value="medium">Medium</option>
          <option value="hard">Hard</option>
        </select>
      </div>

      {error && <p className="error-text">{error}</p>}

      <button className="start-button" onClick={handleStart}>
        Start Quiz
      </button>
      <button className="secondary-button" onClick={onViewLeaderboard}>
        View Leaderboard
      </button>
    </div>
  );
}

export default WelcomeScreen;