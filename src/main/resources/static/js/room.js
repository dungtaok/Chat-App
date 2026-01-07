"use strict";

import { getUsername, getUserId } from "./auth.js";
import { checkExistPrivateRoom, getAllUser, connect, showChatRoomFromSearch,
   setLastChosenItemConversation, getLastChosenItemConversation,
   updateActiveRoom, getActiveRoom, getChatRoomMember, hideUserDetailInfo
} from "./roomChat.js";

var avatarStorage = new Map();

var chatBox = document.querySelector(".chatbox");

var changePasswordBtn = document.querySelector(".changePass-btn");
var changePasswordContainer = document.querySelector(".changePassContainer");
var cancelPasswordBtn = document.getElementById("cancelChangePassword");
var confirmChangePassword = document.getElementById("passwordChangeBtn");
var changePasswordModal = document.querySelector(".changePasswordModel");
var fileInput = document.getElementById("avatar-input");


var editProfileBtn = document.querySelector(".edit-profile");
var profileInputField = document.querySelectorAll(".profileField");
var avatarImg = document.getElementById("avatar-img");

var updateProfileBtns = document.querySelector(".confirmUpdateProfile");
var cancelUpdateProfile = document.getElementById("cancelUpdateProfile");
var confirmUpdateProfileBtn = document.getElementById("confirmUpdateProfile");

var myInfoBtn = document.getElementById("my-info-btn");
var infoContent = document.querySelector(".information");
var infoReturnBtn = document.querySelector(".information .returnBtn");
var userDetailReturnBtn = document.querySelector(".user-detail-info .returnBtn");

var userSearchElements = null;

var memberReturnBtn = document.getElementById("member-return-btn");
var memberDetailContainer = document.querySelector(".chatMemberContainer");
var actionMember = document.querySelector(".chatbox .action");


confirmChangePassword.addEventListener('click', async function(event){
  var pw1 = document.getElementById("new-password").value;
  var pw2 = document.getElementById("re-new-password").value;
  var pw = document.getElementById("old-password").value;

  if(pw1.trim()==="" || pw2.trim()==="" || pw.trim()===""){
    alert("Mật khẩu không được để trống!");
    return;
  }

  if(pw1.trim() !== pw2.trim()){
    alert("Mật khẩu không trùng khớp");
    document.getElementById("new-password").value = "";
    // document.getElementById("re-new-password").value = "";
    return; 
  }
  if(pw1.trim() === pw.trim()){
    alert("Hãy sử dụng mật khẩu khác mật khẩu hiện tại!");
    return;
  }
  
  var fetchCheck = await fetch("http://localhost:2405/users/auth", {
    method : "POST",
    headers : {"Content-Type":"application/json"},
    body : pw.trim()
  })
  
  if(!fetchCheck.ok){
    alert("Mật khẩu không đúng!");
    return;
  }
  var isAuthenticated = await fetchCheck.json();
  if(!isAuthenticated){
    alert("Mật khẩu không đúng!");
    return;
  }

  var fetchChange = await fetch("http://localhost:2405/users/update-pw", {
    method : "POST",
    headers : {"Content-Type":"application/json"},
    body : pw1.trim()
  })
  if(fetchChange.ok){
    alert("Thay đổi mật khẩu thành công");
    document.getElementById("new-password").value = "";
    document.getElementById("re-new-password").value = "";
    document.getElementById("old-password").value = "";
    await changePasswordContainer.classList.add("hidden");
  }
});

memberReturnBtn.addEventListener("click", function(event){
  memberDetailContainer.classList.add("hidden");
  addMemberBtn.classList.remove("hidden");
  memberListArea.classList.remove("hidden");
  cancelAddMemberBtn.classList.add("hidden");
  memberSearchArea.classList.add("hidden");
});

actionMember.addEventListener('click', function(event){ // hiển thị danh sách thành viên
  memberDetailContainer.classList.remove("hidden");
  showAllMember();
});


changePasswordBtn.addEventListener("click", function (event) {
  changePasswordContainer.classList.remove("hidden");
});

cancelPasswordBtn.addEventListener("click", function (event) {
  changePasswordContainer.classList.add("hidden");
});

