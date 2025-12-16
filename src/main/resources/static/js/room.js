'use strict'

var changePasswordBtn = document.querySelector(".changePass-btn");
var changePasswordContainer = document.querySelector(".changePassContainer");
var cancelPasswordBtn = document.getElementById("cancelChangePassword"); 
var changePasswordModal = document.querySelector(".changePasswordModel");

var editProfileBtn = document.querySelector(".edit-profile");
var profileInputField = document.querySelectorAll('.profileField');

var updateProfileBtns = document.querySelector(".confirmUpdateProfile");
var cancelUpdateProfile =document.getElementById("cancelUpdateProfile");


changePasswordBtn.addEventListener('click', function(event){
    changePasswordContainer.classList.remove("hidden");
});

cancelPasswordBtn.addEventListener('click', function(event){
    changePasswordContainer.classList.add("hidden");
});

changePasswordContainer.addEventListener('click', function(){
    changePasswordContainer.classList.add("hidden");
});
changePasswordModal.addEventListener('click', function(event){
    event.stopPropagation();
});

editProfileBtn.addEventListener('click', function(event){
    updateProfileBtns.classList.remove("visible");
    for(const field of profileInputField){
        field.disabled = false;
    }
});

cancelUpdateProfile.addEventListener('click', function(event){
    for(const field of profileInputField){
        field.disabled = true;
    }
    updateProfileBtns.classList.add("visible");
});
