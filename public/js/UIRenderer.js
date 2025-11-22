class UIRenderer {
    constructor(contentElement, modeSelectionElement, restartSectionElement) {
        this.content = contentElement;
        this.modeSelection = modeSelectionElement;
        this.restartSection = restartSectionElement;
    }

    renderNicknameInput() {
        this.content.innerHTML = 'Enter your nickname: <input type="text" id="nickname-input" maxlength="10" autofocus />';
        this.focusInput('nickname-input');
    }

    renderValidationError(nickname, error) {
        this.content.innerHTML = `Enter your nickname: ${nickname}\n`;
        this.content.innerHTML += `Error: ${error}\n\n`;
        this.content.innerHTML += 'Enter your nickname: <input type="text" id="nickname-input" maxlength="10" autofocus />';
        this.focusInput('nickname-input');
    }

    renderModeSelection(nickname) {
        this.content.innerHTML = `Enter your nickname: ${nickname}\n\n`;
        this.modeSelection.style.display = 'block';
    }

    hideModeSelection() {
        this.modeSelection.style.display = 'none';
    }

    renderConnecting(nickname, mode) {
        const modeText = mode === 'SINGLE' ? 'Single Player' : 'Multiplayer';
        this.content.innerHTML = `Enter your nickname: ${nickname}\n\n`;
        this.content.innerHTML += `Selected mode: ${modeText}\n\n`;
        this.content.innerHTML += 'Connecting to server...\n';
    }

    renderConnected(nickname) {
        this.content.innerHTML += 'Connected to server\n\n';
        this.content.innerHTML += `Joining as: ${nickname}\n`;
    }

    renderRetryNicknameInput() {
        this.content.innerHTML += '\nEnter your nickname: <input type="text" id="nickname-input-retry" maxlength="10" autofocus />\n';
        this.focusInput('nickname-input-retry');
    }

    appendMessage(message) {
        this.content.innerHTML += message + '\n';
        window.scrollTo(0, document.body.scrollHeight);
    }

    updateWaitingMessage(message) {
        const lines = this.content.innerHTML.split('\n');
        const waitingLineIndex = lines.findIndex(line => line.includes('대기 중...'));

        if (waitingLineIndex !== -1) {
            lines[waitingLineIndex] = message;
            this.content.innerHTML = lines.join('\n');
        } else {
            this.appendMessage(message);
        }

        window.scrollTo(0, document.body.scrollHeight);
    }

    clearScreen() {
        this.content.innerHTML = '';
    }

    renderRestartPrompt() {
        const initialPrompt = `racing-game ❯ ./game start
Starting server...
Waiting for players...

`;
        this.content.innerHTML = initialPrompt;
    }

    showRestartButton() {
        this.restartSection.style.display = 'block';
    }

    hideRestartButton() {
        this.restartSection.style.display = 'none';
    }

    focusInput(inputId) {
        setTimeout(() => {
            const input = document.getElementById(inputId);
            if (input) {
                input.focus();
            }
        }, 100);
    }
}