package com.ssg.dsilbackend.service;


import com.ssg.dsilbackend.domain.Inform;
import com.ssg.dsilbackend.dto.Inform.InformDTO;
import com.ssg.dsilbackend.repository.InformRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InformServiceImpl implements InformService {

    @Autowired
    private InformRepository informRepository;

    @Override
    @Transactional
    public InformDTO createInform(InformDTO informDTO) {
        Inform inform = Inform.builder()
                .category(informDTO.getCategory())
                .title(informDTO.getTitle())
                .contents(informDTO.getContents())
                .postDate(informDTO.getPostDate())
                .modifiedDate(informDTO.getModifiedDate())
                .img(informDTO.getImg())
                .build();
        inform = informRepository.save(inform);
        return new InformDTO(inform.getId(), inform.getCategory(), inform.getTitle(), inform.getContents(),
                inform.getPostDate(), inform.getModifiedDate(), inform.getImg());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InformDTO> getAllInforms() {
        return informRepository.findAll().stream()
                .map(inform -> new InformDTO(inform.getId(), inform.getCategory(), inform.getTitle(),
                        inform.getContents(), inform.getPostDate(), inform.getModifiedDate(),
                        inform.getImg()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InformDTO getInformById(Long id) {
        Inform inform = informRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inform not found for this id :: " + id));
        return new InformDTO(inform.getId(), inform.getCategory(), inform.getTitle(), inform.getContents(),
                inform.getPostDate(), inform.getModifiedDate(), inform.getImg());
    }

    @Override
    @Transactional
    public InformDTO updateInform(Long id, InformDTO informDTO) {
        Inform inform = informRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inform not found for this id :: " + id));
        inform.updateInformation(informDTO.getCategory(), informDTO.getTitle(), informDTO.getContents(),
                informDTO.getPostDate(), informDTO.getModifiedDate(), informDTO.getImg());
        informRepository.save(inform);
        return new InformDTO(inform.getId(), inform.getCategory(), inform.getTitle(), inform.getContents(),
                inform.getPostDate(), inform.getModifiedDate(), inform.getImg());
    }


    @Override
    @Transactional
    public void deleteInform(Long id) {
        Inform inform = informRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inform not found for this id :: " + id));
        informRepository.delete(inform);
    }
}