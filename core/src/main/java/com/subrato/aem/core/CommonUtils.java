package com.subrato.aem.core;

import com.day.cq.wcm.api.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.subrato.aem.core.dtos.Rss;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.Reader;
import java.io.StringReader;
import java.util.Objects;

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
     * Convert String XML Representation of RSS Feed to POJO
     *
     * @param xml String XML
     * @return RSS Object
     */
    public static Rss convertXmlToPojo(String xml) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Rss.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Reader reader = new StringReader(xml);
            return (Rss) unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            LOG.error("Error parsing the XML to Objects", e);
        }
        return null;
    }
}
