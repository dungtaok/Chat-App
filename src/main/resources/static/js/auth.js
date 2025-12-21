var accessToken = localStorage.getItem("access_token");
var username = localStorage.getItem("access_user");

export function  setToken(token) {
    accessToken = token;
    localStorage.setItem("access_token", accessToken);
}

export function getToken(){
    return accessToken;
}

export function  setUsername(name) {
    username = name;
    localStorage.setItem("access_user", username);
}

export function getUsername(){
    return username;
}