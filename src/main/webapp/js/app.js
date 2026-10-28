// Smart Internship Portal Client Script

function openModal(id) {
    var modal = document.getElementById(id);
    if (modal) {
        modal.style.display = 'flex';
    }
}

function closeModal(id) {
    var modal = document.getElementById(id);
    if (modal) {
        modal.style.display = 'none';
    }
}

// Close modal when clicking outside modal-content
window.onclick = function(event) {
    if (event.target.classList.contains('modal-backdrop')) {
        event.target.style.display = 'none';
    }
};

// Fast Demo Credentials Auto-Fill
function fillCredentials(email, password) {
    var emailField = document.getElementById('email');
    var passwordField = document.getElementById('password');
    if (emailField) emailField.value = email;
    if (passwordField) passwordField.value = password;
}

// Toggle role specific fields in Registration
function handleRoleChange(role) {
    var studentFields = document.getElementById('studentFields');
    var companyFields = document.getElementById('companyFields');
    if (!studentFields || !companyFields) return;

    if (role === 'STUDENT') {
        studentFields.style.display = 'block';
        companyFields.style.display = 'none';
    } else {
        studentFields.style.display = 'none';
        companyFields.style.display = 'block';
    }
}

