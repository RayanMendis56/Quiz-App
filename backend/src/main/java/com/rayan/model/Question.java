package com.rayan.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Question text is required")
    @Size(max = 255, message = "Question text must be 255 characters or less")
    private String question_text;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Level is required")
    @Pattern(regexp = "easy|medium|hard", message = "Level must be easy, medium, or hard")
    private String level;

    @NotBlank(message = "Option 1 is required")
    @Size(max = 255, message = "Option 1 must be 255 characters or less")
    private String option1;

    @NotBlank(message = "Option 2 is required")
    @Size(max = 255, message = "Option 2 must be 255 characters or less")
    private String option2;

    @NotBlank(message = "Option 3 is required")
    @Size(max = 255, message = "Option 3 must be 255 characters or less")
    private String option3;

    @NotBlank(message = "Option 4 is required")
    @Size(max = 255, message = "Option 4 must be 255 characters or less")
    private String option4;

    @Min(value = 1, message = "Correct answer must be between 1 and 4")
    @Max(value = 4, message = "Correct answer must be between 1 and 4")
    private Integer correct_answer;

}
