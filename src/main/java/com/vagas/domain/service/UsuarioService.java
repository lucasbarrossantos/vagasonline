package com.vagas.domain.service;

import com.vagas.api.model.UsuarioModel;
import com.vagas.api.model.input.UsuarioInput;
import com.vagas.api.modelmapper.UsuarioModelMapper;
import com.vagas.domain.exception.NegocioException;
import com.vagas.domain.exception.UsuarioNaoEncontradoException;
import com.vagas.domain.exception.EntidadeEmUsoException;
import com.vagas.domain.model.Usuario;
import com.vagas.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QuerydslUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UsuarioService {
    private final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    private static final String MSG_BENEFICIO_EM_USO
            = "Usuário de código %d não pode ser removido, pois está em uso";

    private final UsuarioRepository usuarioRepository;
    private final UsuarioModelMapper modelMapper;

    @Transactional
    public Mono<Usuario> salvar(final Usuario usuario) {
        return Mono
                .fromCallable(() -> {
                    if (usuario.getIsAtivo() == null)
                        usuario.setIsAtivo(true);

                    Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(usuario.getEmail());

                    if (usuarioExistente.isPresent() && !usuarioExistente.get().equals(usuario)) {
                        throw new NegocioException(
                                String.format("Já existe um usuário cadastrado com o e-mail %s",
                                        usuario.getEmail()));
                    }

                    if (!usuarioExistente.isPresent() && !usuario.getSenha()
                            .equals(usuario.getConfirmarSenha())) {
                        throw new NegocioException("As senhas informadas não coincidem!");
                    }

                    return usuarioRepository.save(usuario);
                })
                .doOnError(error -> log.error("Erro em UsuarioService.salvar() ao tentar salvar o usuário", error));
    }

    public Mono<Page<UsuarioModel>> listarTodos(Pageable pageable) {
        return Mono.fromSupplier(usuarioRepository.findAll(pageable))
                .map(usuarioStream -> modelMapper.toCollectionModel(usuarioStream.collect(Collectors.toList())))
                .map(usuarioModels -> new PageImpl<>(usuarioModels, pageable, usuarioModels.size()));
    }

    public Mono<Usuario> buscarOuFalhar(Long id) {
        return Mono.fromCallable(() -> usuarioRepository.findById(id)
                .orElseThrow(() -> {
                            log.error(String
                                    .format("Erro em UsuarioController.buscarOuFalhar(?) ao tentar buscar o usuário de código %d", id));
                            return new UsuarioNaoEncontradoException(id);
                        }
                ));
    }

    public Mono<Usuario> update(UsuarioInput usuarioInput, Usuario usuario) {
        modelMapper.copyToDomainObject(usuarioInput, usuario);
        return salvar(usuario);
    }

    @Transactional
    public Mono<Void> excluir(Long id) {
        return Mono.fromRunnable(() -> {
            try {
                usuarioRepository.deleteById(id);
                usuarioRepository.flush();
            } catch (EmptyResultDataAccessException e) {
                log.error(String.format("Erro ao tentar excluir o usuário! UsuarioService.excluir(?), " +
                        "nenhum benefício encontrado com o id %s", id));
                throw new UsuarioNaoEncontradoException(id);

            } catch (DataIntegrityViolationException e) {
                log.error("Erro ao tentar excluir o usuário! UsuarioService.excluir(?), " +
                        "violação de chave primária, id não encontrado no Banco de Dados");
                throw new EntidadeEmUsoException(
                        String.format(MSG_BENEFICIO_EM_USO, id));
            }
        });
    }
}
