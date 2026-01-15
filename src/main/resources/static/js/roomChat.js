"use strict";

import { getToken, getUserId, getUsername } from "./auth.js";
import { getAvatar, hiddenInfoShowChat, enableCanBack } from "./room.js";
import { Security } from "./encryption.js";

var messageInput = document.querySelector(".chat-field");
var messageArea = document.querySelector(".conversation");
var fileMessageInput = document.getElementById("inputFileMessage");

var sendMsgBtn = document.querySelector(".send-btn");

var stompClient = null;
var username = null;
var jwtToken = null;
var password = null;

var isActiveRoomId = null;

// các kênh đã sub
var connectedStorage = new Map();

var lastChosenItemConversation = null;

var conversationList = document.querySelectorAll(".conversation-item");
var conversationListHead = document.querySelector(
  ".conversation-area .conversations"
);
var conversationDltBtns = document.querySelectorAll(
  ".conversation-area .conversation-status"
);

// export function reloadConversationList(){
//   conversationList = document.querySelectorAll(".conversation-item");
//   conversationDltBtns = document.querySelectorAll(".conversation-area .conversation-status");
//   // return conversationList;

//   for(var room of conversationList){
//     room.addEventListener("click",
//       async function(event){
//       isActiveRoomId = this.id;
//       await connect(this.id, event);
//       await showChatRoom(isActiveRoomId, this);
//       if(lastChosenItemConversation != null){
//         lastChosenItemConversation.classList.remove("item-chosen");
//       }
//       lastChosenItemConversation = this;
//       this.classList.add("item-chosen");
//     }, true);
//     // bổ sung hàm hiển thị khung chat của room tương ứng
//   }

//   for(var btn of conversationDltBtns){
//   btn.addEventListener('click', async function(event){
//     event.stopPropagation();
//     isDltRoom = this.id.slice(7);
//     var isConfirm = confirm("Bạn có chắc muốn xóa?");
//     if(isConfirm === true){
//       var fetchDlt = await deleteRoom(isDltRoom);
//       if(fetchDlt.ok){

//         confirm("Đã xóa phòng chat thành công!");

//         // ẩn đi element
//         await deleteConversationElement(event, isDltRoom);
//       }
//     }
//   });
// }

// }

export function updateActiveRoom(id) {
  isActiveRoomId = id;
}

export function getActiveRoom() {
  return isActiveRoomId;
}

export function setLastChosenItemConversation(item) {
  lastChosenItemConversation = item;
}
export function getLastChosenItemConversation() {
  return lastChosenItemConversation;
}

