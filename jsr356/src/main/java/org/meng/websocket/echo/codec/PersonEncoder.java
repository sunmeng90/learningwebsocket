package org.meng.websocket.echo.codec;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

public class PersonEncoder implements Encoder.Text<Person> {
    @Override
    public String encode(Person object) throws EncodeException {
        object.setName(object.getName()+" new");
        JAXBContext jaxbContext = null;
        StringWriter st = null;

        try {
            jaxbContext = JAXBContext.newInstance(Person.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            st = new StringWriter();
            marshaller.marshal(object, st);
            System.out.println("Outgoing XML: " + st.toString());
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return st.toString();
    }

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }
}
