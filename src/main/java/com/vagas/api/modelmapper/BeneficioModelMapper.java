package com.vagas.api.modelmapper;

import com.vagas.api.model.BeneficioModel;
import com.vagas.api.model.input.BeneficioInput;
import com.vagas.domain.model.Beneficio;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BeneficioModelMapper {

    private final ModelMapper modelMapper;

    public BeneficioModel toModel(Beneficio beneficio) {
        return modelMapper.map(beneficio, BeneficioModel.class);
    }

    public Beneficio toDomainObject(BeneficioInput beneficioInput) {
        return modelMapper.map(beneficioInput, Beneficio.class);
    }

    public void copyToDomainObject(BeneficioInput beneficioInput, Beneficio beneficio) {
        modelMapper.map(beneficioInput, beneficio);
    }

    public List<BeneficioModel> toCollectionModel(List<Beneficio> beneficios) {
        return beneficios.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

}
