document.addEventListener('DOMContentLoaded', function () {
    // 현재 시간 표시
    const lastLogin = document.getElementById('last-login');
    const lastLoginGame = document.getElementById('last-login-game');
    const now = new Date();
    const loginText = `Last login: ${now.toDateString()} ${now.toTimeString().split(' ')[0]} on ttys001\n`;

    if (lastLogin) {
        lastLogin.textContent = loginText;
    }
    if (lastLoginGame) {
        lastLoginGame.textContent = loginText;
    }

    // Game Start 버튼 이벤트
    const gameStartBtn = document.getElementById('game-start-btn');
    const homeScreen = document.getElementById('home-screen');
    const gameScreen = document.getElementById('game-screen');

    if (gameStartBtn) {
        gameStartBtn.addEventListener('click', function () {
            // 홈 화면 숨기기
            homeScreen.style.display = 'none';
            // 게임 화면 보이기
            gameScreen.style.display = 'block';

            // 게임 시작
            const app = new GameApp();
            app.start();
        });
    }
});
