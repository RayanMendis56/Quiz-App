import React, { useState } from 'react';
import './App.css';
import WelcomeScreen from './pages/WelcomeScreen';
import QuizScreen from './pages/QuizScreen';
import ScoreScreen from './pages/ScoreScreen';
import { createQuiz, getQuizQuestions } from './api';
import LeaderboardScreen from './pages/LeaderboardScreen';

function App() {
  const [screen, setScreen] = useState('welcome'); // 'welcome' | 'quiz' | 'score' | 'leaderboard'
  const [quizId, setQuizId] = useState(null);
  const [questions, setQuestions] = useState([]);
  const [score, setScore] = useState(null);
  const [playerName, setPlayerName] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleStartQuiz = async ({ name, category, numQ }) => {
    setLoading(true);
    setError('');
    try {
      const title = `${category} quiz for ${name}`;
      const newQuizId = await createQuiz({ category, numQ, title });
      const fetchedQuestions = await getQuizQuestions(newQuizId);

      setPlayerName(name);
      setQuizId(newQuizId);
      setQuestions(fetchedQuestions);
      setScreen('quiz');
    } catch (err) {
      console.error(err);
      setError('Something went wrong starting the quiz. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleFinish = (finalScore) => {
    setScore(finalScore);
    setScreen('score');
  };

  const handleTryAgain = () => {
    setQuizId(null);
    setQuestions([]);
    setScore(null);
    setScreen('welcome');
  };

  return (
    <div className="App">
      {screen === 'welcome' && (
        <>
          <WelcomeScreen
            onStartQuiz={handleStartQuiz}
            onViewLeaderboard={() => setScreen('leaderboard')}
          />
          {loading && <p>Creating your quiz...</p>}
          {error && <p className="error-text">{error}</p>}
        </>
      )}

      {screen === 'quiz' && (
        <QuizScreen
          quizId={quizId}
          questions={questions}
          playerName={playerName}
          onFinish={handleFinish}
        />
      )}

      {screen === 'score' && (
        <ScoreScreen
          playerName={playerName}
          score={score}
          total={questions.length}
          onTryAgain={handleTryAgain}
          onViewLeaderboard={() => setScreen('leaderboard')}
        />
      )}

      {screen === 'leaderboard' && (
        <LeaderboardScreen onBack={() => setScreen('welcome')} />
      )}
    </div>
  );
}

export default App;