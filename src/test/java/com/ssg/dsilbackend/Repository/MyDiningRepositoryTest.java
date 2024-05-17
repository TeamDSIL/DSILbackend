package com.ssg.dsilbackend.Repository;

import com.ssg.dsilbackend.dto.myDinig.MydiningReserveDTO;
import com.ssg.dsilbackend.repository.MemberRepository;
import com.ssg.dsilbackend.repository.ReservationRepository;
import com.ssg.dsilbackend.service.MyDiningServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Log4j2
public class MyDiningRepositoryTest {
    @Autowired
<<<<<<< HEAD
    private MemberRepository membersRepository;
=======
    private MemberRepository memberRepository;
>>>>>>> 6e6f942ebc970aba78ff2502983b4892db178252

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ModelMapper modelMapper;

    @InjectMocks
    private MyDiningServiceImpl myDiningService;

    @Test
    public void testGetMydiningListById() {
        Long memberId = 1L;
        List<MydiningReserveDTO> mydiningReserveDTOList = myDiningService.getMydiningReserveListById(memberId);
        System.out.println(mydiningReserveDTOList);

    }


}