changePasswordContainer.addEventListener("click", function () {
  changePasswordContainer.classList.add("hidden");
});
changePasswordModal.addEventListener("click", function (event) {
  event.stopPropagation();
});

editProfileBtn.addEventListener("click", function (event) {
  updateProfileBtns.classList.remove("visible");
  for (const field of profileInputField) {
    field.disabled = false;
  }
  fileInput.classList.remove("visible");
});

cancelUpdateProfile.addEventListener("click", function (event) {
  for (const field of profileInputField) {
    field.disabled = true;
  }
  updateProfileBtns.classList.add("visible");
  fileInput.classList.add("visible");
});

confirmUpdateProfileBtn.addEventListener("click", async function (event) { // khi đã ấn
  var updated = false;
  var updateAvt = false;
  var firstName = document.getElementById("firstname").value.trim();
  var lastName = document.getElementById("lastname").value.trim();
  const formData = new FormData();
  if (fileInput.files.length > 0) {
    const file = fileInput.files[0];
    formData.append("avatar", file);
    avatarImg.src = URL.createObjectURL(file);
    updated = true;
    updateAvt = true;
    // cập nhật avt vào avatarStorage
  }
  
  if (firstName != "") {
    formData.append("firstName", firstName);
    updated = true;
  }
  if (lastName != "") {
    formData.append("lastName", lastName);
    updated = true;
  }
  
  if (updated) {
    var isConfirm = confirm("Bạn có muốn thay đổi thông tin hay không?");
    if (isConfirm) {
      try {
        var username = getUsername();
        
        const response = await fetch(
          "http://localhost:2405/users/" + username,
          {
            method: "POST",
            body: formData,
          }
        );
        
        fileInput.value = "";
      } catch (error) {
        console.log(error);
      }
    }
  }

  if(updateAvt){
    try{
      var response = await fetch("http://localhost:2405/users/avatar/" + username, {
        method : "GET",
        headers : {"Content-Type" : "application/json"}
      });
      var imgUrl = await response.text();
      avatarStorage.set(username, imgUrl);
    }catch(error){
      console.log(error);
    }
  }

  if (updated) {
    for (const field of profileInputField) {
      field.disabled = true;
    }
    updateProfileBtns.classList.add("visible");
    fileInput.classList.add("visible"); 
  } else {
    alert("Bạn chưa thay đổi thông tin");
  }
});

myInfoBtn.addEventListener("click", function (event) {
  chatBox.classList.add("hidden");
  infoContent.classList.remove("hidden");
});

infoReturnBtn.addEventListener("click", function (event) {
  // chatBox.classList.remove("hidden");
  // infoContent.classList.add("hidden");
  hiddenInfoShowChat();

  // hideUserDetailInfo();
});
userDetailReturnBtn.addEventListener('click', function (event) { 
  hideUserDetailInfo();
});


export function hiddenInfoShowChat(){
  chatBox.classList.remove("hidden");
  infoContent.classList.add("hidden");
}


export function getAvatar(username){
  var response = "";
  if(avatarStorage.has(username)){
    response = avatarStorage.get(username);
    return response;
  }
    var fetchPromise = fetch("http://localhost:2405/users/avatar/" + username, {
      method : "GET",
      headers : {"Content-Type" : "application/json"}
    })
    .then(response=>{
      if (!response.ok) throw new Error("Lỗi tải avatar");
      return response.text();
    })
    .catch(error => {
      console.log(error);
      // Nếu lỗi thì xóa khỏi kho để lần sau thử lại
      avatarStorage.delete(username); 
      return ""; // Trả về chuỗi rỗng hoặc ảnh mặc định
    });

    avatarStorage.set(username, fetchPromise)

  return fetchPromise;
}

const inputSearch = document.querySelector(".search-bar input");
const searchReturnBtn = document.querySelector(".search-return");
const conversationArea = document.querySelector(".conversation-area");
const searchArea = document.querySelector(".search-area");
var userList = null;

