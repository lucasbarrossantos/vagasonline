package com.vagas.api.mock;

import com.vagas.api.model.BeneficioModel;
import com.vagas.api.model.input.BeneficioInput;
import com.vagas.domain.model.Beneficio;
import org.springframework.stereotype.Component;

@Component
public class MockFactory {

    public Beneficio getBeneficio() {
        return Beneficio.builder()
                .descricao("Teste de descricao")
                .titulo("Teste titulo")
                .build();
    }

    public BeneficioInput getBeneficioInput() {
        return BeneficioInput.builder()
                .descricao("Teste de descricao")
                .titulo("Teste titulo")
                .build();
    }

    public BeneficioModel getBeneficioModel(){
        BeneficioModel beneficioModel = new BeneficioModel();
        beneficioModel.setId(1L);
        beneficioModel.setTitulo("Titulo teste");
        beneficioModel.setDescricao("Descricao teste");
        return beneficioModel;
    }

}
