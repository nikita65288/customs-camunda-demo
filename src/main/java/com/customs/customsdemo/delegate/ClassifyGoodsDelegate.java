package com.customs.customsdemo.delegate;

import com.customs.customsdemo.entity.CustomsRate;
import com.customs.customsdemo.repository.CustomsRatesRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Optional;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

@Component
public class ClassifyGoodsDelegate implements JavaDelegate {

    private final CustomsRatesRepository ratesRepository;
    private static final Logger LOG = LoggerFactory.getLogger(ClassifyGoodsDelegate.class);

    public ClassifyGoodsDelegate(CustomsRatesRepository ratesRepository) {
        this.ratesRepository = ratesRepository;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String validatedXml = (String) execution.getVariable("validatedXml");
        if (validatedXml == null || validatedXml.isEmpty()) {
            throw new RuntimeException("validatedXml is missing");
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(validatedXml)));

        NodeList itemNodes = doc.getElementsByTagName("item");
        for (int i = 0; i < itemNodes.getLength(); i++) {
            Element item = (Element) itemNodes.item(i);
            String code = item.getElementsByTagName("code").item(0).getTextContent();
            Optional<CustomsRate> rateEntity = ratesRepository.findById(code);
            double rate = rateEntity.map(CustomsRate::getDutyRate).orElse(0.15);
            // Добавляем новый элемент <dutyRate> под <item>
            Element dutyRateElem = doc.createElement("dutyRate");
            dutyRateElem.setTextContent(String.valueOf(rate));
            item.appendChild(dutyRateElem);
        }

        // Сериализуем обогащённый XML
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        String enrichedXml = writer.toString();

        execution.setVariable("enrichedXml", enrichedXml);
        LOG.info("Enriched XML: {}", enrichedXml);
    }
}