import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {BehaviorSubject, tap} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class AuthentificationService {

  isAuth$:BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  constructor(private http: HttpClient) {
    this.isAuth$.next(this.isAuth());
  }

  login(code:string) {
    return this.http.post('/auth/login/history', {
      code: code,
      username: 'screen',
      avatar: "",
      screen: true
    }).pipe(tap({
      next: (data: any) => {
        localStorage.setItem('story_screen_access', data.accessToken);
        this.isAuth$.next(true);
      }
    }));
  }

  getAccessToken(){
    this.isAuth$.next(this.isAuth());
    return localStorage.getItem('story_screen_access');
  }

  isAuth() {
    return localStorage.getItem('story_screen_access') !== null;
  }

  signOut() {
    localStorage.removeItem('story_screen_access');
    this.isAuth$.next(false);
  }
}
