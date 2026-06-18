package com.customs.customsdemo.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Делегат Camunda для генерации HTML-отчёта по результатам расчёта пошлин.
 *
 * Применяет XSLT-преобразование ({@code /xml/inspectionReport.xslt}) к XML
 * с рассчитанными пошлинами ({@code dutyXml}) и сохраняет полученный HTML
 * в переменную {@code reportHtml}.
 */
@Component
public class GenerateReportDelegate implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(GenerateReportDelegate.class);
    private static final String XSLT_PATH = "/xml/inspectionReport.xslt";

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String dutyXml = (String) execution.getVariable("dutyXml");

        if (dutyXml == null || dutyXml.isEmpty()) {
            throw new RuntimeException("The dutyXml variable is not set");
        }

        TransformerFactory factory = TransformerFactory.newInstance();
        StreamSource xsltSource = new StreamSource(new ClassPathResource(XSLT_PATH).getInputStream());
        Transformer transformer = factory.newTransformer(xsltSource);

        StreamSource xmlSource = new StreamSource(new StringReader(dutyXml));
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);

        transformer.transform(xmlSource, result);

        String reportHtml = writer.toString();
        execution.setVariable("reportHtml", reportHtml);
        LOG.info("The HTML report has been generated and reportHtml has been saved");
    }
}