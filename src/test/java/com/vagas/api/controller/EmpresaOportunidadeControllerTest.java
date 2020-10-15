package com.vagas.api.controller;

import com.google.gson.Gson;
import com.vagas.api.mock.MockFactory;
import com.vagas.api.model.OportunidadeModel;
import com.vagas.api.model.input.OportunidadeInput;
import com.vagas.domain.model.Oportunidade;
import com.vagas.domain.service.OportunidadeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Testes do controller de EmpresaOportunidadeController")
public class EmpresaOportunidadeControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MockFactory mockFactory;

    @Autowired
    private Gson gson;

    @MockBean
    private OportunidadeService oportunidadeService;

    private final String urlBase = "/empresa/1/oportunidades";

    @Test
    @DisplayName("Deve salvar uma Oportunidade e retornar 201!")
    void deveSalvarUmaOportunidadeERetornar201() {
        Oportunidade oportunidadeSalvaMock = mockFactory.getOportunidade();
        oportunidadeSalvaMock.setId(1L);

        OportunidadeInput oportunidadeInput = mockFactory.getOportunidadeInput();

        Mockito.when(oportunidadeService.salvar(Mockito.any(Oportunidade.class), Mockito.anyLong())).thenReturn(oportunidadeSalvaMock);
        HttpEntity<OportunidadeInput> request = new HttpEntity<>(oportunidadeInput);
        ResponseEntity<String> response = restTemplate.postForEntity(urlBase, request, String.class);
        OportunidadeModel oportunidadeResponse = gson.fromJson(response.getBody(), OportunidadeModel.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(oportunidadeService, Mockito.times(1)).salvar(Mockito.any(Oportunidade.class), Mockito.anyLong());
        assertEquals((long) oportunidadeResponse.getId(), 1L);
    }

    @Test
    @DisplayName("Deve listar as Oportunidades e retornar 200!")
    void deveListarAsOportunidadesERetornar200() {
        Page<Oportunidade> pageMock = new PageImpl<>(Collections.singletonList(mockFactory.getOportunidade()));
        Mockito.when(oportunidadeService.listarTodos(Mockito.anyLong(), Mockito.any(Pageable.class))).thenReturn(pageMock);
        ResponseEntity<String> response = restTemplate.getForEntity(urlBase, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Deve recuperar uma Oportunidade por id e retornar 200!")
    void deveBuscarUmaOportunidadePorIdERetornar200Ok() {
        Oportunidade oportunidadeMock = mockFactory.getOportunidade();
        oportunidadeMock.setId(1L);

        Mockito.when(oportunidadeService.buscarOuFalhar(1L)).thenReturn(oportunidadeMock);
        ResponseEntity<String> response = restTemplate.getForEntity(urlBase + "/1", String.class);
        OportunidadeModel oportunidadeModel = gson.fromJson(response.getBody(), OportunidadeModel.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(oportunidadeModel.getId(), oportunidadeMock.getId());
        verify(oportunidadeService, Mockito.times(1)).buscarOuFalhar(1L);
    }

    @Test
    @DisplayName("Deve excluir uma Oportunidade e retornar 204!")
    void deveExcluirUmaOportunidadeERetornar204() {
        Oportunidade oportunidadeMock = mockFactory.getOportunidade();
        oportunidadeMock.setId(1L);

        Mockito.doNothing().when(oportunidadeService).excluir(1L, oportunidadeMock.getId());

        HttpEntity<String> entity = new HttpEntity<>(null, new HttpHeaders());
        ResponseEntity<String> response = restTemplate.exchange(urlBase + "/" + oportunidadeMock.getId(),
                HttpMethod.DELETE, entity, String.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(oportunidadeService, Mockito.times(1)).excluir(1L, oportunidadeMock.getId());
    }

}
