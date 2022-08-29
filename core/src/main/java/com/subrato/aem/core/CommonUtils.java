package com.subrato.aem.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.Reader;
import java.io.StringReader;

public class CommonUtils {

    private static final Logger LOG = LoggerFactory.getLogger(CommonUtils.class);

    private CommonUtils() {
    }

    /**
     * Convert POJO to Json String
     *
     * @param object Object to be converted
     * @return Json String
     */
    public static String convertPojoToJson(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            LOG.error("Error processing POJO to Json", e);
        }
        return "[]";
    }

    /**
     * Convert String XML Representation to POJO
     *
     * @param xml String XML
     * @return RSS Object
     */
    public static <T> T convertXmlToPojo(String xml,final Class<T> clazz) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Reader reader = new StringReader(xml);
            return (T) unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            LOG.error("Error parsing the XML to Objects", e);
        }
        return null;
    }
}
