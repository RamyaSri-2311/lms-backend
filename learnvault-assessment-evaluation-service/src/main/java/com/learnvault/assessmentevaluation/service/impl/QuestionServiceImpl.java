package com.learnvault.assessmentevaluation.service.impl;

import com.learnvault.assessmentevaluation.dto.request.QuestionRequest;
import com.learnvault.assessmentevaluation.dto.response.QuestionResponse;
import com.learnvault.assessmentevaluation.entity.Assessment;
import com.learnvault.assessmentevaluation.entity.Question;
import com.learnvault.assessmentevaluation.exception.ResourceNotFoundException;
import com.learnvault.assessmentevaluation.repository.AssessmentRepository;
import com.learnvault.assessmentevaluation.repository.QuestionRepository;
import com.learnvault.assessmentevaluation.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final AssessmentRepository assessmentRepository;

    @Override
    public QuestionResponse addQuestion(Integer assessmentId, QuestionRequest request) {
        log.info("Adding question to assessment: {}", assessmentId);

        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found with id: " + assessmentId));

        Question question = Question.builder()
                .assessment(assessment)
                .questionText(request.getQuestionText())
                .type(request.getType())
                .options(request.getOptions())
                .correctAnswer(request.getCorrectAnswer())
                .marks(request.getMarks())
                .build();

        Question saved = questionRepository.save(question);
        return mapToResponse(saved);
    }

    @Override
    public List<QuestionResponse> getQuestionsByAssessment(Integer assessmentId) {
        log.info("Fetching questions for assessment: {}", assessmentId);
        return questionRepository.findByAssessment_AssessmentId(assessmentId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteQuestion(Integer questionId) {
        log.info("Deleting question: {}", questionId);
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));
        questionRepository.delete(question);
    }

    private QuestionResponse mapToResponse(Question question) {
        return QuestionResponse.builder()
                .questionId(question.getQuestionId())
                .assessmentId(question.getAssessment().getAssessmentId())
                .questionText(question.getQuestionText())
                .type(question.getType())
                .options(question.getOptions())
                .correctAnswer(question.getCorrectAnswer())
                .marks(question.getMarks())
                .build();
    }
}