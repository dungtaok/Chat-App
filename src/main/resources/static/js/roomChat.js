'use strict'

import { getToken, getUsername } from "./auth.js";
import { getAvatar } from "./room.js";

var messageInput = document.querySelector(".chat-field");
var messageArea = document.querySelector(".conversation");

var generalChanel = document.getElementById("general-channel-js");
var sendMsgBtn = document.querySelector(".send-btn");

var stompClient = null;
var username = null;
var jwtToken = null;
var password = null;

var isSub = false;


function createMessageElement(content){ // tạo thẻ hiển thị tin nhắn của bản thân
    var msgElement = document.createElement("li");

    var msgAvt = document.createElement("img");
    msgAvt.classList.add("msgAvt");
    msgAvt.alt = "Avatar";
    // msgAvt.src = avatarUrl;
    var msgBody = document.createElement("p");
    msgBody.classList.add("msgBody");
    msgBody.textContent = content;

    msgElement.appendChild(msgAvt);
    msgElement.appendChild(msgBody);

    return msgElement;
}

// function createYourMessage(content, avatarUrl){ // tạo thẻ hiển thị tin nhắn của đối phương
//     var yourMsgElement = document.createElement("li");
//     yourMsgElement.classList.add("yourMessage");

//     var msgAvt = document.createElement("img");
//     msgAvt.classList.add("msgAvt");
//     msgAvt.src = avatarUrl;
//     var msgBody = document.createElement("p");
//     msgBody.classList.add("msgBody");
//     msgBody.textContent = content;

//     yourMsgElement.appendChild(msgAvt);
//     yourMsgElement.appendChild(msgBody);

//     return yourMsgElement;
// }

function connect(event) { 
    var socket = new SockJS("/ws");
    stompClient = Stomp.over(socket);

    username = getUsername();
    jwtToken = getToken();

      // (header, callback khi kết nối thành công, callback khi kết nối bị ngắt)
    stompClient.connect({}, onConnected, onError); // không cần đăng nhập -> header rỗng

    event.preventDefault();
}

function onConnected() {
  // sub để nhận tin nhắn
  if(!isSub){
    stompClient.subscribe("/public/messages", onMessageReceived);
    isSub = true; 
  }
  // đăng kí kênh và mỗi khi kênh có thay đổi -> thực hiện hàm
}

function onError(error) {
    connectingElement.textContent =
      "Could not connect to WebSocket! Please refresh the page and try again or contact your administrator.";
    connectingElement.style.color = "red";
}

function send(event) {
  var messageContent = messageInput.value.trim();

  if (messageContent && stompClient) {
    var chatMessage = {
      sender: username,
      content: messageInput.value,
    //   type: "CHAT",
    };

    stompClient.send("/app/chat.send", {}, JSON.stringify(chatMessage));
    messageInput.value = "";
  }
  if(event){
    event.preventDefault();
  }
}

/**
 * Handles the received message and updates the chat interface accordingly.
 * param {Object} payload - The payload containing the message data.
 */
async function onMessageReceived(payload) {
  var message = JSON.parse(payload.body);

  var messageElement = createMessageElement(message.content); // set tạm avt là null
  var img = messageElement.querySelector('img');
  var imgUrl = await getAvatar(message.sender);
  if (imgUrl==null || imgUrl.trim() == ""){
      img.src = window.appConfig.defaultAvatar;
  }else{
    img.src = "data:image/png;base64," + imgUrl;
  }

  if (message.sender === username) {
    // Add a class to float the message to the right
    messageElement.classList.add("myMessage");
  } else{
    messageElement.classList.add("yourMessage");
  }
  messageArea.appendChild(messageElement);
  messageArea.scrollTop = messageArea.scrollHeight;
}

messageInput.addEventListener("keydown", function(event){
    if(event.key === "Enter"){
        send();
        event.preventDefault();
    }
});

generalChanel.addEventListener("click", connect, true);
sendMsgBtn.addEventListener("click", send, true);

