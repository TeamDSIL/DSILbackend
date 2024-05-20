package com.ssg.dsilbackend.controller;

import com.ssg.dsilbackend.dto.File.FileDTO;
import com.ssg.dsilbackend.dto.Inform.InformDTO;
import com.ssg.dsilbackend.service.FileService;
import com.ssg.dsilbackend.service.InformService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/informs")
@RequiredArgsConstructor
public class InformController {

    @Autowired
    private final InformService informService;
    @Autowired
    private final FileService fileService;

    // 공지사항 생성
    @PostMapping("/")
    public ResponseEntity<InformDTO> createInform(@RequestPart("inform") InformDTO informDTO,
                                                  @RequestPart(value = "file", required = false) MultipartFile file) {
        if (file != null) {
            List<MultipartFile> files = List.of(file); // MultipartFile을 List로 변환
            List<FileDTO> fileDTOs = fileService.uploadFiles(files, "informs");
            if (!fileDTOs.isEmpty()) {
                informDTO.setImg(fileDTOs.get(0).getUploadFileUrl());
            }
        }
        InformDTO newInform = informService.createInform(informDTO);
        return ResponseEntity.ok(newInform);
    }

    // 전체 공지사항 조회
    @GetMapping("/")
    public ResponseEntity<List<InformDTO>> getAllInforms() {
        List<InformDTO> informs = informService.getAllInforms();
        return ResponseEntity.ok(informs);
    }

    // 특정 공지사항 조회입니다
    @GetMapping("/{id}")
    public ResponseEntity<InformDTO> getInformById(@PathVariable Long id) {
        InformDTO inform = informService.getInformById(id);
        return ResponseEntity.ok(inform);
    }


    // 공지사항 업데이트
    @PutMapping("/{id}")
    public ResponseEntity<InformDTO> updateInform(@PathVariable Long id, @RequestPart("inform") InformDTO informDTO,
                                                  @RequestPart(value = "file", required = false) MultipartFile file) {
        if (file != null) {
            List<MultipartFile> files = List.of(file); // MultipartFile을 List로 변환
            List<FileDTO> fileDTOs = fileService.uploadFiles(files, "informs");
            if (!fileDTOs.isEmpty()) {
                informDTO.setImg(fileDTOs.get(0).getUploadFileUrl());
            }
        }
            InformDTO updatedInform = informService.updateInform(id, informDTO);
            return ResponseEntity.ok(updatedInform);
        }


    // 공지사항 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInform(@PathVariable Long id) {
        informService.deleteInform(id);
        return ResponseEntity.noContent().build();
    }
}