inputSearch.addEventListener("click", async function(event){
    searchReturnBtn.classList.remove("hidden");
    conversationArea.classList.add("hidden");
    searchArea.classList.remove("hidden");

    userList = await getAllUser();

    var currentUserId = getUserId();
    userList = userList.filter(user => {
      return user.id != currentUserId
    });

    await showListUser(userList);
    
});

inputSearch.addEventListener("input", async function(event){
    var keyword = inputSearch.value.trim().toLowerCase();
    var currentUserId = getUserId();
    const filteredUserList = userList.filter(user => {
        const fullName = (user.firstName + " " + user.lastName).toLowerCase();
        return fullName.includes(keyword) && user.id!=currentUserId;
    });
    
    await showListUser(filteredUserList);

});

function showListUser(userList){
// if(userList != null && userList.length > 0){
        var conversationsArea = searchArea.querySelector(".conversations");
        conversationsArea.innerHTML = "";
        for(var user of userList){
            var userElement = document.createElement("li");
            userElement.classList.add("conversation-item-search");
            userElement.id = user.id;
            var imgAvt = document.createElement("img");
            imgAvt.classList.add("conversation-avt-search");
            var imgUrl = user.avatar;
            if (imgUrl==null || imgUrl.trim() == ""){
                imgAvt.src = window.appConfig.defaultAvatar;
            }else{
              imgAvt.src = "data:image/png;base64," + imgUrl;
            }
            userElement.appendChild(imgAvt);
            var userText = document.createElement("div");
            userText.classList.add("conversation-text-search");
            var h4 = document.createElement("h4");
            h4.classList.add("conversation-user-search");
            h4.innerText = user.firstName + " " + user.lastName;
            userText.appendChild(h4);
            userElement.appendChild(userText);
            conversationsArea.appendChild(userElement);
        }
    // }

    userSearchElements = document.querySelectorAll(".conversation-item-search");
    for(var userElement of userSearchElements){
      userElement.addEventListener("click", async function(event){
        hiddenSearchArea(); // ẩn bỏ phần tìm kiếm
        
        // console.log(this.id);
        // kiếm tra xem nếu đã có thì 
        var fectchCheck = await checkExistPrivateRoom(this.id);
        var exist = fectchCheck.exist;
        var roomId = null;
        if(!exist){ // nếu chưa có thì tạo conversation mới (room chat mới)      
          var fetchCreate = await fetch("http://localhost:2405/chat-room/private", {
            method : "POST",
            headers : {"Content-Type" : "application/json"},
            body : this.id
          });
          var data = await fetchCreate.json();
          roomId = data.roomId;

          // tạo item mới bên conversationList
          await createConversationItem(roomId); // sau khi thêm mới thì cập nhật lại
          // reloadConversationList();
        }else{
          roomId = fectchCheck.roomId;
        }

        await updateActiveRoom(roomId);

        await connect(roomId, event);
        await showChatRoomFromSearch(roomId, this);
      });   
    }
}

