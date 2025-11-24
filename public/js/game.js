document.addEventListener('DOMContentLoaded', function () {
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

    const gameStartBtn = document.getElementById('game-start-btn');
    const homeScreen = document.getElementById('home-screen');
    const gameScreen = document.getElementById('game-screen');

    if (gameStartBtn) {
        gameStartBtn.addEventListener('click', function () {
            homeScreen.style.display = 'none';
            gameScreen.style.display = 'block';

            const app = new GameApp();
            app.start();
        });
    }
});
