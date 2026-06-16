package com.customs.customsdemo.delegate;

import com.customs.customsdemo.repository.CustomsRatesRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.io.StringReader;
import java.util.Optional;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

@Component
public class ClassifyGoodsDelegate implements JavaDelegate {

    private final CustomsRatesRepository ratesRepository;

    public ClassifyGoodsDelegate(CustomsRatesRepository ratesRepository) {
        this.ratesRepository = ratesRepository;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String validatedXml = (String) execution.getVariable("validatedXml");
        String goodsCode = extractGoodsCode(validatedXml);

        Optional<com.customs.customsdemo.entity.CustomsRate> rateEntity = ratesRepository.findById(goodsCode);
        double rate = rateEntity.map(com.customs.customsdemo.entity.CustomsRate::getDutyRate).orElse(0.15);
        execution.setVariable("rate", rate);
    }

    private String extractGoodsCode(String xml) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new InputSource(new StringReader(xml)));
        NodeList nodes = doc.getElementsByTagName("code");
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        throw new RuntimeException("Goods code not found in XML");
    }
}