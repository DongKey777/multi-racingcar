class WebSocketClient {
    constructor(url) {
        this.url = url;
        this.ws = null;
        this.onMessageCallback = null;
        this.onCloseCallback = null;
        this.onErrorCallback = null;
        this.onOpenCallback = null;
    }

    connect(onOpen, onMessage, onClose, onError) {
        this.onOpenCallback = onOpen;
        this.onMessageCallback = onMessage;
        this.onCloseCallback = onClose;
        this.onErrorCallback = onError;

        this.ws = new WebSocket(this.url);

        this.ws.onopen = () => {
            console.log('WebSocket opened');
            if (this.onOpenCallback) {
                this.onOpenCallback();
            }
        };

        this.ws.onmessage = (event) => {
            console.log('Message received:', event.data);
            if (this.onMessageCallback) {
                this.onMessageCallback(event.data);
            }
        };

        this.ws.onerror = (error) => {
            console.error('WebSocket error:', error);
            if (this.onErrorCallback) {
                this.onErrorCallback(error);
            }
        };

        this.ws.onclose = () => {
            console.log('WebSocket closed');
            if (this.onCloseCallback) {
                this.onCloseCallback();
            }
            this.ws = null;
        };
    }

    send(message) {
        if (this.ws && this.ws.readyState === WebSocket.OPEN) {
            this.ws.send(message);
            return true;
        }
        return false;
    }

    close() {
        if (this.ws) {
            this.ws.close();
            this.ws = null;
        }
    }

    isConnected() {
        return this.ws !== null && this.ws.readyState === WebSocket.OPEN;
    }
}
