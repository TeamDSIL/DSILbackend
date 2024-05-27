package com.ssg.dsilbackend.service;

import com.ssg.dsilbackend.domain.Inform;
import com.ssg.dsilbackend.dto.Inform.InformDTO;

import java.util.List;


public interface InformService {
    InformDTO createInform(InformDTO informDTO);
    List<InformDTO> getAllInforms();
    InformDTO getInformById(Long id);
    InformDTO updateInform(Long id, InformDTO informDTO);
    void deleteInform(Long id);
}

