const BASE_URL = 'http://localhost:8080';

export async function createQuiz({ category, numQ, title }) {
  const params = new URLSearchParams({ category, numQ, title });
  const response = await fetch(`${BASE_URL}/quiz/create?${params.toString()}`, {
    method: 'POST',
  });

  if (!response.ok) {
    throw new Error('Failed to create quiz');
  }

  return response.json(); // returns the created quiz's id (integer)
}

export async function getQuizQuestions(quizId) {
  const response = await fetch(`${BASE_URL}/quiz/get/${quizId}`);

  if (!response.ok) {
    throw new Error('Failed to fetch quiz questions');
  }

  return response.json();
}

export async function submitQuiz(quizId, responses) {
  const response = await fetch(`${BASE_URL}/quiz/submit/${quizId}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(responses),
  });

  if (!response.ok) {
    throw new Error('Failed to submit quiz');
  }

  return response.json();
}