package com.vagas.api.controller;

import com.vagas.api.mock.MockFactory;
import com.vagas.api.model.BeneficioModel;
import com.vagas.api.model.input.BeneficioInput;
import com.vagas.domain.model.Beneficio;
import com.vagas.domain.service.BeneficioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Testes do controller de BeneficioController")
class BeneficioControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private BeneficioService beneficioService;

    @Autowired
    private MockFactory mockFactory;

    @Test
    @DisplayName("Deve salvar um beneficio e retornar 201!")
    void deveSalvarUmBeneficioERetornar201CREATED() {
        Beneficio beneficioMock = mockFactory.getBeneficio();
        Beneficio beneficioSalvoMock = mockFactory.getBeneficio();
        beneficioSalvoMock.setId(1L);

        BeneficioInput beneficioInputMock = mockFactory.getBeneficioInput();

        Mockito.when(beneficioService.salvar(beneficioMock)).thenReturn(beneficioSalvoMock);
        HttpEntity<BeneficioInput> request = new HttpEntity<>(beneficioInputMock);
        ResponseEntity<BeneficioModel> beneficio = restTemplate.postForEntity("/beneficio", request, BeneficioModel.class);

        assertThat(beneficio.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(Objects.requireNonNull(beneficio.getBody()).getId()).isEqualTo(1L);
        Mockito.verify(beneficioService, Mockito.times(1)).salvar(beneficioMock);
    }
}