async function createConversationItem(roomId){
  var fetchGet = await fetch("http://localhost:2405/chat-room/" + roomId, {
    method : "GET"
  });

  var response = await fetchGet.json();
  var roomName =response.name;
  var imgUrl = response.avatar;

  var conversationList = conversationArea.querySelector(".conversations");

  var conversationItem = document.createElement("li");
  conversationItem.classList.add("conversation-item");
  conversationItem.id = roomId;
  var img = document.createElement("img");
  img.classList.add("conversation-avt");
  if (imgUrl==null || imgUrl.trim() == ""){
      img.src = window.appConfig.defaultAvatar;
  }else{
    img.src = "data:image/png;base64," + imgUrl;
  }

  var div = document.createElement("div");
  div.classList.add("conversation-text");
  var h3 = document.createElement("h3");
  h3.classList.add("conversation-user");
  h3.innerText = roomName;
  // var p = document.createElement("p");
  // p.classList.add("conversation-chat");
  div.appendChild(h3);
  // div.appendChild(p);

  var status = document.createElement("div");
  status.id = "dlt-cv-" + roomId;
  status.classList.add("conversation-status");

  status.innerHTML = `<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-trash3" viewBox="0 0 16 16">
                                <path d="M6.5 1h3a.5.5 0 0 1 .5.5v1H6v-1a.5.5 0 0 1 .5-.5M11 2.5v-1A1.5 1.5 0 0 0 9.5 0h-3A1.5 1.5 0 0 0 5 1.5v1H1.5a.5.5 0 0 0 0 1h.538l.853 10.66A2 2 0 0 0 4.885 16h6.23a2 2 0 0 0 1.994-1.84l.853-10.66h.538a.5.5 0 0 0 0-1zm1.958 1-.846 10.58a1 1 0 0 1-.997.92h-6.23a1 1 0 0 1-.997-.92L3.042 3.5zm-7.487 1a.5.5 0 0 1 .528.47l.5 8.5a.5.5 0 0 1-.998.06L5 5.03a.5.5 0 0 1 .47-.53Zm5.058 0a.5.5 0 0 1 .47.53l-.5 8.5a.5.5 0 1 1-.998-.06l.5-8.5a.5.5 0 0 1 .528-.47M8 4.5a.5.5 0 0 1 .5.5v8.5a.5.5 0 0 1-1 0V5a.5.5 0 0 1 .5-.5"/>
                                </svg>`;

  conversationItem.appendChild(img);
  conversationItem.appendChild(div);
  conversationItem.appendChild(status);
  conversationList.appendChild(conversationItem);

  
  if(getLastChosenItemConversation() != null){
    var item = getLastChosenItemConversation();
    item.classList.remove("item-chosen");
  }
  await setLastChosenItemConversation(conversationItem);
  conversationItem.classList.add("item-chosen");

  var conversationListHead = document.querySelector(".conversation-area .conversations");

  conversationListHead.prepend(conversationItem);

  // await reloadConversationList();
}

searchReturnBtn.addEventListener("click", function(event){
    hiddenSearchArea();
});

function hiddenSearchArea(){
  searchReturnBtn.classList.add("hidden");
    conversationArea.classList.remove("hidden");
    searchArea.classList.add("hidden");
    inputSearch.value = "";
}

// Danh sách người dùng trong đoạn chat

function createRoomMemberElement(user){
  var div = document.createElement("div");
  div.classList.add("member");

  var info = document.createElement("div");
  info.classList.add("member-info");

  var img = document.createElement("img");
  img.classList.add("member-avt");
  var imgUrl = user.avatar;
  if (imgUrl==null || imgUrl.trim() == ""){
      img.src = window.appConfig.defaultAvatar;
  }else{
    img.src = "data:image/png;base64," + imgUrl;
  }
  var h5 = document.createElement("h5");
  h5.classList.add("member-name");
  h5.innerText = user.firstName + " " + user.lastName;
  info.appendChild(img)
  info.appendChild(h5);

  div.appendChild(info);

  if(user.username != getUsername()){
    var btn = document.createElement("button");
    btn.textContent = "Xóa";
    btn.classList.add("delete-btn");
    btn.id = "btn-delete-" + user.id;

    div.appendChild(btn);
  }

  return div; // thành phần user bên trong list
}

async function showAllMember(){
  var roomId = getActiveRoom();
  var members = await getChatRoomMember(roomId);
  var memberListArea = document.querySelector(".chatMemberModal .member-area");
  memberListArea.innerHTML = "";
  for(var user of members){
    var memberElement = createRoomMemberElement(user);
    memberListArea.appendChild(memberElement);
  }
  var deleteMemberBtns = document.querySelectorAll(".chatMemberContainer .member-area .delete-btn");
  for(var deleteMemberBtn of deleteMemberBtns){
    deleteMemberBtn.addEventListener('click', async function(event){
      var roomId = getActiveRoom();
      var memberId = this.id.slice(11);
      var isConfirm = confirm("Bạn có chắc muốn xóa người này ra khỏi nhóm?");
      if(isConfirm === true){
        await deleteMemberFromRoom(roomId, memberId);
        // hiển thị danh sách người mới được thay đổi 
        showAllMember();
      }
    });
  }
}

