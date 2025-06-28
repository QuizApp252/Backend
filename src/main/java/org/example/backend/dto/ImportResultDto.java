package org.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportResultDto {
    private int addedCount;
    private int skippedCount;
    private List<String> addedQuestions;
    private List<String> skippedQuestions;
}
