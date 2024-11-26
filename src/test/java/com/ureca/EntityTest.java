package com.ureca;

import com.ureca.profile.domain.Designer;
import com.ureca.profile.domain.Pet;
import com.ureca.profile.infrastructure.DesignerRepository;
import com.ureca.profile.infrastructure.PetRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EntityTest {

    private static final Logger logger = LoggerFactory.getLogger(EntityTest.class);

    @Autowired DesignerRepository designerRepository;

    @Autowired PetRepository petRepository;

    // 조회 테스트

    // 디자이너
    @Test
    void seleteDesignerTest() {
        Designer designer =
                designerRepository
                        .findById(1L)
                        .orElseThrow(() -> new RuntimeException("data not found"));
        logger.info("데이터 조회 확인: { " + designer.toString() + " }");
    }

    // 반려견
    @Test
    void seletepetTest() {
        List<Pet> petList = petRepository.findByCustomerCustomerId(2L);
        logger.info("Designer 데이터 조회: " + petList.toString());
    }
}
