package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.ImportResultDto;
import org.example.backend.model.Question;
import org.example.backend.repository.IQuestionRepository;
import org.example.backend.service.QuestionExcelService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/question")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class QuestionController {

    private final IQuestionRepository questionRepo;
    private final QuestionExcelService excelService;

    // 1. Import Excel
    @PostMapping("/import")
    public ResponseEntity<?> importQuestions(@RequestParam("file") MultipartFile file) {
        // Kiểm tra MIME type
        String contentType = file.getContentType();
        if (file.isEmpty() || contentType == null ||
                (!contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                        && !contentType.equals("application/vnd.ms-excel"))) {

            return ResponseEntity.badRequest().body("❌ File không hợp lệ. Chỉ chấp nhận .xlsx hoặc .xls");
        }

        // Nếu đúng định dạng, gọi service xử lý
        ImportResultDto result = excelService.importFromExcel(file);
        return ResponseEntity.ok(result);
    }
}

