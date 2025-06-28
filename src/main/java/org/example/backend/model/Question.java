package org.example.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(columnDefinition = "TEXT")
    private String content;
    private String category;
    private Difficulty difficulty = Difficulty.EASY;
    @Column(name = "question_type")
    private QuestionType questionType = QuestionType.SINGLE_CHOICE;
    @Column(name = "create_at")
    private LocalDateTime createAt = LocalDateTime.now();
    @Column(name = "update_at")
    private LocalDateTime updateAt;
    @Column(name = "is_active")
    private boolean isActive = true;
    public enum Difficulty{
        EASY, MEDIUM, HARD
    }
    public enum QuestionType{
        SINGLE_CHOICE, MULTIPLE_CHOICE,
        SHORT_ANSWER, ESSAY,
        DRAG_DROP_ORDER, MATCHING,
        FILL_IN_THE_BLANK, MEDIA_BASED,
        TRUE_FALSE, NUMERIC_INPUT
    }
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers;
}
