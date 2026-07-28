package com.learnvault.assessmentevaluation.service;

import com.learnvault.assessmentevaluation.dto.request.QuestionRequest;
import com.learnvault.assessmentevaluation.dto.response.QuestionResponse;

import java.util.List;

public interface QuestionService {
    QuestionResponse addQuestion(Integer assessmentId, QuestionRequest request);
    List<QuestionResponse> getQuestionsByAssessment(Integer assessmentId);
    void deleteQuestion(Integer questionId);
}