async function deleteMemberFromRoom(roomId, memberId) {
  var fetchDelete = await fetch("http://localhost:2405/chat-room/" + roomId, {
    method : "DELETE",
    headers : {"Content-Type" : "application/json"},
    body : memberId
  });

  if(fetchDelete.ok){
    confirm("Đã xóa người dùng khỏi nhóm thành công!");
  }else{
    var message = await fetchDelete.json().message;
    alert(message);
  }
}

// tìm kiếm và thêm user vào đoạn chat

var addMemberBtn = document.querySelector(".add-member-btn");
var cancelAddMemberBtn = document.querySelector(".cancel-add-member-btn");
var memberListArea = document.querySelector(".chatMemberModal .member-area");
var memberSearchArea = document.querySelector(".chatMemberModal .member-search-area");

addMemberBtn.addEventListener('click', function(event){
  addMemberBtn.classList.add("hidden");
  memberListArea.classList.add("hidden");
  cancelAddMemberBtn.classList.remove("hidden");
  memberSearchArea.classList.remove("hidden");
  showAllMemberSearch();
});

cancelAddMemberBtn.addEventListener('click', function (event){
  addMemberBtn.classList.remove("hidden");
  memberListArea.classList.remove("hidden");
  cancelAddMemberBtn.classList.add("hidden");
  memberSearchArea.classList.add("hidden");
})

function createMemberSearchElement(user) {
  var div = document.createElement("div");
  div.classList.add("member");

  var info = document.createElement("div");
  info.classList.add("member-info");

  var img = document.createElement("img");
  img.classList.add("member-avt");
  var imgUrl = user.avatar;
  if (imgUrl == null || imgUrl.trim() == "") {
    img.src = window.appConfig.defaultAvatar;
  } else {
    img.src = "data:image/png;base64," + imgUrl;
  }
  var h5 = document.createElement("h5");
  h5.classList.add("member-name");
  h5.innerText = user.firstName + " " + user.lastName;
  info.appendChild(img);
  info.appendChild(h5);

  div.appendChild(info);

  var btn = document.createElement("button");
  btn.textContent = "Thêm";
  btn.classList.add("add-btn");
  btn.id = "btn-add-" + user.id;

  div.appendChild(btn);
  return div; // thành phần user bên trong list
}

var memberSearchBar = document.querySelector(".member-search-area #search-bar-member");

async function showAllMemberSearch(){
    userList = await getAllUser();

    var currentUserId = getUserId();
    userList = userList.filter(user => {
      return user.id != currentUserId
    });
    await showMemberSearch(userList);
}

memberSearchBar.addEventListener('input', async function(event){
  var keyword = memberSearchBar.value.trim().toLowerCase();

  var currentUserId = getUserId();

  var userList = await getAllUser();
  userList = userList.filter(user => {
        const fullName = (user.firstName + " " + user.lastName).toLowerCase();
        return fullName.includes(keyword) && user.id!=currentUserId;
    });

   await showMemberSearch(userList); 
});

async function showMemberSearch(userList){ // hiển thị kết quả danh sách người dùng để thêm vào đoạn chat
  var searchMemberResult = document.querySelector(".member-search-area .search-member-result");
  searchMemberResult.innerHTML = "";
  for(var user of userList){
    var userSearchElement = await createMemberSearchElement(user);
    searchMemberResult.appendChild(userSearchElement);
  }
  var addMemberBtns = document.querySelectorAll(".search-member-result .member .add-btn");
  for(var addMemberBtn of addMemberBtns){
    addMemberBtn.addEventListener("click",  async function(event){
      var memberId = this.id.slice(8);
      var roomId = getActiveRoom();
      var check = await addMemberIntoRoom(memberId, roomId);
      if(check === "true"){
        this.classList.add("disabled");
      }
      showAllMember();
    });
  }
}

async function addMemberIntoRoom(userId, roomId){
  var fetchPost = await fetch("http://localhost:2405/chat-room/" + roomId, {
    method : "POST",
    headers : {"Content-Type" : "application/json"},
    body : userId
  });
  if(fetchPost.ok){
    confirm("Đã thêm thành công!");
    return "true";
  }else{
    var msg = await fetchPost.json().message;
    alert(msg);
    return "false";
  }
}

