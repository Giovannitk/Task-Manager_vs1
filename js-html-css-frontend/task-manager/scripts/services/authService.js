export function getUserRole() {
  return localStorage.getItem('role');
}

export function getToken() {
  return localStorage.getItem('token');
}

export function isLoggedIn() {
  return !!getToken();
}

export function logOut() {
  localStorage.removeItem('token');
  localStorage.removeItem('role');
  location.href = 'login.html';
}