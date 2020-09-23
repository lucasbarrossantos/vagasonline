package com.vagas.api.controller;

import javax.validation.Valid;

import com.vagas.domain.model.Beneficio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vagas.api.model.BeneficioModel;
import com.vagas.api.model.input.BeneficioInput;
import com.vagas.api.modelmapper.BeneficioModelMapper;
import com.vagas.domain.service.BeneficioService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/beneficio")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BeneficioController {

    private final BeneficioService beneficioService;
    private final BeneficioModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<Page<BeneficioModel>> findAll(Pageable pageable) {
        Page<Beneficio> response = beneficioService.listarTodos(pageable);

        return new ResponseEntity<>(
                new PageImpl<>(modelMapper.toCollectionModel(response.getContent()),
                        pageable, response.getTotalElements())
                , HttpStatus.OK);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BeneficioModel> salvar(@RequestBody @Valid BeneficioInput beneficioInput) {
        Beneficio beneficioSalvo = this.beneficioService.salvar(modelMapper.toDomainObject(beneficioInput));
        return new ResponseEntity<>(modelMapper.toModel(beneficioSalvo), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeneficioModel> findById(@PathVariable("id") Long id) {
        Optional<Beneficio> beneficio = beneficioService.buscarOuFalhar(id);
        return beneficio.map(value -> new ResponseEntity<>(modelMapper.toModel(value), HttpStatus.OK))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BeneficioModel> atualizar(@PathVariable("id") Long id,
                                                    @RequestBody @Valid BeneficioInput beneficioInput) {
        Optional<Beneficio> beneficio = beneficioService.buscarOuFalhar(id);

        return beneficio.map(value -> {
            modelMapper.copyToDomainObject(beneficioInput, value);
            Beneficio beneficioSalvo = beneficioService.update(value);
            return new ResponseEntity<>(modelMapper.toModel(beneficioSalvo),
                    HttpStatus.OK);
        }).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        beneficioService.excluir(id);
        return ResponseEntity.noContent().build();
    }

}
