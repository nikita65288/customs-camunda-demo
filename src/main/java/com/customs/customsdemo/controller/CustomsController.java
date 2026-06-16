package com.customs.customsdemo.controller;

import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.runtime.ProcessInstanceWithVariables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/customs/declaration")
public class CustomsController {

    private final RuntimeService runtimeService;

    @Autowired
    public CustomsController(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    @PostMapping("/process")
    public ResponseEntity<String> processDeclaration(@RequestBody String declarationXml) {
        try {
            // Синхронный запуск процесса и получение результирующих переменных
            ProcessInstanceWithVariables instance = runtimeService
                    .createProcessInstanceByKey("customsDeclarationProcessing")
                    .setVariable("declarationXml", declarationXml)
                    .executeWithVariablesInReturn();

            Map<String, Object> result = instance.getVariables();
            String reportHtml = (String) result.get("reportHtml");
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(reportHtml);

        } catch (ProcessEngineException e) {
            String errorMessage = extractBpmnErrorMessage(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(errorMessage);
        }
    }

    /**
     * Извлекает текст ошибки BPMN из цепочки исключений.
     */
    private String extractBpmnErrorMessage(ProcessEngineException e) {
        Throwable cause = e;
        while (cause != null) {
            if (cause instanceof BpmnError) {
                return "Ошибка обработки: " + cause.getMessage();
            }
            cause = cause.getCause();
        }
        if (e != null) {
            return "Внутренняя ошибка: " + e.getMessage();
        } else {
            return "Неизвестная ошибка";
        }
    }
}