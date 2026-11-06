import React, { useState } from 'react';
import './QuizScreen.css';
import { submitQuiz } from '../api';

function QuizScreen({ quizId, questions, onFinish }) {
  const [currentIndex, setCurrentIndex] = useState(0);
  const [selectedOption, setSelectedOption] = useState(null);
  const [responses, setResponses] = useState([]);
  const [pendingResponses, setPendingResponses] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  const currentQuestion = questions[currentIndex];
  const isLastQuestion = currentIndex === questions.length - 1;

  const handleOptionClick = (optionNumber) => {
    setSelectedOption(optionNumber);
  };

  const submitResponses = async (responsesToSubmit) => {
    setSubmitting(true);
    try {
      const score = await submitQuiz(quizId, responsesToSubmit);
      onFinish(score);
    } catch (err) {
      console.error(err);
      setError('Something went wrong submitting your quiz.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleNext = async () => {
    if (isLastQuestion && pendingResponses) {
      setError('');
      await submitResponses(pendingResponses);
      return;
    }

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
      setPendingResponses(updatedResponses);
      await submitResponses(updatedResponses);
    } else {
      setCurrentIndex(currentIndex + 1);
    }
  };

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