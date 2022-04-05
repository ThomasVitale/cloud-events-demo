package com.thomasvitale.springrsocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.core.builder.CloudEventBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.rsocket.context.LocalRSocketServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SpringRsocketApplicationTests {

	private static ObjectMapper objectMapper;
	private static RSocketRequester requester;

	@BeforeAll
	static void setupOnce(
		@Autowired ObjectMapper mapper,
		@Autowired RSocketRequester.Builder builder,
		@LocalRSocketServerPort Integer port,
		@Autowired RSocketStrategies strategies
	) {
		objectMapper = mapper;
		requester = builder
			.rsocketStrategies(strategies)
			.tcp("localhost", port);
	}

	@Test
	void testRequestResponse() {
		var bookToSend = new Book(21L, "The Lord of the Rings");

		Mono<CloudEvent> cloudEventResponse = requester
			.route("request-response")
			.data(CloudEventBuilder.v1()
				.withId(UUID.randomUUID().toString())
				.withSource(URI.create("https://client.thomasvitale.com"))
				.withType("com.thomasvitale.events.Book")
				.withData(convertToBytes(bookToSend))
				.build())
			.retrieveMono(CloudEvent.class);
		
		StepVerifier.create(cloudEventResponse)
			.consumeNextWith(event -> {
				var returnedBook = convertToBook(event.getData());
				assertThat(returnedBook.title()).isEqualTo(bookToSend.title());
			})
			.verifyComplete();
	}

	@Test
	void testFireAndForget() {
		var bookToSend = new Book(394L, "The Hobbit");

		Mono<Void> cloudEventResponse = requester
			.route("fire-and-forget")
			.data(CloudEventBuilder.v1()
				.withId(UUID.randomUUID().toString())
				.withSource(URI.create("https://client.thomasvitale.com"))
				.withType("com.thomasvitale.events.Book")
				.withData(convertToBytes(bookToSend))
				.build())
			.send();
		
		StepVerifier.create(cloudEventResponse)
			.verifyComplete();
	}

	@Test
	void testRequestStream() {
		var messageToSend = "I love reading";

		Flux<CloudEvent> requestStream = requester
			.route("request-stream")
			.data(CloudEventBuilder.v1()
				.withId(UUID.randomUUID().toString())
				.withSource(URI.create("https://client.thomasvitale.com"))
				.withType("com.thomasvitale.events.Message")
				.withData(convertToBytes(messageToSend))
				.build())
			.retrieveFlux(CloudEvent.class);
		
		StepVerifier.create(requestStream)
			.thenCancel()
			.verify();
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

	private byte[] convertToBytes(String message) {
		try {
			return objectMapper.writeValueAsBytes(message);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("The string is not serializable to bytes.");
		}
	}

}
