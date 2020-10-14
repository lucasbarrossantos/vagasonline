package com.vagas.api.mock;

import com.vagas.api.model.BeneficioModel;
import com.vagas.api.model.input.*;
import com.vagas.domain.model.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;

@Component
public class MockFactory {

    public Beneficio getBeneficio() {
        return new Beneficio(1L, "Titulo beneficio", "Descricao beneficio");
    }

    public BeneficioInput getBeneficioInput() {
        return BeneficioInput.builder()
                .descricao("Teste de descricao")
                .titulo("Teste titulo")
                .build();
    }

    public BeneficioModel getBeneficioModel() {
        BeneficioModel beneficioModel = new BeneficioModel();
        beneficioModel.setId(1L);
        beneficioModel.setTitulo("Titulo teste");
        beneficioModel.setDescricao("Descricao teste");
        return beneficioModel;
    }

    public Empresa getEmpresa() {
        Empresa empresa = new Empresa();
        empresa.setNome("Empresa teste");
        empresa.setCnpj("77784090000183");
        empresa.setEmail("empresa@teste.com");
        empresa.setId(1L);
        empresa.setLatitude("-1");
        empresa.setLongitude("-2");
        empresa.setSite("www.teste.com.br");
        empresa.setTelefone("(87)9 9999-9999");
        empresa.setEndereco(getEndereco());
        return empresa;
    }

    public Oportunidade getOportunidade() {
        Oportunidade oportunidade = new Oportunidade();
        oportunidade.setDescricao("Teste de oportunidade");
        oportunidade.setTitulo("Teste Oprt");
        oportunidade.setDataCriacao(LocalDate.now());
        oportunidade.setLocalTrabalho("Teste de local de trabalho");
        oportunidade.setId(1L);
        oportunidade.setEmpresa(getEmpresa());
        oportunidade.setDataEncerramento(LocalDate.now());
        oportunidade.setBeneficios(Collections.singleton(getBeneficio()));
        return oportunidade;
    }

    public Endereco getEndereco() {
        Endereco endereco = new Endereco();
        endereco.setId(1L);
        endereco.setUf("PE");
        endereco.setBairro("Bairro teste");
        endereco.setCep("51110110");
        endereco.setCidade("Cidade teste");
        endereco.setLogradouro("Logradouro teste");
        endereco.setComplemento("Complemento teste");
        endereco.setNumero("20");
        return endereco;
    }

    public EmpresaInput getEmpresaInput() {
        EmpresaInput empresa = new EmpresaInput();
        empresa.setNome("Empresa teste");
        empresa.setCnpj("77784090000183");
        empresa.setEmail("empresa@teste.com");
        empresa.setLatitude("-1");
        empresa.setLongitude("-2");
        empresa.setSite("www.teste.com.br");
        empresa.setTelefone("(87)9 9999-9999");
        empresa.setEndereco(getEnderecoInput());
        return empresa;
    }

    public EnderecoInput getEnderecoInput() {
        EnderecoInput endereco = new EnderecoInput();
        endereco.setUf("PE");
        endereco.setBairro("Bairro teste");
        endereco.setCep("51110110");
        endereco.setCidade("Cidade teste");
        endereco.setLogradouro("Logradouro teste");
        endereco.setComplemento("Complemento teste");
        endereco.setNumero("20");
        return endereco;
    }

    public OportunidadeInput getOportunidadeInput() {
        OportunidadeInput oportunidade = new OportunidadeInput();
        oportunidade.setDescricao("Teste de oportunidade");
        oportunidade.setTitulo("Teste Oprt");
        oportunidade.setDataCriacao(LocalDate.now());
        oportunidade.setLocalTrabalho("Teste de local de trabalho");
        oportunidade.setEmpresa(getEmpresaId());
        oportunidade.setDataEncerramento(LocalDate.now());
        return oportunidade;
    }

    public EmpresaId getEmpresaId() {
        EmpresaId empresa = new EmpresaId();
        empresa.setId(1L);
        return empresa;
    }

    public Usuario getUsuario() {
        Usuario usuario = new Usuario();
        usuario.setEmail("lucas.barros.santos@email.com");
        usuario.setIsAtivo(true);
        usuario.setLogin("lucas.barros");
        return usuario;
    }

    public UsuarioInput getUsuarioInput() {
        UsuarioInput usuario = new UsuarioInput();
        usuario.setEmail("lucas.barros.santos@email.com");
        usuario.setIsAtivo(true);
        usuario.setLogin("lucas.barros");
        usuario.setSenha("123");
        usuario.setConfirmarSenha("123");
        return usuario;
    }
}
