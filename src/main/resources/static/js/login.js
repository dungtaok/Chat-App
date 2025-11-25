
const loginBtn = document.getElementById('login-btn');
const loginSubmit = document.getElementById('login-submit');
const registerBtn = document.getElementById('register-btn');
const registerSubmit = document.getElementById('register-submit');

var loginForm = document.getElementById('login-form');
var registerForm = document.getElementById('register-form');

loginBtn.addEventListener('click', function(event) {
    registerForm.style.display = 'none';
    loginForm.style.display = 'flex';
    event.preventDefault();
});

registerBtn.addEventListener('click', function(event) {
    registerForm.style.display = 'flex';
    loginForm.style.display = 'none';
    event.preventDefault();
});

loginSubmit.addEventListener('click', async function(event){
    event.preventDefault();
    var username = document.getElementById('username-login').value;
    var password = document.getElementById('password-login').value;

    const payload = {
        username : username,
        password : password
    };
    
    try{
        const token = await fetch('http://localhost:2405/token', {
            method : 'POST',
            headers : {'Content-Type': 'application/json'},
            body : JSON.stringify(payload) 
        });

        if(!token.ok){
            const errorMsg = await token.text();
            alert(errorMsg);
            return;
        }

        const tokenstring = await token.json();

        const request = {token:tokenstring.data.token};

        const response = await fetch("http://localhost:2405/introspect", {
            method : 'POST',
            headers : {'Content-Type': 'application/json'},
            body : JSON.stringify(request) 
        });

        if(!response.ok){
            const errorMsg = await response.text();
            alert(errorMsg);
            return;
        }
        window.location.href = '/messages';

    }catch(error){
        console.log(error);
    }
});

registerSubmit.addEventListener('click', async function(event) {
    event.preventDefault();
    var firstName = document.getElementById('firstname').value;
    var lastName = document.getElementById('lastname').value;
    var dob = document.getElementById('dob').value;
    var username = document.getElementById('username').value;
    var password = document.getElementById('password').value;
    var confirmPassword = document.getElementById('confirm-password').value;
    if(password !== confirmPassword){
        alert('Mật khẩu không trùng khớp!');
        return;
    }
    const payload = {
        username : username,
        password : password,
        firstName : firstName,
        lastName : lastName,
        dob : dob
    }
    
    try{
        const response = await fetch('http://localhost:2405/users', {
            method : 'POST',
            headers : {'Content-Type': 'application/json'},
            body : JSON.stringify(payload)    
        });

        if(!response.ok){
            const errorMsg = await response.text();
            alert(errorMsg);
            return;
        }
        alert("Đăng kí thành công!");
        window.location.href = '/login';
        
    }catch(error){
        console.log(error);
    }

});
