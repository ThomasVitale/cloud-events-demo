package com.thomasvitale.springrsocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.spring.codec.CloudEventDecoder;
import io.cloudevents.spring.codec.CloudEventEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
public class SpringRsocketApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringRsocketApplication.class, args);
	}

}

@Controller
class CloudEventController {

	private static final Logger log = LoggerFactory.getLogger(CloudEventController.class);
	private static final Map<Long,Book> books = new ConcurrentHashMap<>();

	private final ObjectMapper objectMapper;

	public CloudEventController(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@MessageMapping("request-response")
	public Mono<CloudEvent> requestResponse(final CloudEvent cloudEvent) {
		var book = convertToBook(cloudEvent.getData());
		books.put(book.id(), book);
		log.info("Received request-response request. Book: {}", book.title());
		return Mono.just(CloudEventBuilder.from(cloudEvent)
				.withId(UUID.randomUUID().toString())
				.withSource(URI.create("https://server.thomasvitale.com"))
				.withType("com.thomasvitale.events.Book")
				.withData(cloudEvent.getData().toBytes())
				.build());
	}

	@MessageMapping("fire-and-forget")
	public Mono<Void> fireAndForget(final CloudEvent cloudEvent) {
		var book = convertToBook(cloudEvent.getData());
		books.put(book.id(), book);
		log.info("Received fire-and-forget request. Book: {}", book.title());
		return Mono.empty();
	}

	@MessageMapping("request-stream")
	public Flux<CloudEvent> requestStream(final CloudEvent cloudEvent) {
		log.info("Received request-stream request. Message: '{}'", convertToString(cloudEvent.getData()));
		return Flux.fromIterable(books.values())
			.delayElements(Duration.ofSeconds(1))
			.map(book -> CloudEventBuilder.v1()
				.withId(UUID.randomUUID().toString())
				.withSource(URI.create("https://thomasvitale.com/books"))
				.withType("com.thomasvitale.events.Book")
				.withData(convertToBytes(book))
				.build())
			.log();
	}

	@MessageMapping("stream-stream")
	public Flux<CloudEvent> channel(final Flux<Integer> settings) {
		log.info("Received stream-stream request.");
		return settings
			.doOnNext(setting -> log.info("Requested interval is {} seconds", setting))
			.doOnCancel(() -> log.warn("The client cancelled the channel."))
			.switchMap(setting -> Flux.interval(Duration.ofSeconds(setting))
				.map(index -> CloudEventBuilder.v1()
					.withId(UUID.randomUUID().toString())
					.withSource(URI.create("https://thomasvitale.com/books"))
					.withType("com.thomasvitale.events.Message")
					.withData(convertToBytes(new Message("Book: " + index)))
					.build())
			).log();
	}

	private String convertToString(CloudEventData eventData) {
		try {
			return objectMapper.readValue(eventData.toBytes(), String.class);
		} catch (IOException e) {
			throw new IllegalArgumentException("The event body cannot be deserialized into a String.");
		}
	}

	private Book convertToBook(CloudEventData eventData) {
		try {
			return objectMapper.readValue(eventData.toBytes(), Book.class);
		} catch (IOException e) {
			throw new IllegalArgumentException("The event body cannot be deserialized into a Book.");
		}
	}

	private byte[] convertToBytes(Book book) {
		try {
			return objectMapper.writeValueAsBytes(book);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("The book is not serializable to bytes.");
		}
	}

	private byte[] convertToBytes(Message message) {
		try {
			return objectMapper.writeValueAsBytes(message);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("The message is not serializable to bytes.");
		}
	}

}

@Configuration
class RSocketConfiguration {

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public RSocketStrategiesCustomizer cloudEventsCustomizer() {
		return strategies -> {
			strategies.encoder(new CloudEventEncoder());
			strategies.decoder(new CloudEventDecoder());
		};
	}

}

record Book(Long id, String title){}

record Message(String content){}
