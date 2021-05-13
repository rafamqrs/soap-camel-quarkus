package com.redhat;


//camel-k: dependency=mvn:org.apache.camel.quarkus:camel-quarkus-jackson
//camel-k: dependency=mvn:org.apache.camel.quarkus:camel-quarkus-jacksonxml
//camel-k: dependency=mvn:org.apache.camel.quarkus:camel-quarkus-qute
//camel-k: dependency=mvn:org.apache.camel.quarkus:camel-quarkus-openapi-java
import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;



public class CalculationRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		restConfiguration().bindingMode(RestBindingMode.auto).component("platform-http")
				.dataFormatProperty("prettyPrint", "true").contextPath("/").port(8080).apiContextPath("/openapi")
				.apiProperty("api.title", "Camel Quarkus SOAP API").apiProperty("api.version", "1.0.0-SNAPSHOT")
				.apiProperty("cors", "true");

		rest().tag("Camel, Quarkus and SOAP Demonstration").produces("application/json").get("/add")
				.param().name("number1").type(RestParamType.query).defaultValue("0").description("Number 1").endParam()
				.param().name("number2").type(RestParamType.query).defaultValue("0").description("Number 2").endParam()
				.description("ADD Endpoint to sum value1 and value2").route().routeId("restcalculateadd")
				.to("direct:calculateaddws").setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200)).endRest();

		from("direct:calculateaddws")
		.routeId("calculateadd")
		.to("qute:soap-env-add.xml")
				.setHeader(Exchange.HTTP_METHOD, constant("POST"))
				.setHeader(Exchange.CONTENT_TYPE,
						constant("application/soap+xml;charset=UTF-8;action=\"http://tempuri.org/Add"))
				.log("request ${body}")
				.toD("http://{{calculatorws-url}}/calculator.asmx?bridgeEndpoint=true")
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

//	@JsonIgnoreProperties(ignoreUnknown = true)
//	public class AddResponse {
//		
//		@JacksonXmlProperty(localName="AddResult")
//		protected int addResult;
//
//		/**
//		 * Gets the value of the addResult property.
//		 * 
//		 */
//		public int getAddResult() {
//			return addResult;
//		}
//
//		/**
//		 * Sets the value of the addResult property.
//		 * 
//		 */
//		public void setAddResult(int value) {
//			this.addResult = value;
//		}
//
//	}
}


