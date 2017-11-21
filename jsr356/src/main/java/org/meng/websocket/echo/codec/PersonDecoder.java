package org.meng.websocket.echo.codec;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

public class PersonDecoder implements Decoder.Text<Person> {
    @Override
    public Person decode(String s) throws DecodeException {
        System.out.println("Incoming XML: " + s);
        Person person = null;
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(Person.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            StringReader reader = new StringReader(s);
            person = (Person) unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return person;
    }

    @Override
    public boolean willDecode(String s) {
        return (s != null);
    }

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }
}
