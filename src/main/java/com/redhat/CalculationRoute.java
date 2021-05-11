package com.redhat;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jacksonxml.JacksonXMLDataFormat;
import org.apache.camel.component.jacksonxml.ListJacksonXMLDataFormat;
import org.apache.camel.model.dataformat.JaxbDataFormat;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.spi.DataFormat;
import org.apache.camel.support.builder.Namespaces;


public class CalculationRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
//		Namespaces ns = new Namespaces("ns2", "http://tempuri.org/");
		
		restConfiguration().bindingMode(RestBindingMode.auto).component("platform-http")
		.dataFormatProperty("prettyPrint", "true")
		.contextPath("/")
		.port(8080)
		.apiContextPath("/openapi")
		.apiProperty("api.title", "Camel Quarkus Demo API")
		.apiProperty("api.version", "1.0.0-SNAPSHOT")
		.apiProperty("cors", "true");

		rest()
		.tag("Camel, Quarkus and SOAP Demonstration")
		.produces("application/json")
		.get("/add/{num1}.{num2}")
		.description("ADD Endpoint to sum value1 and value2")
		.route().routeId("restcalculateadd")
		.to("direct:calculateaddws")
		.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
		.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
		.endRest();
		
		
		
		JacksonXMLDataFormat format = new JacksonXMLDataFormat();
		format.useList();
		format.setUnmarshalType(AddResponse.class);

		from("direct:calculateaddws").routeId("calculateadd").to("qute:soap-env-add.xml")
		.setHeader(Exchange.HTTP_METHOD, constant("POST"))
		.setHeader(Exchange.CONTENT_TYPE, constant("application/soap+xml;charset=UTF-8;action=\"http://tempuri.org/Add"))
		.log("request ${body}")
		.toD("http://{{calculatorws.url}}/calculator.asmx?bridgeEndpoint=true")
		.log("Response ${body}")
		.unmarshal(format);
	}

}
