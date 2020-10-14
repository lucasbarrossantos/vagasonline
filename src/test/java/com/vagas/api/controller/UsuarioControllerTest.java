package com.vagas.api.controller;

import com.google.gson.Gson;
import com.vagas.api.mock.MockFactory;
import com.vagas.api.model.BeneficioModel;
import com.vagas.api.model.UsuarioModel;
import com.vagas.api.model.input.BeneficioInput;
import com.vagas.api.model.input.UsuarioInput;
import com.vagas.domain.model.Beneficio;
import com.vagas.domain.model.Usuario;
import com.vagas.domain.service.UsuarioService;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Testes do controller de BeneficioController")
public class UsuarioControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MockFactory mockFactory;

    @Autowired
    private Gson gson;

    @MockBean
    private UsuarioService usuarioService;

    @Test
    @DisplayName("Deve salvar um usuário e retornar 201!")
    void deveSalvarUmUsuarioERetornar201() {
        Usuario usuarioSalvoMock = mockFactory.getUsuario();
        usuarioSalvoMock.setId(1L);

        UsuarioInput usuarioInput = mockFactory.getUsuarioInput();

        Mockito.when(usuarioService.salvar(Mockito.any(Usuario.class))).thenReturn(usuarioSalvoMock);
        HttpEntity<UsuarioInput> request = new HttpEntity<>(usuarioInput);
        ResponseEntity<String> response = restTemplate.postForEntity("/usuario", request, String.class);
        Usuario usuarioResponse = gson.fromJson(response.getBody(), Usuario.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(usuarioService, Mockito.times(1)).salvar(Mockito.any(Usuario.class));
        assertEquals((long) usuarioResponse.getId(), 1L);
    }

    @Test
    @DisplayName("Deve listar os usuários e retornar 200!")
    void deveListarOsUsuariosERetornar200() {
        Page<Usuario> pageMock = new PageImpl<>(Collections.singletonList(mockFactory.getUsuario()));
        Mockito.when(usuarioService.listarTodos(Mockito.any(Pageable.class))).thenReturn(pageMock);
        ResponseEntity<String> response = restTemplate.getForEntity("/usuario", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Deve recuperar um usuário por id e retornar 200!")
    void deveBuscarUmUsuarioPorIdERetornar200() {
        Usuario usuarioMock = mockFactory.getUsuario();
        usuarioMock.setId(1L);

        Mockito.when(usuarioService.buscarOuFalhar(1L)).thenReturn(usuarioMock);
        ResponseEntity<String> response = restTemplate.getForEntity("/usuario/1", String.class);
        UsuarioModel usuarioModel = gson.fromJson(response.getBody(), UsuarioModel.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals((long) usuarioModel.getId(), 1L);
        verify(usuarioService, Mockito.times(1)).buscarOuFalhar(1L);
    }

    @Test
    @DisplayName("Deve excluir um usuário e retornar 204!")
    void deveExcluirUmUsuarioERetornar204() {
        Usuario usuarioMock = mockFactory.getUsuario();
        usuarioMock.setId(1L);

        Mockito.doNothing().when(usuarioService).excluir(usuarioMock.getId());

        HttpEntity<String> entity = new HttpEntity<>(null, new HttpHeaders());
        ResponseEntity<String> response = restTemplate.exchange("/usuario/" + usuarioMock.getId(),
                HttpMethod.DELETE, entity, String.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(usuarioService, Mockito.times(1)).excluir(usuarioMock.getId());
    }

    @Test
    @DisplayName("Deve atualizar um usuário e retornar 200!")
    void deveAtualizarUmUsuarioERetornar200() {
        Usuario usuarioMock = mockFactory.getUsuario();

        Usuario usuarioSalvoMock = mockFactory.getUsuario();
        usuarioSalvoMock.setId(1L);

        UsuarioInput usuarioInput = mockFactory.getUsuarioInput();
        HttpEntity<UsuarioInput> request = new HttpEntity<>(usuarioInput);

        Mockito.when(usuarioService.update(Mockito.any(Usuario.class))).thenReturn(usuarioSalvoMock);
        Mockito.when(usuarioService.buscarOuFalhar(1L)).thenReturn(usuarioSalvoMock);


        ResponseEntity<UsuarioModel> response = restTemplate
                .exchange("/usuario/1", HttpMethod.PUT, request, UsuarioModel.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getId()).isEqualTo(1L);
        verify(usuarioService, Mockito.times(1)).update(Mockito.any(Usuario.class));
    }

}
