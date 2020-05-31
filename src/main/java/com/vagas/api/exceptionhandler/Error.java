package com.vagas.api.exceptionhandler;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.OffsetDateTime;

public abstract class Error {

    public static void erroDeNegocio(DeferredResult<HttpEntity<?>> deferredResult, Throwable error, HttpStatus status) {
        Problem problem = Problem.builder()
                .status(status.value())
                .timestamp(OffsetDateTime.now())
                .type(ProblemType.ERRO_NEGOCIO.getUri())
                .title(error.getMessage())
                .detail(error.getMessage())
                .userMessage(error.getMessage())
                .build();
        deferredResult.setResult(new ResponseEntity<>(problem, status));
    }

}
