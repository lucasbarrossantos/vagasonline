package com.vagas.api.modelmapper;

import com.vagas.api.model.EmpresaModel;
import com.vagas.api.model.EmpresaResumoModel;
import com.vagas.api.model.input.EmpresaInput;
import com.vagas.domain.model.Empresa;
import com.vagas.domain.model.Endereco;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmpresaModelMapper {

    private final ModelMapper modelMapper;

    public EmpresaModel toModel(Empresa empresa) {
        return modelMapper.map(empresa, EmpresaModel.class);
    }

    public EmpresaResumoModel toResumeModel(Empresa empresa) {
        return modelMapper.map(empresa, EmpresaResumoModel.class);
    }

    public Empresa toDomainObject(EmpresaInput empresaInput) {
        return modelMapper.map(empresaInput, Empresa.class);
    }

    public void copyToDomainObject(EmpresaInput empresaInput, Empresa empresa) {
        empresa.setEndereco(new Endereco());
        modelMapper.map(empresaInput, empresa);
    }

    public List<EmpresaModel> toCollectionModel(List<Empresa> empresas) {
        return empresas.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    public List<EmpresaResumoModel> toCollectionResumeModel(List<Empresa> empresas) {
        return empresas.stream()
                .map(this::toResumeModel)
                .collect(Collectors.toList());
    }

}
