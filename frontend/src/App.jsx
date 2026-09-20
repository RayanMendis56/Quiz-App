import React, { useState } from 'react';
import './App.css';
import WelcomeScreen from './pages/WelcomeScreen';
import QuizScreen from './pages/QuizScreen';
import { createQuiz, getQuizQuestions } from './api';

function App() {
  const [screen, setScreen] = useState('welcome'); // 'welcome' | 'quiz' | 'score'
  const [quizId, setQuizId] = useState(null);
  const [questions, setQuestions] = useState([]);
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

  return (
    <div className="App">
      {screen === 'welcome' && (
        <>
          <WelcomeScreen onStartQuiz={handleStartQuiz} />
          {loading && <p>Creating your quiz...</p>}
          {error && <p className="error-text">{error}</p>}
        </>
      )}

      {screen === 'quiz' && (
        <QuizScreen
          quizId={quizId}
          questions={questions}
          playerName={playerName}
        />
      )}
    </div>
  );
}

export default App;