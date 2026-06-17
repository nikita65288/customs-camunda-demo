package com.customs.customsdemo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.filter.CharacterEncodingFilter;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CustomsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String PROCESS_URL = "/api/customs/declaration/process";

    /**
     * Внутренняя тестовая конфигурация: принудительно переводит ответы в UTF-8,
     * чтобы MockMvc корректно распознавал кириллицу.
     */
    @TestConfiguration
    static class TestConfig {
        @Bean
        public CharacterEncodingFilter characterEncodingFilter() {
            CharacterEncodingFilter filter = new CharacterEncodingFilter();
            filter.setEncoding("UTF-8");
            filter.setForceResponseEncoding(true);
            return filter;
        }
    }

    /**
     * Успешный сценарий: валидный XML -> все делегаты -> HTML-отчёт.
     * Теперь кириллица проверяется без проблем.
     */
    @Test
    void processValidDeclaration_ShouldReturnReportHtml() throws Exception {
        String validXml = new String(
                new ClassPathResource("test-declaration.xml").getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );

        mockMvc.perform(post(PROCESS_URL)
                        .contentType(MediaType.APPLICATION_XML)
                        .content(validXml))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(containsString("Отчёт инспектора")))
                // Проверяем пошлину по первому товару
                .andExpect(content().string(containsString("250,000.00")))
                // Проверяем пошлину по второму товару
                .andExpect(content().string(containsString("360,000.00")))
                // Проверяем итоговую сумму
                .andExpect(content().string(containsString("610,000.00")));
    }

    /**
     * Ошибка валидации: невалидный XML -> 500 и сообщение об ошибке.
     */
    @Test
    void processInvalidDeclaration_ShouldReturnValidationError() throws Exception {
        String invalidXml = "<invalid>No root</invalid>";

        mockMvc.perform(post(PROCESS_URL)
                        .contentType(MediaType.APPLICATION_XML)
                        .content(invalidXml))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("Validation error")));
    }
}