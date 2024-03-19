import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private token: string | null = null;

  constructor() { }

  isLogged() {
    return this.token !== null;
  }

  getToken() {
    return this.token;
  }

  login(username: 'admin'|'manager'|'user') {
    const rawToken = `${username}:password`
    // Encode rawToken in base64
    this.token = btoa(rawToken)
  }

  logout() {
    this.token = null;
  }
}
