package com.rayan.service;

import com.rayan.model.Question;
import com.rayan.model.QuestionWrapper;
import com.rayan.model.Quiz;
import com.rayan.model.Response;
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

    public ResponseEntity<String> createQuiz(String category, int numQ, String title) {

        List<Question> questions=questionRepository.findRandomQuestionsByCategory(category,numQ);

        Quiz quiz=new Quiz();
        quiz.setTitle(title);
        quiz.setQuestions(questions);
        quizRepository.save(quiz);

        return new ResponseEntity<>("Success",HttpStatus.CREATED);
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


    public ResponseEntity<Integer> calculateResult(Integer id, List<Response> responses) {
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
        return new ResponseEntity<>(right, HttpStatus.OK);
    }
}