function createMessageElement(content) {
  // tạo thẻ hiển thị tin nhắn của bản thân
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

function createFileMessageElement(content) {
  // tạo thẻ hiển thị tin nhắn của bản thân
  var msgElement = document.createElement("li");

  var msgAvt = document.createElement("img");
  msgAvt.classList.add("msgAvt");
  msgAvt.alt = "Avatar";
  // msgAvt.src = avatarUrl;
  var msgBody = document.createElement("div");
  msgBody.classList.add("msgBody");
  msgBody.classList.add("fileMessage");

  var iconFile = document.createElement("div");
  iconFile.classList.add("iconFile");
  iconFile.innerHTML = `<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-file-earmark-text" viewBox="0 0 16 16">
                                        <path d="M5.5 7a.5.5 0 0 0 0 1h5a.5.5 0 0 0 0-1zM5 9.5a.5.5 0 0 1 .5-.5h5a.5.5 0 0 1 0 1h-5a.5.5 0 0 1-.5-.5m0 2a.5.5 0 0 1 .5-.5h2a.5.5 0 0 1 0 1h-2a.5.5 0 0 1-.5-.5"/>
                                        <path d="M9.5 0H4a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2V4.5zm0 1v2A1.5 1.5 0 0 0 11 4.5h2V14a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1z"/>
                                    </svg>`;
  var a = document.createElement("a");
  a.classList.add("fileName");
  // p.innerText = content.name;
  var fileType = content.split(",")[0];
  var fileBase64Content = content.split(",")[1];
  var fileName = content.split(",")[2];
  a.href = fileType + "," + fileBase64Content;
  a.download = fileName;
  a.innerHTML = fileName;

  msgBody.appendChild(iconFile);
  msgBody.appendChild(a);

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

export function connect(roomId, event) {
  if (stompClient && stompClient.connected) {
    onConnected(roomId);
    return;
  }
  var socket = new SockJS("/ws");
  stompClient = Stomp.over(socket);

  username = getUsername();
  jwtToken = getToken();

  // (header, callback khi kết nối thành công, callback khi kết nối bị ngắt)
  stompClient.connect(
    {},
    function () {
      onConnected(roomId);
    },
    onError
  ); // không cần đăng nhập -> header rỗng

  if (event) {
    event.preventDefault();
  }
}

function onConnected(roomId) {
  isActiveRoomId = roomId;
  // client chỉ sub mỗi channel 1 lần
  if (!connectedStorage.has(roomId)) {
    // chưa từng kết nối trước đó
    stompClient.subscribe("/queue/room/" + roomId, onMessageReceived);
    connectedStorage.set(roomId, true);
  } else {
    // đã kết nối trước đó -> không sub lại
  }
  // sub để nhận tin nhắn
  // đăng kí kênh và mỗi khi kênh có thay đổi -> thực hiện hàm
}

function onError(error) {
  console.log(
    "Could not connect to WebSocket! Please refresh the page and try again or contact your administrator."
  );
}

function send(event) {
  var messageContent = messageInput.value.trim();

  if (messageContent && stompClient) {
    var chatMessage = {
      sender: username,
      content: messageInput.value,
      type: "TEXT",
      //   type: "CHAT",
    };

    stompClient.send("/app/chat.send", {}, JSON.stringify(chatMessage));
    messageInput.value = "";
  }
  if (event) {
    event.preventDefault();
  }
}

// gửi tin nhắn cho tin nhắn thông thường
function sendMessage(event) {
  var messageContent = messageInput.value.trim();
  var encryptContent = Security.encrypt(messageContent); // mã hóa tin nhắn khi gửi

  if (messageContent && stompClient) {
    var chatMessage = {
      sender: username,
      content: encryptContent,
      recipient: isActiveRoomId,
      type: "TEXT",
    };

    stompClient.send(
      "/app/chat.send/" + isActiveRoomId,
      {},
      JSON.stringify(chatMessage)
    );
    messageInput.value = "";
  }

  if (event) {
    event.preventDefault();
  }
}

// gửi tin nhắn cho file
function sendFileMessage(event) {
  var file = fileMessageInput.files[0];
  // var encryptContent = Security.encrypt(messageContent); // mã hóa tin nhắn khi gửi
  var base64Content = null;
  if (file) {
    var fileName = file.name;
    var fileType = file.type;
    var reader = new FileReader();

    reader.onload = function (e) {
      var full64BaseContent = e.target.result;
      // base64Content = full64BaseContent.split(',')[1]; // lấy phần mã , bỏ qua phần định dạng
      base64Content = full64BaseContent + "," + fileName; // đã có file type , file_type,base64,file_name

      if (base64Content && stompClient) {
        var chatMessage = {
          sender: username,
          // content : file,
          recipient: isActiveRoomId,
          content: base64Content,
          type: "FILE",
        };
        stompClient.send(
          "/app/chat.sendFile/" + isActiveRoomId,
          {},
          JSON.stringify(chatMessage)
        );
        // messageInput.value = "";
      }
    };
    reader.readAsDataURL(file);
  }

  if (event) {
    event.preventDefault();
  }
}

/**
 * Handles the received message and updates the chat interface accordingly.
 * param {Object} payload - The payload containing the message data.
 */
async function onMessageReceived(payload) {
  var message = JSON.parse(payload.body);

  // hiển thị tin nhắn
  // chỉ hiển thị nếu như đang mở đoạn chat đó

  if (message.type === "TEXT") {
    var messageContent = Security.decrypt(message.content);

    var messageElement = createMessageElement(messageContent); // set tạm avt là null
    var img = messageElement.querySelector("img");
    var imgUrl = await getAvatar(message.sender);
    if (imgUrl == null || imgUrl.trim() == "") {
      img.src = window.appConfig.defaultAvatar;
    } else {
      img.src = "data:image/png;base64," + imgUrl;
    }

    if (message.sender === username) {
      messageElement.classList.add("myMessage");
    } else {
      messageElement.classList.add("yourMessage");
    }

    if (
      isActiveRoomId == message.recipient ||
      message.sender == getUsername()
    ) {
      messageArea.appendChild(messageElement);
      messageArea.scrollTop = messageArea.scrollHeight;
    }
  } else {
    var msgFileElement = createFileMessageElement(message.content);
    var img = msgFileElement.querySelector("img");
    var imgUrl = await getAvatar(message.sender);
    if (imgUrl == null || imgUrl.trim() == "") {
      img.src = window.appConfig.defaultAvatar;
    } else {
      img.src = "data:image/png;base64," + imgUrl;
    }

    if (message.sender === username) {
      msgFileElement.classList.add("myMessage");
    } else {
      msgFileElement.classList.add("yourMessage");
    }

    if (
      isActiveRoomId == message.recipient ||
      message.sender == getUsername()
    ) {
      messageArea.appendChild(msgFileElement);
      messageArea.scrollTop = messageArea.scrollHeight;
    }
  }

  // thay đổi thứ tự của các đoạn chat
  // var conversationList = document.querySelectorAll(".conversation-area .conversations");
  var lastUpdatedConversation = document.getElementById(message.recipient);
  conversationListHead.prepend(lastUpdatedConversation);
  // reloadConversationList();
}

async function showOldMessage(message) {
  // if (message.type === "TEXT") {
  //   var messageContent = Security.decrypt(message.content);

  //   var messageElement = createMessageElement(messageContent); // set tạm avt là null
  //   var img = messageElement.querySelector("img");
  //   var imgUrl = await getAvatar(message.sender);
  //   if (imgUrl == null || imgUrl.trim() == "") {
  //     img.src = window.appConfig.defaultAvatar;
  //   } else {
  //     img.src = "data:image/png;base64," + imgUrl;
  //   }

  //   if (message.sender === username) {
  //     messageElement.classList.add("myMessage");
  //   } else {
  //     messageElement.classList.add("yourMessage");
  //   }

  //   // if(isActiveRoomId==message.recipient || message.sender==getUsername()){
  //   //   messageArea.appendChild(messageElement);
  //   //   messageArea.scrollTop = messageArea.scrollHeight;
  //   // }
  // } else {
  //   var msgFileElement = createFileMessageElement(message.content);
  //   var img = msgFileElement.querySelector("img");
  //   var imgUrl = await getAvatar(message.sender);
  //   if (imgUrl == null || imgUrl.trim() == "") {
  //     img.src = window.appConfig.defaultAvatar;
  //   } else {
  //     img.src = "data:image/png;base64," + imgUrl;
  //   }

  //   if (message.sender === username) {
  //     msgFileElement.classList.add("myMessage");
  //   } else {
  //     msgFileElement.classList.add("yourMessage");
  //   }

  //   // if(isActiveRoomId==message.recipient || message.sender==getUsername()){
  //   //   messageArea.appendChild(msgFileElement);
  //   //   messageArea.scrollTop = messageArea.scrollHeight;
  //   // }
  // }
  if(message.type === "TEXT"){
    var messageContent = Security.decrypt(message.content);
    var messageElement = createMessageElement(messageContent); // set tạm avt là null
    var img = messageElement.querySelector('img');
    var imgUrl = await getAvatar(message.sender);
    if (imgUrl==null || imgUrl.trim() == ""){
        img.src = window.appConfig.defaultAvatar;
    }else{
      img.src = "data:image/png;base64," + imgUrl;
    }

    if (message.sender === username) {
      messageElement.classList.add("myMessage");
    } else{
      messageElement.classList.add("yourMessage");
    }
    messageArea.appendChild(messageElement);

  }else{
    var msgFileElement = createFileMessageElement(message.content);
      var img = msgFileElement.querySelector('img');
      var imgUrl = await getAvatar(message.sender);
      if (imgUrl==null || imgUrl.trim() == ""){
          img.src = window.appConfig.defaultAvatar;
      }else{
        img.src = "data:image/png;base64," + imgUrl;
      }

      if (message.sender === username) {
        msgFileElement.classList.add("myMessage");
      } else{
        msgFileElement.classList.add("yourMessage");
      }

      messageArea.appendChild(msgFileElement);
  }
  messageArea.scrollTop = messageArea.scrollHeight;
}

async function loadRoomMessage(roomId) {
  var response = await fetch(
    "http://localhost:2405/chat-room/room-messages/" + roomId,
    {
      method: "GET",
    }
  );
  return await response.json();
}

export async function showChatRoom(roomId, conversationElement) {
  // thay đổi header của chatbox
  var roomName = document.querySelector(".chatbox .info .user-name");
  roomName.innerText = conversationElement.querySelector("h3").innerText;

  var info = document.querySelector(".chatbox .info");
  info.id = "info-" + roomId;

  var roomImg = document.querySelector(".chatbox .info .avatar img");
  roomImg.src = conversationElement.querySelector("img").src;

  // thay đổi content của chatbox
  messageArea.innerHTML = "";
  try {
    var histotyMessages = await loadRoomMessage(roomId); // tin nhắn cũ
  } catch (error) {
    console.log(error);
  }

  for (var message of histotyMessages) {
    await showOldMessage(message);
  }
}

export async function showChatRoomFromSearch(roomId, conversationElement) {
  // thay đổi header của chatbox
  var roomName = document.querySelector(".chatbox .info .user-name");
  roomName.innerText = conversationElement.querySelector("h4").innerText;
  0;

  var info = document.querySelector(".chatbox .info");
  info.id = "info-" + roomId;

  var roomImg = document.querySelector(".chatbox .info .avatar img");
  roomImg.src = conversationElement.querySelector("img").src;

  // thay đổi content của chatbox
  messageArea.innerHTML = "";
  try {
    var histotyMessages = await loadRoomMessage(roomId); // tin nhắn cũ
  } catch (error) {
    console.log(error);
  }

  for (var message of histotyMessages) {
    await showOldMessage(message);
  }
}

messageInput.addEventListener("keydown", function (event) {
  if (event.key === "Enter") {
    //send();
    sendMessage();
    event.preventDefault();
  }
});

fileMessageInput.addEventListener("change", async function (event) {
  sendFileMessage();
  event.preventDefault();
});

sendMsgBtn.addEventListener("click", sendMessage, true);

var conversationContainer = document.querySelector(
  ".conversation-area .conversations"
);
conversationContainer.addEventListener("click", async function (event) {
  var delBtn = event.target.closest(".conversation-status");
  if (delBtn) {
    event.preventDefault();
    event.stopPropagation();
    var isDltRoom = delBtn.id.slice(7);
    var isConfirm = confirm("Bạn có chắc muốn xóa?");
    if (isConfirm === true) {
      var fetchDlt = await deleteRoom(isDltRoom);
      if (fetchDlt.ok) {
        confirm("Đã xóa phòng chat thành công!");

        // ẩn đi element
        await deleteConversationElement(event, isDltRoom);

        if (roomId === isDltRoom) {
          ddddddddddddddddddddddddddddđ;
        }
      }
    }

    return;
  }

  var roomChosen = event.target.closest(".conversation-item");
  if (roomChosen) {
    isActiveRoomId = roomChosen.id;
    await connect(isActiveRoomId, event);
    await showChatRoom(isActiveRoomId, roomChosen);
    if (lastChosenItemConversation != null) {
      lastChosenItemConversation.classList.remove("item-chosen");
    }
    lastChosenItemConversation = roomChosen;
    roomChosen.classList.add("item-chosen");

    enableCanBack();
  }

  hiddenInfoShowChat();
  hideUserDetailInfo();
});

for (var room of conversationList) {
  // kết nối để nhân tin nhắn toàn bộ
  var roomId = room.id;
  await connect(roomId);
}

// for(var room of conversationList){
//   room.addEventListener("click",
//     async function(event){
//     isActiveRoomId = this.id;
//     await connect(this.id, event);
//     await showChatRoom(isActiveRoomId, this);
//     if(lastChosenItemConversation != null){
//       lastChosenItemConversation.classList.remove("item-chosen");
//     }
//     lastChosenItemConversation = this;
//     this.classList.add("item-chosen");
//   }, true);
//   // bổ sung hàm hiển thị khung chat của room tương ứng
// }

// for(var btn of conversationDltBtns){
//   btn.addEventListener('click', async function(event){
//     event.stopPropagation();
//     var isDltRoom = this.id.slice(7);
//     var isConfirm = confirm("Bạn có chắc muốn xóa?");
//     if(isConfirm === true){
//       var fetchDlt = await deleteRoom(isDltRoom);
//       if(fetchDlt.ok){
//         confirm("Đã xóa phòng chat thành công!");

//         // ẩn đi element
//         await deleteConversationElement(event,isDltRoom);
//       }
//     }
//   });
// }

function deleteConversationElement(event, roomId) {
  event.stopPropagation();

  var dltElement = document.getElementById(roomId);
  dltElement.remove();
}

async function deleteRoom(id) {
  var currentUserId = getUserId();
  var fecthDelete = await fetch(
    "http://localhost:2405/users/" + currentUserId,
    {
      method: "DELETE",
      headers: { "Content-Type": "application/json" },
      body: id,
    }
  );
  return fecthDelete;
}

export async function getAllUser() {
  var response = await fetch("http://localhost:2405/users", { method: "GET" });

  var users = await response.json();
  return users;
}

export async function checkExistPrivateRoom(userId) {
  var fetchPromise = await fetch(
    "http://localhost:2405/chat-room/exist-private/" + userId,
    { method: "GET" }
  );

  var response = await fetchPromise.json();
  return response;
}

export async function getChatRoomMember(roomId) {
  var fetchGet = await fetch(
    "http://localhost:2405/chat-room/member/" + roomId,
    { method: "GET" }
  );
  var response = await fetchGet.json();
  return response;
}

// show information

var userInfoElement = document.querySelector(".chatbox .info");
userInfoElement.addEventListener("click", async function (event) {
  var roomId = userInfoElement.id.slice(5);
  var fetchGet = await fetch(
    "http://localhost:2405/chat-room/detail/" + roomId,
    {
      method: "GET",
      headers: { "Content-Type": "application/json" },
    }
  );

  if (!fetchGet.ok) {
    return;
  }

  var roomDetail = await fetchGet.json();
  await showUserInformation(roomDetail);
});

var charBoxArea = document.querySelector(".content-container .chatbox");
var userDetailInformation = document.querySelector(
  ".content-container .user-detail-info"
);

export function hideUserDetailInfo() {
  charBoxArea.classList.remove("hidden");
  userDetailInformation.classList.add("hidden");
}

async function showUserInformation(userDetail) {
  // vì chỉ hoạt động nếu room có 2 người
  charBoxArea.classList.add("hidden");
  userDetailInformation.classList.remove("hidden");
  var userDetailAvt = document.querySelector(
    ".content-container .user-detail-info img"
  );
  var userDetailName = document.querySelector(
    ".content-container .user-detail-info .user-detail-name"
  );

  if (userDetail.avatar == null || userDetail.avatar.trim() == "") {
    userDetailAvt.src = window.appConfig.defaultAvatar;
  } else {
    // userDetailAvt.src = "data:image/png;base64," + imgUrl;
    userDetailAvt.src = "data:image/png;base64," + userDetail.avatar;
  }
  userDetailName.innerText = userDetail.name;
}
