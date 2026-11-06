import React, { useEffect, useRef, useState } from 'react';
import './QuizScreen.css';
import { submitQuiz } from '../api';

const QUESTION_TIME_LIMIT = 30;

function QuizScreen({ quizId, questions, playerName, onFinish }) {
  const [currentIndex, setCurrentIndex] = useState(0);
  const [selectedOption, setSelectedOption] = useState(null);
  const [responses, setResponses] = useState([]);
  const [pendingResponses, setPendingResponses] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [timeLeft, setTimeLeft] = useState(QUESTION_TIME_LIMIT);
  const handleNextRef = useRef();

  const currentQuestion = questions[currentIndex];
  const isLastQuestion = currentIndex === questions.length - 1;

  const handleOptionClick = (optionNumber) => {
    setSelectedOption(optionNumber);
  };

  const submitResponses = async (responsesToSubmit) => {
    setSubmitting(true);
    try {
      const score = await submitQuiz(quizId, responsesToSubmit, playerName);
      onFinish(score);
    } catch (err) {
      console.error(err);
      setError('Something went wrong submitting your quiz.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleNext = async (timedOut = false) => {
    if (isLastQuestion && pendingResponses) {
      setError('');
      await submitResponses(pendingResponses);
      return;
    }

    if (selectedOption === null) {
      if (!timedOut) {
        setError('Please select an answer before continuing.');
        return;
      }
    }
    setError('');

    const updatedResponses = [
      ...responses,
      {
        id: currentQuestion.id,
        response: selectedOption === null ? '0' : String(selectedOption),
      },
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

  handleNextRef.current = handleNext;

  useEffect(() => {
    setTimeLeft(QUESTION_TIME_LIMIT);
    if (!currentQuestion || pendingResponses || submitting) {
      return undefined;
    }

    let secondsRemaining = QUESTION_TIME_LIMIT;
    const timerId = window.setInterval(() => {
      secondsRemaining -= 1;
      setTimeLeft(secondsRemaining);

      if (secondsRemaining === 0) {
        window.clearInterval(timerId);
        handleNextRef.current(true);
      }
    }, 1000);

    return () => window.clearInterval(timerId);
  }, [currentIndex, currentQuestion, pendingResponses, submitting]);

  if (!currentQuestion) {
    return <p>Loading question...</p>;
  }

  return (
    <div className="quiz-container">
      <div className="quiz-progress">
        Question {currentIndex + 1} of {questions.length}
      </div>

      <div className="quiz-timer" role="timer" aria-label="Time remaining">
        Time remaining: {timeLeft}s
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