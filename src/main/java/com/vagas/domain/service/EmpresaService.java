package com.vagas.domain.service;

import com.vagas.api.model.EmpresaModel;
import com.vagas.api.model.EmpresaResumoModel;
import com.vagas.api.model.input.EmpresaInput;
import com.vagas.api.modelmapper.EmpresaModelMapper;
import com.vagas.domain.exception.EmpresaNaoEncontradaException;
import com.vagas.domain.exception.EnderecoNaoEncontradoException;
import com.vagas.domain.exception.EntidadeEmUsoException;
import com.vagas.domain.model.Empresa;
import com.vagas.domain.model.Endereco;
import com.vagas.domain.repository.EmpresaRepository;
import com.vagas.domain.repository.EnderecoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmpresaService {
    private final Logger log = LoggerFactory.getLogger(EmpresaService.class);

    private static final String MSG_EMPRESA_EM_USO
            = "Empresa de código %d não pode ser removida, pois está em uso";

    private final EmpresaRepository empresaRepository;
    private final EnderecoRepository enderecoRepository;
    private final EmpresaModelMapper modelMapper;

    @Transactional
    public Mono<EmpresaModel> salvar(final Empresa empresa) {
        return Mono
                .fromSupplier(() -> {
                    Endereco endereco = empresa.getEndereco();
                    empresa.setEndereco(null);
                    Empresa empresaSalvaSemEndereco = empresaRepository.save(empresa);
                    vincularEnderecoAEmpresa(endereco, empresaSalvaSemEndereco);
                    return empresaRepository.save(empresaSalvaSemEndereco);
                })
                .map(modelMapper::toModel)
                .doOnError(error -> log.error("Erro em EmpresaService.salvar() ao tentar salvar o empresa", error));
    }

    public Mono<Page<EmpresaResumoModel>> listarTodos(Pageable pageable) {
        return Mono.fromSupplier(() -> empresaRepository.findAll(pageable))
                .map(empresaPage -> {
                    List<EmpresaResumoModel> empresaResumo = modelMapper
                            .toCollectionResumeModel(empresaPage.getContent());
                    return new PageImpl<>(empresaResumo, pageable, empresaPage.getTotalElements());
                });
    }

    public Mono<Empresa> buscarOuFalhar(Long id) {
        return Mono.fromCallable(() -> empresaRepository.findById(id)
                .orElseThrow(() -> {
                            log.error(String
                                    .format("Erro em EmpresaController.buscarOuFalhar(?) ao tentar buscar o empresa de código %d", id));
                            return new EmpresaNaoEncontradaException(id);
                        }
                ));
    }

    public Mono<EmpresaModel> update(EmpresaInput empresaInput, Empresa empresa, Long enderecoId) {
        modelMapper.copyToDomainObject(empresaInput, empresa);
        empresa.getEndereco().setId(enderecoId);
        return salvar(empresa);
    }

    @Transactional
    public Mono<Tuple2<Empresa, Boolean>> excluir(Long id) {
        return buscarOuFalhar(id).zipWhen(empresa -> {
            try {
                Endereco endereco = empresaRepository
                        .enderecoByEmpresaId(id)
                        .orElseThrow(() -> new EnderecoNaoEncontradoException("Endereço não encontrado!"));

                empresaRepository.deleteById(id);
                empresaRepository.flush();

                if (endereco != null) {
                    enderecoRepository.deleteById(endereco.getId());
                    enderecoRepository.flush();
                }

            } catch (EmptyResultDataAccessException e) {
                log.error(String.format("Erro ao tentar excluir a empresa! EmpresaService.excluir(?), nenhum empresa encontrado com o id %s", id));
                throw new EmpresaNaoEncontradaException(id);

            } catch (DataIntegrityViolationException e) {
                log.error("Erro ao tentar excluir a empresa! EmpresaService.excluir(?), " +
                        "violação de chave primária, id não encontrado no Banco de Dados");
                throw new EntidadeEmUsoException(
                        String.format(MSG_EMPRESA_EM_USO, id));
            }

            return Mono.just(Boolean.TRUE);
        });
    }

    private void vincularEnderecoAEmpresa(Endereco endereco, Empresa empresaSalvaSemEndereco) {
        Long enderecoId = endereco.getId();

        if (enderecoId != null) {
            Endereco enderecoVinculado = enderecoRepository.findById(enderecoId)
                    .orElseThrow(() -> new EnderecoNaoEncontradoException(enderecoId));
            BeanUtils.copyProperties(endereco, enderecoVinculado);
            endereco = enderecoVinculado;
        }

        endereco.setEmpresa(empresaSalvaSemEndereco);
        endereco = enderecoRepository.save(endereco);
        empresaSalvaSemEndereco.setEndereco(endereco);
    }
}
