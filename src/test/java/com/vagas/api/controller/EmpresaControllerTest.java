package com.vagas.api.controller;

import com.google.gson.Gson;
import com.vagas.api.mock.MockFactory;
import com.vagas.api.model.BeneficioModel;
import com.vagas.api.model.EmpresaModel;
import com.vagas.api.model.input.EmpresaInput;
import com.vagas.domain.model.Empresa;
import com.vagas.domain.service.EmpresaService;
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
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Testes do controller de EmpresaController")
public class EmpresaControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MockFactory mockFactory;

    @Autowired
    private Gson gson;

    @MockBean
    private EmpresaService empresaService;

    @Test
    @DisplayName("Deve salvar uma empresa e retornar 201!")
    void deveSalvarUmaEmpresaERetornar201() {
        Empresa empresaMock = mockFactory.getEmpresa();
        EmpresaInput empresaInput = mockFactory.getEmpresaInput();

        Mockito.when(empresaService.salvar(Mockito.any(Empresa.class))).thenReturn(empresaMock);

        HttpEntity<EmpresaInput> request = new HttpEntity<>(empresaInput);
        ResponseEntity<String> response = restTemplate.postForEntity("/empresa", request, String.class);
        Empresa empresa = gson.fromJson(response.getBody(), Empresa.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(empresaService, Mockito.times(1)).salvar(Mockito.any(Empresa.class));
        assertEquals((long) empresa.getId(), 1L);
    }

    @Test
    @DisplayName("Deve listar as Empresas e retornar 200!")
    void deveListarAsEmpresasERetornar200() {
        Page<Empresa> pageMock = new PageImpl<>(Collections.singletonList(mockFactory.getEmpresa()));
        Mockito.when(empresaService.listarTodos(Mockito.any(Pageable.class))).thenReturn(pageMock);
        ResponseEntity<String> response = restTemplate.getForEntity("/empresa", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Deve recuperar uma Empresa por id e retornar 200!")
    void deveBuscarUmaEmpresaPorIdERetornar200() {
        Empresa empresaMock = mockFactory.getEmpresa();
        empresaMock.setId(1L);

        Mockito.when(empresaService.buscarOuFalhar(1L)).thenReturn(empresaMock);
        ResponseEntity<String> response = restTemplate.getForEntity("/empresa/1", String.class);
        EmpresaModel beneficioModel = gson.fromJson(response.getBody(), EmpresaModel.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals((long) beneficioModel.getId(), 1L);
        verify(empresaService, Mockito.times(1)).buscarOuFalhar(1L);
    }

    @Test
    @DisplayName("Deve excluir uma Empresa e retornar 204!")
    void deveExcluirUmaEmpresaERetornar204() {
        Empresa empresaMock = mockFactory.getEmpresa();
        empresaMock.setId(1L);

        Mockito.doNothing().when(empresaService).excluir(empresaMock.getId());

        HttpEntity<String> entity = new HttpEntity<>(null, new HttpHeaders());
        ResponseEntity<String> response = restTemplate.exchange("/empresa/" + empresaMock.getId(),
                HttpMethod.DELETE, entity, String.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(empresaService, Mockito.times(1)).excluir(empresaMock.getId());
    }

    @Test
    @DisplayName("Deve atualizar uma Empresa e retornar 200!")
    void deveAtualizarUmaEmpresaERetornar200() {
        Empresa empresaMock = mockFactory.getEmpresa();

        Empresa empresaSalvoMock = mockFactory.getEmpresa();
        empresaSalvoMock.setId(1L);

        EmpresaModel empresaModel = new EmpresaModel();
        empresaModel.setId(1L);
        empresaMock.setSite("www.site.com.br");

        EmpresaInput beneficioInputMock = mockFactory.getEmpresaInput();
        HttpEntity<EmpresaInput> request = new HttpEntity<>(beneficioInputMock);

        Mockito.when(empresaService.update(empresaMock)).thenReturn(empresaSalvoMock);
        Mockito.when(empresaService.buscarOuFalhar(1L)).thenReturn(empresaSalvoMock);


        ResponseEntity<BeneficioModel> response = restTemplate
                .exchange("/empresa/1", HttpMethod.PUT, request, BeneficioModel.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getId()).isEqualTo(1L);
        verify(empresaService, Mockito.times(1)).update(empresaMock);
    }

}
