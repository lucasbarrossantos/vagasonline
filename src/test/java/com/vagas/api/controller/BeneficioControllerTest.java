package com.vagas.api.controller;

import com.google.gson.Gson;
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
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

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

    @Autowired
    private Gson gson;

    @Test
    @DisplayName("Deve salvar um beneficio e retornar 201!")
    void deveSalvarUmBeneficioERetornar201() {
        Beneficio beneficioSalvoMock = mockFactory.getBeneficio();
        beneficioSalvoMock.setId(1L);

        BeneficioInput beneficioInputMock = mockFactory.getBeneficioInput();

        Mockito.when(beneficioService.salvar(Mockito.any(Beneficio.class))).thenReturn(beneficioSalvoMock);
        HttpEntity<BeneficioInput> request = new HttpEntity<>(beneficioInputMock);
        ResponseEntity<String> beneficio = restTemplate.postForEntity("/beneficio", request, String.class);
        Beneficio beneficioResponse = gson.fromJson(beneficio.getBody(), Beneficio.class);

        assertThat(beneficio.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(beneficioService, Mockito.times(1)).salvar(Mockito.any(Beneficio.class));
        assertEquals((long) beneficioResponse.getId(), 1L);
    }

    @Test
    @DisplayName("Deve excluir um beneficio e retornar 204!")
    void deveExcluirUmBeneficioERetornar204() {
        Beneficio beneficioMock = mockFactory.getBeneficio();
        beneficioMock.setId(1L);

        Mockito.doNothing().when(beneficioService).excluir(beneficioMock.getId());

        HttpEntity<String> entity = new HttpEntity<>(null, new HttpHeaders());
        ResponseEntity<String> response = restTemplate.exchange("/beneficio/" + beneficioMock.getId(),
                HttpMethod.DELETE, entity, String.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(beneficioService, Mockito.times(1)).excluir(beneficioMock.getId());
    }

    @Test
    @DisplayName("Deve atualizar um beneficio e retornar 200!")
    void deveAtualizarUmBeneficioERetornar200() {
        Beneficio beneficioMock = mockFactory.getBeneficio();

        Beneficio beneficioSalvoMock = mockFactory.getBeneficio();
        beneficioSalvoMock.setDescricao("Beneficio atualizado");
        beneficioSalvoMock.setId(1L);

        BeneficioModel beneficioModel = new BeneficioModel();
        beneficioModel.setId(1L);
        beneficioModel.setTitulo("Teste");
        beneficioModel.setDescricao("Teste");

        BeneficioInput beneficioInputMock = mockFactory.getBeneficioInput();
        HttpEntity<BeneficioInput> request = new HttpEntity<>(beneficioInputMock);

        Mockito.when(beneficioService.update(beneficioMock)).thenReturn(beneficioSalvoMock);
        Mockito.when(beneficioService.buscarOuFalhar(1L)).thenReturn(Optional.of(beneficioSalvoMock));


        ResponseEntity<BeneficioModel> beneficio = restTemplate
                .exchange("/beneficio/1", HttpMethod.PUT, request, BeneficioModel.class);

        assertThat(beneficio.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(beneficio.getBody()).getId()).isEqualTo(1L);
        verify(beneficioService, Mockito.times(1)).update(beneficioMock);
    }

    @Test
    @DisplayName("Deve retornar 400 ao tentar salvar um beneficio sem titulo!")
    void deveRetornarErroAoTentarSalvarUmBeneficioSemTitulo() {
        Beneficio beneficioMock = mockFactory.getBeneficio();
        Beneficio beneficioSalvoMock = mockFactory.getBeneficio();
        beneficioSalvoMock.setId(1L);

        BeneficioInput beneficioInputMock = mockFactory.getBeneficioInput();
        beneficioInputMock.setTitulo(null);

        Mockito.when(beneficioService.salvar(beneficioMock)).thenReturn(beneficioSalvoMock);
        HttpEntity<BeneficioInput> request = new HttpEntity<>(beneficioInputMock);
        ResponseEntity<BeneficioModel> beneficio = restTemplate.postForEntity("/beneficio", request, BeneficioModel.class);

        assertThat(beneficio.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
