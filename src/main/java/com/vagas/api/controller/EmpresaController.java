package com.vagas.api.controller;

import javax.validation.Valid;

import com.vagas.domain.model.Empresa;
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

import com.vagas.api.model.EmpresaModel;
import com.vagas.api.model.EmpresaResumoModel;
import com.vagas.api.model.input.EmpresaInput;
import com.vagas.api.modelmapper.EmpresaModelMapper;
import com.vagas.domain.service.EmpresaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/empresa")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmpresaController {

    private final EmpresaService empresaService;
    private final EmpresaModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<Page<EmpresaResumoModel>> findAll(Pageable pageable) {
        Page<Empresa> empresas = empresaService.listarTodos(pageable);
        return new ResponseEntity<>(
                new PageImpl<>(modelMapper.toCollectionResumeModel(empresas.getContent()),
                        pageable, empresas.getTotalElements())
                , HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<EmpresaModel> salvar(@RequestBody @Valid EmpresaInput empresaInput) {
        Empresa empresaSalva = empresaService.salvar(modelMapper.toDomainObject(empresaInput));
        return new ResponseEntity<>(modelMapper.toModel(empresaSalva), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaModel> findById(@PathVariable("id") Long id) {
        Empresa empresa = empresaService.buscarOuFalhar(id);
        return new ResponseEntity<>(modelMapper.toModel(empresa), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpresaModel> atualizar(@PathVariable("id") Long id,
                                                  @RequestBody @Valid EmpresaInput empresaInput) {
        Empresa empresa = empresaService.buscarOuFalhar(id);
        modelMapper.copyToDomainObject(empresaInput, empresa);
        Empresa empresaAtualizada = empresaService.update(empresaInput, empresa);
        return new ResponseEntity<>(modelMapper.toModel(empresaAtualizada), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        empresaService.excluir(id);
        return ResponseEntity.noContent().build();
    }

}
