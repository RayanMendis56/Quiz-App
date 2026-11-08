import React, { useState, useEffect } from 'react';
import './LeaderboardScreen.css';
import { getLeaderboard } from '../api';

const CATEGORIES = [
  { value: '', label: 'All' },
  { value: 'java', label: 'Java' },
  { value: 'python', label: 'Python' },
  { value: 'javascript', label: 'JavaScript' },
];

function LeaderboardScreen({ onBack }) {
  const [category, setCategory] = useState('');
  const [entries, setEntries] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    setError('');

    getLeaderboard(category)
      .then((data) => {
        if (!cancelled) setEntries(data);
      })
      .catch(() => {
        if (!cancelled) setError('Could not load the leaderboard.');
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });

    // Ignore results from an older request if the filter changed meanwhile
    return () => {
      cancelled = true;
    };
  }, [category]);

  return (
    <div className="leaderboard-container">
      <h2>Leaderboard</h2>

      <div className="filter-tabs">
        {CATEGORIES.map((c) => (
          <button
            key={c.value}
            className={`filter-tab ${category === c.value ? 'active' : ''}`}
            onClick={() => setCategory(c.value)}
          >
            {c.label}
          </button>
        ))}
      </div>

      {loading && <p className="leaderboard-message">Loading scores...</p>}
      {error && <p className="error-text">{error}</p>}

      {!loading && !error && entries.length === 0 && (
        <p className="leaderboard-message">No scores yet. Be the first!</p>
      )}

      {!loading && !error && entries.length > 0 && (
        <table className="leaderboard-table">
          <thead>
            <tr>
              <th>Rank</th>
              <th>Player</th>
              <th>Category</th>
              <th>Level</th>
              <th>Score</th>
            </tr>
          </thead>
          <tbody>
            {entries.map((entry, index) => (
              <tr key={entry.id}>
                <td>{index + 1}</td>
                <td>{entry.playerName}</td>
                <td className="category-cell">{entry.category}</td>
                <td className="category-cell">{entry.level || 'N/A'}</td>
                <td>
                  {entry.score}/{entry.total}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      <button className="secondary-button" onClick={onBack}>
        Back to Home
      </button>
    </div>
  );
}

export default LeaderboardScreen;