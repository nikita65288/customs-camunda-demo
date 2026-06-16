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

@Component
public class CalculateDutyDelegate implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(CalculateDutyDelegate.class);
    private static final String XSLT_PATH = "/xml/dutyCalculation.xslt";

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String validatedXml = (String) execution.getVariable("validatedXml");
        Double rate = (Double) execution.getVariable("rate");

        if (validatedXml == null || validatedXml.isEmpty()) {
            throw new RuntimeException("validatedXml variable is not set");
        }
        if (rate == null) {
            throw new RuntimeException("rate variable is not set");
        }

        TransformerFactory factory = TransformerFactory.newInstance();
        StreamSource xsltSource = new StreamSource(new ClassPathResource(XSLT_PATH).getInputStream());
        Transformer transformer = factory.newTransformer(xsltSource);

        // Передаём параметр rate в XSLT
        transformer.setParameter("rate", rate);

        StreamSource xmlSource = new StreamSource(new StringReader(validatedXml));
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);

        transformer.transform(xmlSource, result);

        String dutyXml = writer.toString();
        execution.setVariable("dutyXml", dutyXml);
        LOG.info("Duty calculation completed, dutyXml saved");
    }
}
