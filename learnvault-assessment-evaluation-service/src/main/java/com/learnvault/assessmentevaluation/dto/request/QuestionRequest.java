package com.learnvault.assessmentevaluation.dto.request;

import com.learnvault.assessmentevaluation.entity.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionRequest {
    private String questionText;
    private QuestionType type;
    private String options;
    private String correctAnswer;
    private Integer marks;
}