import React, { useState } from 'react';
import './QuizScreen.css';
import { submitQuiz } from '../api';

function QuizScreen({ quizId, questions, playerName }) {
  const [currentIndex, setCurrentIndex] = useState(0);
  const [selectedOption, setSelectedOption] = useState(null);
  const [responses, setResponses] = useState([]);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [result, setResult] = useState(null);

  const currentQuestion = questions[currentIndex];
  const isLastQuestion = currentIndex === questions.length - 1;

  const handleOptionClick = (optionNumber) => {
    setSelectedOption(optionNumber);
  };

  const handleNext = async () => {
    if (selectedOption === null) {
      setError('Please select an answer before continuing.');
      return;
    }
    setError('');

    const updatedResponses = [
      ...responses,
      { id: currentQuestion.id, response: String(selectedOption) },
    ];
    setResponses(updatedResponses);
    setSelectedOption(null);

    if (isLastQuestion) {
      setSubmitting(true);
      try {
        const score = await submitQuiz(quizId, updatedResponses);
        setResult(score);
      } catch (err) {
        console.error(err);
        setError('Something went wrong submitting your quiz.');
      } finally {
        setSubmitting(false);
      }
    } else {
      setCurrentIndex(currentIndex + 1);
    }
  };

  if (result !== null) {
    return (
      <div className="quiz-container">
        <h2>Nice work, {playerName}!</h2>
        <p className="score-text">
          You scored {result} out of {questions.length}
        </p>
      </div>
    );
  }

  if (!currentQuestion) {
    return <p>Loading question...</p>;
  }

  return (
    <div className="quiz-container">
      <div className="quiz-progress">
        Question {currentIndex + 1} of {questions.length}
      </div>

      <h2 className="question-text">{currentQuestion.question_text}</h2>

      <div className="options-grid">
        {[
          currentQuestion.option1,
          currentQuestion.option2,
          currentQuestion.option3,
          currentQuestion.option4,
        ].map((option, idx) => {
          const optionNumber = idx + 1;
          return (
            <button
              key={optionNumber}
              className={`option-button ${selectedOption === optionNumber ? 'selected' : ''}`}
              onClick={() => handleOptionClick(optionNumber)}
            >
              {option}
            </button>
          );
        })}
      </div>

      {error && <p className="error-text">{error}</p>}

      <button className="next-button" onClick={handleNext} disabled={submitting}>
        {submitting ? 'Submitting...' : isLastQuestion ? 'Submit Quiz' : 'Next Question'}
      </button>
    </div>
  );
}

export default QuizScreen;