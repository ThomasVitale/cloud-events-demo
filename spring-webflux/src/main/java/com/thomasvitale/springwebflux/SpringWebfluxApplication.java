package com.thomasvitale.springwebflux;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.spring.webflux.CloudEventHttpMessageReader;
import io.cloudevents.spring.webflux.CloudEventHttpMessageWriter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;

@SpringBootApplication
public class SpringWebfluxApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringWebfluxApplication.class, args);
	}

}

@RestController
class CloudEventController {

	@PostMapping("/event")
	public Mono<CloudEvent> event(@RequestBody Mono<CloudEvent> body) {
		return body.map(event -> CloudEventBuilder.from(event)
			.withId(UUID.randomUUID().toString())
			.withSource(URI.create("https://server.thomasvitale.com"))
			.withType("com.thomasvitale.events.Book")
			.withData(event.getData().toBytes())
			.build());
	}

}

@Configuration
class CloudEventHandlerConfiguration implements CodecCustomizer {

    @Override
    public void customize(CodecConfigurer configurer) {
        configurer.customCodecs().register(new CloudEventHttpMessageReader());
        configurer.customCodecs().register(new CloudEventHttpMessageWriter());
    }

}

record Book(String title){}
