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

    document.addEventListener('click', function () {
        if (inputMode === 'nickname') {
            const input = document.getElementById('nickname-input');
            if (input) {
                input.focus();
            }
        }

        const retryInput = document.getElementById('nickname-input-retry');
        if (retryInput) {
            retryInput.focus();
        }
    });
});

let ws = null;
let inputMode = 'nickname';
let currentNickname = null;
let selectedMode = null;

function showNicknamePrompt() {
    const content = document.getElementById('content');
    content.innerHTML = 'Enter your nickname: <input type="text" id="nickname-input" maxlength="10" autofocus />';

    const input = document.getElementById('nickname-input');

    setTimeout(() => input.focus(), 100);

    input.addEventListener('keydown', handleNicknameInput);

    inputMode = 'nickname';
}

function handleNicknameInput(e) {
    const input = document.getElementById('nickname-input');

    if (e.key === 'Enter') {
        e.preventDefault();
        const nickname = input.value.trim();

        if (nickname.length === 0) {
            return;
        }

        input.removeEventListener('keydown', handleNicknameInput);

        showConnectButton(nickname);
    }
}

function showConnectButton(nickname) {
    const content = document.getElementById('content');
    content.innerHTML = 'Enter your nickname: ' + nickname + '\n\n';

    showModeSelection(nickname);
    inputMode = 'mode';
}

function showModeSelection(nickname) {
    const modeSection = document.getElementById('mode-selection');
    if (modeSection) {
        modeSection.style.display = 'block';
    }

    const singleBtn = document.getElementById('single-mode-btn');
    const multiBtn = document.getElementById('multi-mode-btn');

    singleBtn.onclick = () => selectMode(nickname, 'SINGLE');
    multiBtn.onclick = () => selectMode(nickname, 'MULTI');

    setTimeout(() => multiBtn.focus(), 100);
}

function selectMode(nickname, mode) {
    selectedMode = mode;

    const modeSection = document.getElementById('mode-selection');
    if (modeSection) {
        modeSection.style.display = 'none';
    }

    const content = document.getElementById('content');
    const modeText = mode === 'SINGLE' ? 'Single Player' : 'Multiplayer';
    content.innerHTML += 'Selected mode: ' + modeText + '\n\n';

    connectToServer(nickname);
}

function connectToServer(nickname) {
    if (ws && ws.readyState === WebSocket.OPEN) {
        console.log('Already connected');
        return;
    }

    currentNickname = nickname;

    const content = document.getElementById('content');
    const button = document.getElementById('connect-btn');
    if (button) button.remove();

    hideRestartButton();

    content.innerHTML = 'Enter your nickname: ' + nickname + '\n\nConnecting to server...\n';

    ws = new WebSocket('ws://localhost:8080/ws');

    ws.onopen = function () {
        console.log('WebSocket opened');
        content.innerHTML += 'Connected to server\n\n';
        content.innerHTML += 'Joining as: ' + nickname + '\n';

        const message = JSON.stringify({
            nickname: nickname,
            mode: selectedMode || 'MULTI'
        });
        ws.send(message);
        inputMode = 'game';
    };

    ws.onmessage = function (event) {
        console.log('Message received:', event.data);
        content.innerHTML += event.data + '\n';
        window.scrollTo(0, document.body.scrollHeight);

        if (event.data.includes('입장 실패')) {
            content.innerHTML += '\nEnter your nickname: <input type="text" id="nickname-input-retry" maxlength="10" autofocus />\n';

            const retryInput = document.getElementById('nickname-input-retry');
            setTimeout(() => retryInput.focus(), 100);

            retryInput.addEventListener('keydown', function (e) {
                if (e.key === 'Enter') {
                    e.preventDefault();
                    const newNickname = retryInput.value.trim();

                    if (newNickname.length === 0) {
                        return;
                    }

                    retryInput.remove();
                    content.innerHTML += newNickname + '\n\nRetrying...\n';

                    const message = JSON.stringify({
                        nickname: newNickname,
                        mode: selectedMode || 'MULTI'
                    });
                    ws.send(message);
                    currentNickname = newNickname;
                }
            });
            return;
        }

        if (event.data.includes('게임 종료') || event.data.includes('최종 우승자')) {
            setTimeout(showRestartButton, 1000);
        }
    };

    ws.onerror = function (error) {
        console.error('WebSocket error:', error);
        content.innerHTML += 'Connection failed\n';
    };

    ws.onclose = function () {
        console.log('WebSocket closed');
        content.innerHTML += 'Connection closed\n';
        ws = null;
    };
}

function showRestartButton() {
    const restartSection = document.getElementById('restart-section');
    if (restartSection) {
        restartSection.style.display = 'block';
    }

    const restartBtn = document.getElementById('restart-btn');
    if (restartBtn && !restartBtn.hasAttribute('data-listener')) {
        restartBtn.setAttribute('data-listener', 'true');
        restartBtn.addEventListener('click', handleRestart);
    }
}

function hideRestartButton() {
    const restartSection = document.getElementById('restart-section');
    if (restartSection) {
        restartSection.style.display = 'none';
    }
}

function handleRestart() {
    console.log('Restarting game...');

    if (ws) {
        ws.close();
        ws = null;
    }

    hideRestartButton();

    selectedMode = null;

    const content = document.getElementById('content');
    content.innerHTML = '';

    if (currentNickname) {
        showConnectButton(currentNickname);
    } else {
        showNicknamePrompt();
    }
}