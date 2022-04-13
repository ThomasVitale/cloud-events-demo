import {Component, OnDestroy, OnInit} from '@angular/core';
import {IdentitySerializer, JsonSerializer, RSocketClient} from "rsocket-core";
import RSocketWebSocketClient from "rsocket-websocket-client";
import {CloudEvent} from "cloudevents";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, OnDestroy {
  title = "angular-rsocket";
  messages: any[] = [];
  client: any;

  ngOnInit(): void {
    // Creates an RSocket client based on the WebSocket network protocol
    this.client = new RSocketClient({
      serializers: {
        data: JsonSerializer,
        metadata: IdentitySerializer
      },
      setup: {
        keepAlive: 60000,
        lifetime: 180000,
        dataMimeType: 'application/cloudevents+json',
        metadataMimeType: 'message/x.rsocket.routing.v0',
      },
      transport: new RSocketWebSocketClient({
        url: 'ws://localhost:9000'
      }),
    });

    // Open an RSocket connection to the server
    this.client.connect().subscribe({
      onComplete: (socket: any) => {
        socket
          .requestStream({
            data: null,
            metadata: this.route('infinite-stream-ce')
          }).subscribe({
          onComplete: () => console.log('complete'),
          onError: (error: string) => {
            console.log("Connection has been closed due to: " + error);
          },
          onNext: (payload: { data: CloudEvent; }) => {
            console.log(payload);
            let cloudEvent = payload.data;
            if (cloudEvent.type === 'com.thomasvitale.events.Message') {
              this.addMessage(cloudEvent.data);
            }
          },
          onSubscribe: (subscription: any) => {
            subscription.request(1000000);
          },
        });
      },
      onError: (error: string) => {
        console.log("RSocket connection refused due to: " + error);
      },
      onSubscribe: (cancel: any) => {
        /* call cancel() to abort */
      }
    });
  }

  ngOnDestroy(): void {
    if (this.client) {
      this.client.close();
    }
  }

  route(value: string) : string {
    return String.fromCharCode(value.length) + value;
  }

  addMessage(newMessage: any) {
    console.log("Add message:" + JSON.stringify(newMessage))
    this.messages = [...this.messages, newMessage];
  }

}
