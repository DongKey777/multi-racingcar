class NicknameValidator {
    static MIN_LENGTH = 2;
    static MAX_LENGTH = 6;

    static validate(nickname) {
        if (!nickname || nickname.trim().length === 0) {
            return {
                valid: false,
                error: 'Nickname cannot be empty'
            };
        }

        const length = nickname.length;
        if (length < this.MIN_LENGTH || length > this.MAX_LENGTH) {
            return {
                valid: false,
                error: `Nickname must be between ${this.MIN_LENGTH} and ${this.MAX_LENGTH} characters`
            };
        }

        return {valid: true};
    }
}
