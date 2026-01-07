'use strict'

import CryptoJS from 'https://cdn.jsdelivr.net/npm/crypto-js@4.2.0/+esm';

const SECRET_PASSPHRASE = "qFiwvlcJDKAKFc7YQN4RXAZctwLrNrvR"; 

export const Security = {
    encrypt: (text) => {
        return CryptoJS.AES.encrypt(text, SECRET_PASSPHRASE).toString();
    },
    decrypt: (encryptText) => {
        try {
            const bytes = CryptoJS.AES.decrypt(encryptText, SECRET_PASSPHRASE);
            return bytes.toString(CryptoJS.enc.Utf8);
        } catch (e) {
            return "...Có lỗi xảy ra...";
        }
    }
};
