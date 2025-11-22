class GameApp {
    constructor() {
        this.state = new GameState();
        this.ui = new UIRenderer(
            document.getElementById('content'),
            document.getElementById('mode-selection'),
            document.getElementById('restart-section')
        );
        this.client = null;
    }

    start() {
        this.initializeLastLogin();
        this.ui.renderNicknameInput();
        this.setupNicknameInput();
        this.setupClickToFocus();
    }

    initializeLastLogin() {
        const lastLogin = document.getElementById('last-login');
        if (!lastLogin) return;

        const now = new Date();
        const days = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
        const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

        const dayName = days[now.getDay()];
        const monthName = months[now.getMonth()];
        const day = now.getDate();
        const hours = String(now.getHours()).padStart(2, '0');
        const minutes = String(now.getMinutes()).padStart(2, '0');
        const seconds = String(now.getSeconds()).padStart(2, '0');

        lastLogin.textContent = `Last login: ${dayName} ${monthName} ${day} ${hours}:${minutes}:${seconds} on ttys021\n`;
    }

    setupClickToFocus() {
        document.addEventListener('click', () => {
            const input = document.getElementById('nickname-input');
            if (input) input.focus();

            const retryInput = document.getElementById('nickname-input-retry');
            if (retryInput) retryInput.focus();
        });
    }

    setupNicknameInput() {
        const input = document.getElementById('nickname-input');
        if (!input) return;

        input.addEventListener('keydown', (e) => this.handleNicknameInput(e));
    }

    handleNicknameInput(e) {
        if (e.key !== 'Enter') return;

        e.preventDefault();
        const input = document.getElementById('nickname-input');
        const nickname = input.value.trim();

        if (nickname.length === 0) return;

        const validation = NicknameValidator.validate(nickname);
        if (!validation.valid) {
            this.ui.renderValidationError(nickname, validation.error);
            this.setupNicknameInput();
            return;
        }

        this.state.setNickname(nickname);
        this.showModeSelection();
    }

    showModeSelection() {
        const nickname = this.state.getNickname();
        this.ui.renderModeSelection(nickname);

        const singleBtn = document.getElementById('single-mode-btn');
        const multiBtn = document.getElementById('multi-mode-btn');

        singleBtn.onclick = () => this.selectMode('SINGLE');
        multiBtn.onclick = () => this.selectMode('MULTI');

        setTimeout(() => multiBtn.focus(), 100);
    }

    selectMode(mode) {
        this.state.setMode(mode);
        this.ui.hideModeSelection();
        this.connectToServer();
    }

    connectToServer() {
        if (this.client && this.client.isConnected()) {
            console.log('Already connected');
            return;
        }

        const nickname = this.state.getNickname();
        const mode = this.state.getMode();

        this.ui.hideRestartButton();
        this.ui.renderConnecting(nickname, mode);

        this.client = new WebSocketClient('ws://localhost:8080/ws');
        this.client.connect(
            () => this.handleOpen(nickname, mode),
            (message) => this.handleMessage(message),
            () => this.handleClose(),
            (error) => this.handleError(error)
        );
    }

    handleOpen(nickname, mode) {
        this.ui.renderConnected(nickname);
        const joinMessage = JSON.stringify({nickname, mode});
        this.client.send(joinMessage);
    }

    handleMessage(message) {
        if (message.includes('게임 시작')) {
            this.ui.clearScreen();
            this.state.setState('playing');
        }

        if (message.includes('대기 중...')) {
            this.ui.updateWaitingMessage(message);
        } else {
            this.ui.appendMessage(message);
        }

        if (message.includes('입장 실패')) {
            this.ui.renderRetryNicknameInput();
            this.setupRetryInput();
            return;
        }

        if (message.includes('게임 종료') || message.includes('최종 우승자')) {
            this.state.setState('finished');
            setTimeout(() => this.ui.showRestartButton(), 1000);
            this.setupRestartButton();
        }
    }

    setupRetryInput() {
        const retryInput = document.getElementById('nickname-input-retry');
        if (!retryInput) return;

        retryInput.addEventListener('keydown', (e) => {
            if (e.key !== 'Enter') return;

            e.preventDefault();
            const newNickname = retryInput.value.trim();

            if (newNickname.length === 0) return;

            retryInput.remove();
            this.ui.appendMessage(newNickname);
            this.ui.appendMessage('Retrying...');

            const mode = this.state.getMode();
            const message = JSON.stringify({nickname: newNickname, mode});
            this.client.send(message);
            this.state.setNickname(newNickname);
        });
    }

    setupRestartButton() {
        const restartBtn = document.getElementById('restart-btn');
        if (!restartBtn || restartBtn.hasAttribute('data-listener')) return;

        restartBtn.setAttribute('data-listener', 'true');
        restartBtn.addEventListener('click', () => this.handleRestart());
    }

    handleRestart() {
        console.log('Restarting game...');

        if (this.client) {
            this.client.close();
            this.client = null;
        }

        this.ui.hideRestartButton();
        this.state.reset();

        this.ui.renderRestartPrompt();

        const nickname = this.state.getNickname();
        if (nickname) {
            this.showModeSelection();
        } else {
            this.ui.renderNicknameInput();
            this.setupNicknameInput();
        }
    }

    handleClose() {
        if (!this.state.isPlaying()) {
            this.ui.appendMessage('Connection closed');
        }
        this.client = null;
    }

    handleError(error) {
        this.ui.appendMessage('Connection failed');
    }
}
