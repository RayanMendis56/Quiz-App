package com.rayan.service;

import com.rayan.Question;
import com.rayan.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    @Autowired
    QuestionRepository questionRepository;

    public List<Question> getAllQuestions(){
        return questionRepository.findAll();
    }

    public List<Question> getQuestionsByCategory(String category) {
           return questionRepository.findByCategory(category);
    }

    public String addQuestion(Question question) {
        questionRepository.save(question);
        return "Question added successfully";
    }

    public String deleteQuestion(Integer id) {
        if(!questionRepository.existsById(id)){
            return "Question with id " + id + " does not exist";
        }
        questionRepository.deleteById(id);
        return "Question with id " + id + " deleted successfully";
    }

    public String updateQuestion(int id, Question updatedQuestion) {
        Optional<Question> existingQuestion = questionRepository.findById(id);
        if (existingQuestion.isEmpty()) {
            return "Question with id " + id + " not found";
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
        return "Question updated successfully";
    }

}
