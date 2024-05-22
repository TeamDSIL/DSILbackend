package com.ssg.dsilbackend.dto.Inform;

import lombok.*;

import java.time.LocalDate;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InformDTO {
    private Long id;
    private String category;
    private String title;
    private String contents;
    private LocalDate postDate;
    private LocalDate modifiedDate;
    private String img;
}

