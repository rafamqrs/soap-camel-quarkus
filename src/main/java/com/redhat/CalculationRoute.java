package com.redhat;

import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class CalculationRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		restConfiguration().bindingMode(RestBindingMode.auto).component("platform-http")
				.dataFormatProperty("prettyPrint", "true").contextPath("/").port(8080).apiContextPath("/openapi")
				.apiProperty("api.title", "Camel Quarkus SOAP API").apiProperty("api.version", "1.0.0-SNAPSHOT")
				.apiProperty("cors", "true");

		rest().tag("Camel, Quarkus and SOAP Demonstration").produces("application/json").get("/add/{num1}.{num2}")
				.description("ADD Endpoint to sum value1 and value2").route().routeId("restcalculateadd")
				.to("direct:calculateaddws").setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200)).endRest();

		from("direct:calculateaddws").routeId("calculateadd").to("qute:soap-env-add.xml")
				.setHeader(Exchange.HTTP_METHOD, constant("POST"))
				.setHeader(Exchange.CONTENT_TYPE,
						constant("application/soap+xml;charset=UTF-8;action=\"http://tempuri.org/Add"))
				.log("request ${body}").toD("http://{{calculatorws.url}}/calculator.asmx?bridgeEndpoint=true")
				.log("Response ${body}").process(new Processor() {

					@Override
					public void process(Exchange exchange) throws Exception {
						System.out.println("Exchange body: " + exchange.getIn().getBody(String.class));
						XmlMapper mapper = new XmlMapper();
						AddResponse response = null;
						mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
						XMLInputFactory factory = XMLInputFactory.newFactory();
						XMLStreamReader reader = factory
								.createXMLStreamReader(new StringReader(exchange.getIn().getBody(String.class)));
						while (reader.hasNext()) {
							int type = reader.next();
							if (type == XMLStreamReader.START_ELEMENT && "AddResponse".equals(reader.getLocalName())) {
								response = mapper.readValue(reader, AddResponse.class);
							}
						}
						exchange.getIn().setBody(response);

					}
				});
	}

}
