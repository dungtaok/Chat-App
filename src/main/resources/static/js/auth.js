var accessToken = localStorage.getItem("access_token");
var username = localStorage.getItem("access_user");
var userId = localStorage.getItem("access_user_id");

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

export function setUserId(id){
    userId = id;
    localStorage.setItem("access_user_id", userId);
}

export function getUserId(){
    return userId;
}