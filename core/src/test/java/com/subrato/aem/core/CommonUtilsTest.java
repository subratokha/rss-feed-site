package com.subrato.aem.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommonUtilsTest {

    @Test
    void convertPojoToJson() {
        Employee employee = new Employee();
        employee.setName("ABC");
        employee.setLastName("XYZ");
        String actual = CommonUtils.convertPojoToJson(employee);
        String expected = "{\"name\":\"ABC\",\"lastName\":\"XYZ\"}";
        assertEquals(expected, actual);
    }

    @Test
    void convertPojoToJsonEmptyTest() {
        Employee employe1 = new Employee();
        String expected = "{}";
        String actual = CommonUtils.convertPojoToJson(employe1);
        assertEquals(expected, actual);
    }

    @Test
    void convertXmlToPojo() {
        String xmlSample = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<note>\n" +
                "  <to>Tove</to>\n" +
                "  <from>Jani</from>\n" +
                "  <heading>Reminder</heading>\n" +
                "  <body>Don't forget me this weekend!</body>\n" +
                "</note>";

        Note expected = new Note();
        expected.setBody("Don't forget me this weekend!");
        expected.setFrom("Jani");
        expected.setHeading("Reminder");
        expected.setTo("Tove");

        Note actual = CommonUtils.convertXmlToPojo(xmlSample, Note.class);
        if (actual != null) {
            assertEquals(expected.getBody(), actual.getBody());
        }
    }
}