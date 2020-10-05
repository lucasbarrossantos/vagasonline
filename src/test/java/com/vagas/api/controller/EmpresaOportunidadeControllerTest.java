package com.vagas.api.controller;

import com.google.gson.Gson;
import com.vagas.api.mock.MockFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Testes do controller de BeneficioController")
public class EmpresaOportunidadeControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MockFactory mockFactory;

    @Autowired
    private Gson gson;

}
