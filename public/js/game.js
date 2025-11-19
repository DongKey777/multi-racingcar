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

    showNicknamePrompt();
});

let ws = null;
let inputMode = 'nickname';

function showNicknamePrompt() {
    const content = document.getElementById('content');
    content.innerHTML = 'Enter your nickname: <input type="text" id="nickname-input" maxlength="10" />';

    const input = document.getElementById('nickname-input');

    setTimeout(() => input.focus(), 0);
    setTimeout(() => input.focus(), 100);
    setTimeout(() => input.focus(), 300);

    document.addEventListener('keydown', function (e) {
        if (inputMode === 'nickname' && document.activeElement !== input) {
            input.focus();
        }
    });

    document.body.addEventListener('click', function () {
        if (inputMode === 'nickname') {
            input.focus();
        }
    });

    input.addEventListener('keydown', function (e) {
        if (e.key === 'Enter') {
            e.preventDefault();
            const nickname = input.value.trim();

            if (nickname.length === 0) {
                return;
            }

            showConnectButton(nickname);
        }
    });

    inputMode = 'nickname';
}

function showConnectButton(nickname) {
    const content = document.getElementById('content');

    content.innerHTML = 'Enter your nickname: ' + nickname + '\n\n<button onclick="connectToServer(\'' + nickname + '\')" id="connect-btn">Press Enter to Connect</button>\n\nracing-game ❯ ';
    inputMode = 'button';

    const button = document.getElementById('connect-btn');
    setTimeout(() => button.focus(), 100);
}

function connectToServer(nickname) {
    if (ws && ws.readyState === WebSocket.OPEN) {
        return;
    }

    const content = document.getElementById('content');
    const button = document.getElementById('connect-btn');
    if (button) button.remove();

    content.innerHTML = 'Enter your nickname: ' + nickname + '\n\nConnecting to server...\n';

    ws = new WebSocket('ws://localhost:8080/ws');

    ws.onopen = function () {
        content.innerHTML += 'Connected to server\n\n';
        content.innerHTML += 'Joining as: ' + nickname + '\n';

        ws.send(nickname);
        inputMode = 'game';
    };

    ws.onmessage = function (event) {
        content.innerHTML += event.data + '\n';
        window.scrollTo(0, document.body.scrollHeight);
    };

    ws.onerror = function (error) {
        content.innerHTML += 'Connection failed\n';
        console.error('WebSocket error:', error);
    };

    ws.onclose = function () {
        content.innerHTML += 'Connection closed\n';
        ws = null;
    };
}