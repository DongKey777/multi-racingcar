class GameState {
    constructor() {
        this.state = 'waiting';
        this.nickname = null;
        this.mode = null;
    }

    setState(newState) {
        this.state = newState;
    }

    setNickname(nickname) {
        this.nickname = nickname;
    }

    setMode(mode) {
        this.mode = mode;
    }

    getNickname() {
        return this.nickname;
    }

    getMode() {
        return this.mode;
    }

    isWaiting() {
        return this.state === 'waiting';
    }

    isPlaying() {
        return this.state === 'playing';
    }

    isFinished() {
        return this.state === 'finished';
    }

    reset() {
        this.state = 'waiting';
        this.mode = null;
    }
}
