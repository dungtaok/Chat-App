"use strict";

import { getUsername } from "./auth.js";

var avatarStorage = new Map();

var chatBox = document.querySelector(".chatbox");

var changePasswordBtn = document.querySelector(".changePass-btn");
var changePasswordContainer = document.querySelector(".changePassContainer");
var cancelPasswordBtn = document.getElementById("cancelChangePassword");
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
var infoReturnBtn = document.querySelector(".returnBtn");

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
  chatBox.classList.remove("hidden");
  infoContent.classList.add("hidden");
});


export async function getAvatar(username){
  var response = "";
  if(avatarStorage.has(username)){
    response = avatarStorage.get(username);
    return response;
  }
  try{
    response = await fetch("http://localhost:2405/users/avatar/" + username, {
      method : "GET",
      headers : {"Content-Type" : "application/json"}
    });
    var imgUrl = await response.text();
    avatarStorage.set(username, imgUrl);
  }catch(error){
    console.log(error);
  }
  return imgUrl;
}
