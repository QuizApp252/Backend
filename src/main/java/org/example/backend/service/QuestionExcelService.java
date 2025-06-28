package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.backend.dto.ImportResultDto;
import org.example.backend.model.Answer;
import org.example.backend.model.Question;
import org.example.backend.repository.IAnswerRepository;
import org.example.backend.repository.IQuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionExcelService {

    private final IQuestionRepository questionRepo;
    private final IAnswerRepository answerRepo;

    public ImportResultDto importFromExcel(MultipartFile file) {
        int added = 0, skipped = 0;
        List<String> addedQuestions = new ArrayList<>();
        List<String> skippedQuestions = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String content = getCellValue(row.getCell(0));
                String category = getCellValue(row.getCell(1));
                String diffStr = getCellValue(row.getCell(2)).toUpperCase();
                String typeStr = getCellValue(row.getCell(3)).toUpperCase();
                String answerStr = getCellValue(row.getCell(4));
                String correctStr = getCellValue(row.getCell(5));

                if (questionRepo.existsByContent(content)) {
                    skipped++;
                    skippedQuestions.add(content);
                    continue;
                }

                Question.Difficulty difficulty = Question.Difficulty.valueOf(diffStr);
                Question.QuestionType type = Question.QuestionType.valueOf(typeStr);

                Question question = new Question();
                question.setContent(content);
                question.setCategory(category);
                question.setDifficulty(difficulty);
                question.setQuestionType(type);
                question = questionRepo.save(question);
                added++;
                addedQuestions.add(content);

                if (!answerStr.isEmpty()) {
                    String[] answers = answerStr.split("\\|");
                    Set<String> correctAnswers = Arrays.stream(correctStr.split("\\|"))
                            .map(String::trim)
                            .collect(Collectors.toSet());

                    for (int j = 0; j < answers.length; j++) {
                        String ans = answers[j].trim();
                        Answer a = new Answer();
                        a.setQuestion(question);
                        a.setContent(ans);
                        a.setCorrect(correctAnswers.contains(ans));
                        a.setDisplayOrder(j);
                        answerRepo.save(a);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi đọc file Excel: " + e.getMessage());
        }

        return new ImportResultDto(added, skipped, addedQuestions, skippedQuestions);
    }

    private String getCellValue(Cell cell) {
        return (cell == null) ? "" : cell.toString().trim();
    }
}
