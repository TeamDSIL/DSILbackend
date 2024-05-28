package com.ssg.dsilbackend.controller;

import com.ssg.dsilbackend.dto.File.FileDTO;
import com.ssg.dsilbackend.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<List<FileDTO>> uploadFiles(@RequestParam("files") List<MultipartFile> files) {
        System.out.println("들어옴");
        List<FileDTO> uploadedFiles = fileService.uploadFilesSample(files);
        return ResponseEntity.ok(uploadedFiles);
    }
}
