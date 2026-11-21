package com.rayan.controller;

import com.rayan.model.Question;
import com.rayan.model.QuestionWrapper;
import com.rayan.model.Response;
import com.rayan.service.QuizService;
import jakarta.validation.constraints.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("quiz")
public class QuizController {

    @Autowired
    QuizService quizService;

    @PostMapping("create")
    public ResponseEntity<Integer> createQuiz(
            @RequestParam @NotBlank(message = "Category is required") String category,
            @RequestParam @Pattern(regexp = "easy|medium|hard", message = "Level must be easy, medium, or hard") String level,
            @RequestParam @Min(value = 1, message = "Must be at least 1") @Max(value = 20, message = "Must be at most 20") int numQ,
            @RequestParam @NotBlank(message = "Title is required") @Size(max = 100, message = "Title must be 100 characters or less") String title) {
        return quizService.createQuiz(category, level, numQ, title);
    }

    @GetMapping("get/{id}")
    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(@PathVariable Integer id){
        return quizService.getQuizQuestions(id);
    }

    @PostMapping("submit/{id}")
    public ResponseEntity<Integer> submitAnswer(@PathVariable Integer id,
            @RequestParam(defaultValue = "Anonymous") @Size(max = 50, message = "Player name must be 50 characters or less") String playerName,
            @RequestBody List<Response> responses) {
        return quizService.calculateResult(id, responses, playerName);
    }


}
