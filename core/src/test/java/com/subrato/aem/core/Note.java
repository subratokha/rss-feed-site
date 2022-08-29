package com.subrato.aem.core;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "note")
public class Note {
    private String heading;
    private String from;
    private String to;
    private String body;

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
