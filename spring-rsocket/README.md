# Spring RSocket

## Request/Response

```shell
java -jar rsc-0.9.1.jar --request --dataMimeType=application/cloudevents+json --route=request-response \
    --data='{"data": {"id": 21, "title": "The Lord of the Rings"},
           "id": "21",
           "source": "https://cli.thomasvitale.com",
           "type": "com.thomasvitale.event.Book",
           "specversion": "1.0"}' \
    --debug tcp://localhost:9000
```

## Fire and forget

```shell
java -jar rsc-0.9.1.jar --fnf --dataMimeType=application/cloudevents+json --route=fire-and-forget \
    --data='{"data": {"id": 394, "title": "The Hobbit"},
           "id": "394",
           "source": "https://cli.thomasvitale.com",
           "type": "com.thomasvitale.event.Book",
           "specversion": "1.0"}' \
    --debug tcp://localhost:9000
```

## Request/Stream

```shell
java -jar rsc-0.9.1.jar --stream --dataMimeType=application/cloudevents+json --route=request-stream \
    --data='{"data": "I love reading",
           "id": "1",
           "source": "https://cli.thomasvitale.com",
           "type": "com.thomasvitale.event.Message",
           "specversion": "1.0"}' \
    --debug tcp://localhost:9000
```

## Channel

```shell
java -jar rsc-0.9.1.jar --channel --route=stream-stream \
    --data - \
    --debug tcp://localhost:9000
```
