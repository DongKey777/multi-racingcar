document.addEventListener('DOMContentLoaded', function () {
    const lastLogin = document.getElementById('last-login');
    if (lastLogin) {
        const now = new Date();

        const days = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
        const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
            'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

        const dayName = days[now.getDay()];
        const monthName = months[now.getMonth()];
        const day = now.getDate();
        const hours = String(now.getHours()).padStart(2, '0');
        const minutes = String(now.getMinutes()).padStart(2, '0');
        const seconds = String(now.getSeconds()).padStart(2, '0');

        lastLogin.textContent = `Last login: ${dayName} ${monthName} ${day} ${hours}:${minutes}:${seconds} on ttys021\n`;
    }
});

let ws = null;

function startGame() {
    if (ws && ws.readyState === WebSocket.OPEN) {
        log('Already connected.');
        return;
    }

    log('Connecting to server...');

    ws = new WebSocket('ws://localhost:8080/ws');

    ws.onopen = function () {
        log('Connected to server');
        log('Waiting for players... (1/4)');
        log('');

        ws.send('Player joined');
    };

    ws.onmessage = function (event) {
        log('Server: ' + event.data);
    };

    ws.onerror = function (error) {
        log('Connection failed');
        console.error('WebSocket error:', error);
    };

    ws.onclose = function () {
        log('Connection closed');
        ws = null;
    };
}

function log(message) {
    document.body.innerHTML += message + '\n';

    window.scrollTo(0, document.body.scrollHeight);
}

document.addEventListener('keypress', function (e) {
    if (e.key === 'Enter') {
        startGame();
    }
});