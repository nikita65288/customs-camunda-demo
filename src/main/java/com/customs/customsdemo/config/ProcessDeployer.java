package com.customs.customsdemo.config;

import org.camunda.bpm.engine.RepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Автоматический деплой BPMN-процессов при запуске приложения.
 *
 * Сканирует папку classpath*:/processes/ на наличие файлов с расширением .bpmn
 * и развёртывает их в движке Camunda.
 *
 */
@Component
public class ProcessDeployer {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessDeployer.class);

    private final RepositoryService repositoryService;

    public ProcessDeployer(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    /**
     * Обработчик события готовности приложения (после запуска контекста).
     * Выполняет деплой всех найденных BPMN-файлов.
     *
     * В случае отсутствия файлов логирует предупреждение.
     * При ошибках ввода-вывода логирует ошибку и продолжает работу.
     *
     */
    @EventListener(ApplicationReadyEvent.class)
    public void deployProcesses() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath*:/processes/*.bpmn");
            for (Resource resource : resources) {
                String fileName = resource.getFilename();
                LOG.info("Deploying process: {}", fileName);
                repositoryService.createDeployment()
                        .addInputStream(fileName, resource.getInputStream())
                        .name(fileName)
                        .deploy();
                LOG.info("Deployed process: {}", fileName);
            }
            if (resources.length == 0) {
                LOG.warn("No BPMN files found in classpath*:/processes/");
            }
        } catch (IOException e) {
            LOG.error("Failed to deploy processes", e);
        }
    }
}
