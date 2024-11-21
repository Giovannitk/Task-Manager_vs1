export function saveAuthData(token, username, role) {
  localStorage.setItem('token', token);
  localStorage.setItem('username', username);
  localStorage.setItem('role', role);
}

export function getUserRole() {
  return localStorage.getItem('role');
}

export function getToken() {
  return localStorage.getItem('token');
}

export function getUsername() {
  return localStorage.getItem('username');
}

export function isLoggedIn() {
  return !!getToken();
}

export function logOut() {
  localStorage.removeItem('token');
  localStorage.removeItem('role');
  localStorage.removeItem('username');
  location.href = 'login.html';
}
