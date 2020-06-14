package com.vagas.api.modelmapper;

import com.vagas.api.model.OportunidadeModel;
import com.vagas.api.model.input.OportunidadeInput;
import com.vagas.domain.model.Oportunidade;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OportunidadeModelMapper {

    private final ModelMapper modelMapper;

    public OportunidadeModel toModel(Oportunidade oportunidade) {
        return modelMapper.map(oportunidade, OportunidadeModel.class);
    }

    public Oportunidade toDomainObject(OportunidadeInput oportunidadeInput) {
        return modelMapper.map(oportunidadeInput, Oportunidade.class);
    }

    public void copyToDomainObject(OportunidadeInput oportunidadeInput, Oportunidade oportunidade) {
        modelMapper.map(oportunidadeInput, oportunidade);
    }

    public List<OportunidadeModel> toCollectionModel(List<Oportunidade> oportunidades) {
        return oportunidades.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

}
