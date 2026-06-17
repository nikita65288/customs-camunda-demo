package com.customs.customsdemo.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.StringReader;

@Component
public class ValidateXmlDelegate implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(ValidateXmlDelegate.class);
    private static final String XSD_PATH = "/xml/declaration.xsd";

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String declarationXml = (String) execution.getVariable("declarationXml");
        if (declarationXml == null || declarationXml.isEmpty()) {
            throw new RuntimeException("The incoming declaration XML is missing");
        }

        Schema schema;
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            schema = factory.newSchema(new ClassPathResource(XSD_PATH).getURL());
        } catch (Exception e) {
            LOG.error("Error loading XSD schema", e);
            throw new RuntimeException("Failed to load XSD schema: " + e.getMessage());
        }

        Validator validator = schema.newValidator();
        try {
            validator.validate(new StreamSource(new StringReader(declarationXml)));
        } catch (org.xml.sax.SAXException e) {
            LOG.error("XML validation error", e);
            throw new RuntimeException("XML does not match the schema: " + e.getMessage());
        }

        // Успешная валидация – сохраняем исходный XML как валидированный
        execution.setVariable("validatedXml", declarationXml);
        LOG.info("XML successfully validated, validatedXml set");
    }
}
