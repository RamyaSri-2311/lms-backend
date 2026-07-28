package com.learnvault.assessmentevaluation.controller;

import com.learnvault.assessmentevaluation.dto.request.QuestionRequest;
import com.learnvault.assessmentevaluation.dto.response.QuestionResponse;
import com.learnvault.assessmentevaluation.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assessments/{assessmentId}/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    public ResponseEntity<QuestionResponse> addQuestion(@PathVariable Integer assessmentId,
                                                        @RequestBody QuestionRequest request) {
        return new ResponseEntity<>(questionService.addQuestion(assessmentId, request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<QuestionResponse>> getQuestionsByAssessment(@PathVariable Integer assessmentId) {
        return ResponseEntity.ok(questionService.getQuestionsByAssessment(assessmentId));
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Integer questionId) {
        questionService.deleteQuestion(questionId);
        return ResponseEntity.noContent().build();
    }
}