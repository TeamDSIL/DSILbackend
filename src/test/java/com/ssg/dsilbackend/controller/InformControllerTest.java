//package com.ssg.dsilbackend.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.ssg.dsilbackend.dto.Inform.InformDTO;
//import com.ssg.dsilbackend.service.InformService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.LocalDate;
//import java.util.Arrays;
//
//import static org.mockito.BDDMockito.given;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(InformController.class)
//public class InformControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private InformService informService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private InformDTO sampleInform;
//
//    @BeforeEach
//    void setUp() {
//        sampleInform = new InformDTO(1L, "Category", "Title", "Content", LocalDate.now(), LocalDate.now(), "filepath");
//    }
//
//    @Test
//    void testGetAllInforms() throws Exception {
//        given(informService.getAllInforms()).willReturn(Arrays.asList(sampleInform));
//
//        mockMvc.perform(get("/api/informs/")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(sampleInform.getId()))
//                .andExpect(jsonPath("$[0].title").value(sampleInform.getTitle()));
//    }
//
//    @Test
//    void testGetInformById() throws Exception {
//        given(informService.getInformById(1L)).willReturn(sampleInform);
//
//        mockMvc.perform(get("/api/informs/{id}", 1L)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(sampleInform.getId()))
//                .andExpect(jsonPath("$.title").value(sampleInform.getTitle()));
//    }
//
//    @Test
//    void testCreateInform() throws Exception {
//        given(informService.createInform(sampleInform)).willReturn(sampleInform);
//
//        mockMvc.perform(post("/api/informs/")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(sampleInform)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(sampleInform.getId()))
//                .andExpect(jsonPath("$.title").value(sampleInform.getTitle()));
//    }
//
//    @Test
//    void testUpdateInform() throws Exception {
//        given(informService.updateInform(1L, sampleInform)).willReturn(sampleInform);
//
//        mockMvc.perform(put("/api/informs/{id}", 1L)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(sampleInform)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(sampleInform.getId()))
//                .andExpect(jsonPath("$.title").value(sampleInform.getTitle()));
//    }
//
//    @Test
//    void testDeleteInform() throws Exception {
//        mockMvc.perform(delete("/api/informs/{id}", 1L)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNoContent());
//    }
//}
