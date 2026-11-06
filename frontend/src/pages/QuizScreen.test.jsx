import { act, fireEvent, render, screen, waitFor } from '@testing-library/react';
import QuizScreen from './QuizScreen';
import { submitQuiz } from '../api';

jest.mock('../api', () => ({
  submitQuiz: jest.fn(),
}));

test('retries final submission without adding the last answer again', async () => {
  jest.spyOn(console, 'error').mockImplementation(() => {});
  submitQuiz
    .mockRejectedValueOnce(new Error('Network error'))
    .mockResolvedValueOnce(1);
  const onFinish = jest.fn();

  render(
    <QuizScreen
      quizId={123}
      playerName="Alex"
      questions={[
        {
          id: 1,
          question_text: 'What is 1 + 1?',
          option1: '1',
          option2: '2',
          option3: '3',
          option4: '4',
        },
        {
          id: 2,
          question_text: 'What is 2 + 1?',
          option1: '1',
          option2: '2',
          option3: '3',
          option4: '4',
        },
      ]}
      onFinish={onFinish}
    />,
  );

  fireEvent.click(screen.getByRole('button', { name: '2' }));
  fireEvent.click(screen.getByRole('button', { name: 'Next Question' }));
  fireEvent.click(screen.getByRole('button', { name: '3' }));
  fireEvent.click(screen.getByRole('button', { name: 'Submit Quiz' }));
  expect(await screen.findByText('Something went wrong submitting your quiz.')).toBeInTheDocument();

  fireEvent.click(screen.getByRole('button', { name: 'Submit Quiz' }));

  await waitFor(() => expect(onFinish).toHaveBeenCalledWith(1));
  expect(submitQuiz).toHaveBeenCalledTimes(2);
  const expectedResponses = [
    { id: 1, response: '2' },
    { id: 2, response: '3' },
  ];
  expect(submitQuiz).toHaveBeenNthCalledWith(1, 123, expectedResponses, 'Alex');
  expect(submitQuiz).toHaveBeenNthCalledWith(2, 123, expectedResponses, 'Alex');
});

test('advances after 30 seconds, retaining selected answers and marking skipped answers wrong', async () => {
  jest.useFakeTimers();
  submitQuiz.mockResolvedValue(1);
  const onFinish = jest.fn();

  render(
    <QuizScreen
      quizId={123}
      playerName="Alex"
      questions={[
        {
          id: 1,
          question_text: 'What is 1 + 1?',
          option1: '1',
          option2: '2',
          option3: '3',
          option4: '4',
        },
        {
          id: 2,
          question_text: 'What is 2 + 1?',
          option1: '1',
          option2: '2',
          option3: '3',
          option4: '4',
        },
      ]}
      onFinish={onFinish}
    />,
  );

  expect(screen.getByRole('timer')).toHaveTextContent('30s');
  fireEvent.click(screen.getByRole('button', { name: '2' }));
  act(() => {
    jest.advanceTimersByTime(30000);
  });

  expect(screen.getByText('What is 2 + 1?')).toBeInTheDocument();
  expect(screen.getByRole('timer')).toHaveTextContent('30s');

  await act(async () => {
    jest.advanceTimersByTime(30000);
    await Promise.resolve();
  });

  await waitFor(() => expect(onFinish).toHaveBeenCalledWith(1));
  expect(submitQuiz).toHaveBeenCalledWith(123, [
    { id: 1, response: '2' },
    { id: 2, response: '0' },
  ], 'Alex');
});
