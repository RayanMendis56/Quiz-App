import React from 'react';
import './ScoreScreen.css';

function getMessage(percentage) {
  if (percentage === 100) return 'Perfect score! Outstanding!';
  if (percentage >= 80) return 'Great job!';
  if (percentage >= 50) return 'Good effort, keep practicing!';
  return 'Keep learning, you will get there!';
}

function ScoreScreen({ playerName, score, total, onTryAgain, onViewLeaderboard}) {
  const percentage = total > 0 ? Math.round((score / total) * 100) : 0;
  const correctPercentage = total > 0 ? (score / total) * 100 : 0;

  return (
    <div className="score-container">
      <h2>Quiz Complete, {playerName}!</h2>

      <div
        className="score-circle"
        style={{ '--correct-percentage': `${correctPercentage}%` }}
      >
        <span className="score-percentage">{percentage}%</span>
      </div>

      <p className="score-detail">
        You got {score} out of {total} correct
      </p>
      <p className="score-message">{getMessage(percentage)}</p>

      <button className="try-again-button" onClick={onTryAgain}>
        Try Again
      </button>
        <button className="secondary-button" onClick={onViewLeaderboard}>
        View Leaderboard
        </button>
    </div>
  );
}

export default ScoreScreen;