package com.learnvault.assessmentevaluation.service.impl;

import com.learnvault.assessmentevaluation.dto.request.QuestionRequest;
import com.learnvault.assessmentevaluation.dto.response.QuestionResponse;
import com.learnvault.assessmentevaluation.entity.Assessment;
import com.learnvault.assessmentevaluation.entity.Question;
import com.learnvault.assessmentevaluation.entity.enums.QuestionType;
import com.learnvault.assessmentevaluation.exception.ResourceNotFoundException;
import com.learnvault.assessmentevaluation.repository.AssessmentRepository;
import com.learnvault.assessmentevaluation.repository.QuestionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionServiceImplTest {

    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private AssessmentRepository assessmentRepository;

    @InjectMocks
    private QuestionServiceImpl questionService;

    private Assessment buildAssessment() {
        return Assessment.builder().assessmentId(1).build();
    }

    private Question buildQuestion(Assessment assessment) {
        return Question.builder()
                .questionId(50).assessment(assessment)
                .questionText("2+2?").type(QuestionType.MCQ)
                .options("2,3,4").correctAnswer("4").marks(10).build();
    }

    @Test
    void addQuestion_success_savesAndReturnsResponse() {
        // Arrange
        Assessment assessment = buildAssessment();
        QuestionRequest request = QuestionRequest.builder()
                .questionText("2+2?").type(QuestionType.MCQ)
                .options("2,3,4").correctAnswer("4").marks(10).build();
        when(assessmentRepository.findById(1)).thenReturn(Optional.of(assessment));
        when(questionRepository.save(any(Question.class))).thenReturn(buildQuestion(assessment));

        // Act
        QuestionResponse response = questionService.addQuestion(1, request);

        // Assert
        assertEquals(50, response.getQuestionId());
        assertEquals("4", response.getCorrectAnswer());
        verify(questionRepository).save(any(Question.class));
    }

    @Test
    void addQuestion_assessmentNotFound_throwsResourceNotFoundException() {
        // Arrange
        QuestionRequest request = QuestionRequest.builder().questionText("Q").build();
        when(assessmentRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> questionService.addQuestion(99, request));
    }

    @Test
    void deleteQuestion_notFound_throwsResourceNotFoundException() {
        // Arrange
        when(questionRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> questionService.deleteQuestion(99));
    }
}
