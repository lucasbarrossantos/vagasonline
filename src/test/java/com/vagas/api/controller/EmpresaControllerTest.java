package com.vagas.api.controller;

import com.google.gson.Gson;
import com.vagas.api.mock.MockFactory;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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

}
