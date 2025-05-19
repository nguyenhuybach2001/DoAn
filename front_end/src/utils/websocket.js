// utils/notificationSocket.js
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

export const connectNotificationSocket = (
  recipientId,
  onNotificationReceived
) => {
  const socketUrl = "http://localhost:8080/api/ws";
  const client = new Client({
    webSocketFactory: () => new SockJS(socketUrl),
    reconnectDelay: 5000,
    debug: (str) => {
      console.log("[WebSocket Debug]", str);
    },
    onConnect: () => {
      const topics = [
        "SERVICEBILL",
        "REQUEST",
        "MAINTENANCEEXPENSE",
        "CONTRACT",
      ];
      topics.forEach((topic) => {
        const destination = `/topic/${topic.toLowerCase()}${recipientId}`;
        client.subscribe(destination, (message) => {
          if (message.body) {
            const data = JSON.parse(message.body);
            onNotificationReceived(data);
          }
        });
      });
    },
  });

  client.activate();
  return client;
};
