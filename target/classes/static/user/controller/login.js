
function showLogin() {
    document.getElementById('login-form').style.display = 'block';
    document.getElementById('register-form').style.display = 'none';
    document.querySelector('.login-tab').classList.add('active');
    document.querySelector('.register-tab').classList.remove('active');
}

function showRegister() {
    document.getElementById('login-form').style.display = 'none';
    document.getElementById('register-form').style.display = 'block';
    document.querySelector('.register-tab').classList.add('active');
    document.querySelector('.login-tab').classList.remove('active');
}

