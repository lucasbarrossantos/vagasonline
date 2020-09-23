package com.vagas.api.controller;

import javax.validation.Valid;

import com.vagas.domain.model.Oportunidade;
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

import com.vagas.api.model.OportunidadeModel;
import com.vagas.api.model.input.OportunidadeInput;
import com.vagas.api.modelmapper.OportunidadeModelMapper;
import com.vagas.domain.service.OportunidadeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/empresa/{empresaId}/oportunidades")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmpresaOportunidadeController {

    private final OportunidadeService oportunidadeService;
    private final OportunidadeModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<Page<OportunidadeModel>> findAll(@PathVariable Long empresaId, Pageable pageable) {
        Page<Oportunidade> oportunidadePage = oportunidadeService.listarTodos(empresaId, pageable);
        List<OportunidadeModel> empresaResumo =
                modelMapper.toCollectionModel(oportunidadePage.getContent());

        return new ResponseEntity<>(new PageImpl<>(empresaResumo, pageable,
                oportunidadePage.getTotalElements()), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<OportunidadeModel> salvar(@RequestBody @Valid OportunidadeInput oportunidadeInput,
                                                    @PathVariable Long empresaId) {
        Oportunidade oportunidadeSalva = oportunidadeService.salvar(modelMapper.toDomainObject(oportunidadeInput), empresaId);
        return new ResponseEntity<>(modelMapper.toModel(oportunidadeSalva), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OportunidadeModel> findById(@PathVariable("id") Long id) {
        Oportunidade oportunidade = oportunidadeService.buscarOuFalhar(id);
        return new ResponseEntity<>(modelMapper.toModel(oportunidade), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OportunidadeModel> atualizar(@PathVariable("id") Long id, @PathVariable Long empresaId,
                                                       @RequestBody @Valid OportunidadeInput oportunidadeInput) {
        Oportunidade oportunidade = oportunidadeService.buscarOuFalhar(id);
        modelMapper.copyToDomainObject(oportunidadeInput, oportunidade);
        Oportunidade oportunidadeAtualizada = oportunidadeService.update(oportunidade, empresaId);
        return new ResponseEntity<>(modelMapper.toModel(oportunidadeAtualizada), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> remover(@PathVariable Long id, @PathVariable Long empresaId) {
        oportunidadeService.excluir(empresaId, id);
        return ResponseEntity.noContent().build();
    }

}
