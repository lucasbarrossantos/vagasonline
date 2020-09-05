package com.vagas;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import com.vagas.api.model.input.BeneficioInput;
import com.vagas.domain.model.Beneficio;
import com.vagas.domain.service.BeneficioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import reactor.core.publisher.Mono;

class BeneficioTest extends APIBaseTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BeneficioService beneficioService;

	@Autowired
	private Gson gson;

	@Test
	public void test_Criar_Novo_Beneficio() throws Exception {
		Beneficio beneficioEsperado = new Beneficio();
		beneficioEsperado.setId(1L);
		beneficioEsperado.setTitulo("VR");
		beneficioEsperado.setDescricao("Vale Refeição");

		when(beneficioService.salvar(any(Beneficio.class))).thenReturn(Mono.just(beneficioEsperado));

		mockMvc.perform(MockMvcRequestBuilders.post("/beneficio")
				.contentType(MediaType.APPLICATION_JSON)
				.content(gson.toJson(new BeneficioInput("VR", "Vale Refeição"))))
			.andExpect(status().isCreated());
	}

}
