package com.rayan.service;

import com.rayan.exception.ResourceNotFoundException;
import com.rayan.model.Question;
import com.rayan.model.QuestionWrapper;
import com.rayan.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    QuestionRepository questionRepository;

    public ResponseEntity<List<QuestionWrapper>> getAllQuestions() {
        return new ResponseEntity<>(toWrappers(questionRepository.findAll()), HttpStatus.OK);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuestionsByCategory(String category) {
        return new ResponseEntity<>(toWrappers(questionRepository.findByCategory(category)), HttpStatus.OK);
    }

    public ResponseEntity<String> addQuestion(Question question) {
        questionRepository.save(question);
        return new ResponseEntity<>("Question added successfully", HttpStatus.CREATED);
    }

    public ResponseEntity<String> deleteQuestion(Integer id) {
        if (!questionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Question with id " + id + " not found");
        }
        questionRepository.deleteById(id);
        return new ResponseEntity<>("Question with id " + id + " deleted successfully", HttpStatus.OK);
    }

    public ResponseEntity<String> updateQuestion(Integer id, Question updatedQuestion) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question with id " + id + " not found"));

        question.setQuestion_text(updatedQuestion.getQuestion_text());
        question.setCategory(updatedQuestion.getCategory());
        question.setLevel(updatedQuestion.getLevel());
        question.setOption1(updatedQuestion.getOption1());
        question.setOption2(updatedQuestion.getOption2());
        question.setOption3(updatedQuestion.getOption3());
        question.setOption4(updatedQuestion.getOption4());
        question.setCorrect_answer(updatedQuestion.getCorrect_answer());

        questionRepository.save(question);
        return new ResponseEntity<>("Question updated successfully", HttpStatus.OK);
    }

    private List<QuestionWrapper> toWrappers(List<Question> questions) {
        List<QuestionWrapper> wrappers = new ArrayList<>();
        for (Question q : questions) {
            wrappers.add(new QuestionWrapper(q.getId(), q.getQuestion_text(),
                    q.getOption1(), q.getOption2(), q.getOption3(), q.getOption4()));
        }
        return wrappers;
    }

}
