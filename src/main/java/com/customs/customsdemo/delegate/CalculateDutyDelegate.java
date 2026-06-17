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
        String enrichedXml = (String) execution.getVariable("enrichedXml");
        if (enrichedXml == null || enrichedXml.isEmpty()) {
            throw new RuntimeException("enrichedXml variable is not set");
        }

        TransformerFactory factory = TransformerFactory.newInstance();
        StreamSource xsltSource = new StreamSource(new ClassPathResource(XSLT_PATH).getInputStream());
        Transformer transformer = factory.newTransformer(xsltSource);

        StreamSource xmlSource = new StreamSource(new StringReader(enrichedXml));
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);

        transformer.transform(xmlSource, result);

        String dutyXml = writer.toString();
        execution.setVariable("dutyXml", dutyXml);
        LOG.info("Duty calculation completed, dutyXml saved");
    }
}
