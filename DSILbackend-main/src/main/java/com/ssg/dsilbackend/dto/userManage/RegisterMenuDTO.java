package com.ssg.dsilbackend.dto.userManage;

import jakarta.persistence.Column;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RegisterMenuDTO {

    private String name;
    private Long price;
    private MultipartFile img;
    private String imgUrl;
    private String menuInfo;
}
