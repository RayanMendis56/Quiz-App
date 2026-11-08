package com.rayan.service;

import com.rayan.model.*;
import com.rayan.repository.LeaderboardRepository;
import com.rayan.repository.QuestionRepository;
import com.rayan.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuizService {

    @Autowired
    QuizRepository quizRepository;
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    LeaderboardRepository leaderboardRepository;

    public ResponseEntity<Integer> createQuiz(String category, String level, int numQ, String title) {
        List<Question> questions = questionRepository.findRandomQuestionsByCategoryAndLevel(category, level, numQ);

        if (questions.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestions(questions);
        Quiz savedQuiz = quizRepository.save(quiz);
        return new ResponseEntity<>(savedQuiz.getId(), HttpStatus.CREATED);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer id) {
        Optional<Quiz> quiz = quizRepository.findById(id);
        List<Question> questionFromDB=quiz.get().getQuestions();

        List<QuestionWrapper> questionsForUser=new ArrayList<>();
        for(Question q: questionFromDB){
            questionsForUser.add(new QuestionWrapper(q.getId(),q.getQuestion_text(),q.getOption1(),q.getOption2(),q.getOption3(),q.getOption4()));
        }
        return new ResponseEntity<>(questionsForUser,HttpStatus.OK);
    }


    public ResponseEntity<Integer> calculateResult(Integer id, List<Response> responses, String playerName) {
        Optional<Quiz> quizOpt = quizRepository.findById(id);
        if (quizOpt.isEmpty()) {
            return new ResponseEntity<>(0, HttpStatus.NOT_FOUND);
        }
        Quiz quiz = quizOpt.get();
        List<Question> questions = quiz.getQuestions();

        Map<Integer, Question> questionMap = new HashMap<>();
        for (Question q : questions) {
            questionMap.put(q.getId(), q);
        }

        int right = 0;
        for (Response resp : responses) {
            Question question = questionMap.get(resp.getId());
            if (question == null) continue;

            try {
                Integer given = Integer.valueOf(resp.getResponse());
                if (given.equals(question.getCorrect_answer())) {
                    right++;
                }
            } catch (NumberFormatException e) {
                // invalid answer format - treat as incorrect
            }
        }

        saveToLeaderboard(playerName, questions, right);
        return new ResponseEntity<>(right, HttpStatus.OK);
    }

    private void saveToLeaderboard(String playerName, List<Question> questions, int score) {
        try {
            if (questions.isEmpty()) return;

            LeaderboardEntry entry = new LeaderboardEntry();
            entry.setPlayerName(playerName);
            entry.setCategory(questions.get(0).getCategory());
            entry.setLevel(questions.get(0).getLevel());
            entry.setScore(score);
            entry.setTotal(questions.size());
            leaderboardRepository.save(entry);
        } catch (Exception e) {
            e.printStackTrace(); // a leaderboard failure should never block the player's score
        }
    }


}
