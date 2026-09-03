package com.rayan.service;

import com.rayan.Question;
import com.rayan.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    @Autowired
    QuestionRepository questionRepository;

    public ResponseEntity<List<Question>> getAllQuestions(){
        try{
            return new ResponseEntity<>(questionRepository.findAll(), HttpStatus.OK);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<List<Question>> getQuestionsByCategory(String category) {
        try{
            return new ResponseEntity<>(questionRepository.findByCategory(category), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<String> addQuestion(Question question) {
        try{
            questionRepository.save(question);
            return new ResponseEntity<>("Question added successfully", HttpStatus.CREATED);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("Failed to add question", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<String> deleteQuestion(Integer id) {
        try{
            if(!questionRepository.existsById(id)){
                return new ResponseEntity<>("Question with id " + id + " does not exist", HttpStatus.NOT_FOUND);
            }
            questionRepository.deleteById(id);
            return new ResponseEntity<>("Question with id " + id + " deleted successfully", HttpStatus.OK);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("Failed to delete question", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<String> updateQuestion(Integer id, Question updatedQuestion) {
        try {
            Optional<Question> existingQuestion = questionRepository.findById(id);
            if (existingQuestion.isEmpty()) {
                return new ResponseEntity<>("Question with id " + id + " not found", HttpStatus.NOT_FOUND);
            }

            Question question = existingQuestion.get();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("Failed to update question", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
