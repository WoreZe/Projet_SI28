import {Injectable} from '@angular/core';
import {AuthentificationService} from "./authentification.service";
import {WebSocketSubject, WebSocketSubjectConfig} from "rxjs/internal/observable/dom/WebSocketSubject";
import {webSocket} from "rxjs/webSocket";
import {environment} from "../env";
import {BehaviorSubject, filter, retry, Subject, tap} from "rxjs";

export enum TopicTypes{
  historyInstance = "HISTORY_INSTANCE_STATE",
  dirtyScreen = "DIRTYSATE_SCREEN",
}
// data:any, type:TopicTypes, timestamp:number
export interface Message {
  type: string;
  data: any;
  timestamp: number;
}


@Injectable({
  providedIn: 'root'
})
export class WebsocketService {
  ws?: WebSocketSubject<any>;

  message$ = new Subject<Message>();
  error$ = new Subject<any>();
  isReady$ = new Subject<boolean>();

  private _messageBroker = new Map<string, BehaviorSubject<Message|undefined>[]>();


  constructor(private authService: AuthentificationService) {
    this.authService.isAuth$.pipe(filter((isAuth:boolean)=>!isAuth)).subscribe({
      next: (value) => {
        this.ws?.unsubscribe();
        this.ws = undefined;
      }
    });
  }

  private getWsConf() :WebSocketSubjectConfig<any>{
    return {
      url:`${environment.api}/instance/ws?token=${this.authService.getAccessToken()}`,
    };
  }

  connectToWebSocket() {
    if(this.ws && !this.ws.closed){
      return this.isReady$.asObservable();
    }
    this.ws = webSocket(this.getWsConf());
    this.ws.pipe(retry({count: 360, delay: 1000})).subscribe({
      next: this.onMessage.bind(this),
      error: (err) => {
        this.onWebSocketError(err);
        this.ws?.unsubscribe();
        this.ws = undefined;
      },
      complete: () => {
        this.ws?.unsubscribe();
        this.ws = undefined;
        this.onWebSocketError({message: "Connection closed"});
      }
    });
    return this.isReady$.asObservable();
  }

  private onMessage(message:  Message){
    this.isReady$.next(true);
    this.message$.next(message);
    this._messageBroker.get(message.type)?.forEach((subject) => {
      if(!subject.closed){
        subject.next(message);
      }
    });
  }

  private onWebSocketError(err: any) {
    this.error$.next(err);
    this.isReady$.next(false);
  }

  sendMessage(message: any) {
    this.ws?.next(message);
  }

  subscribeToMessage(name: TopicTypes) {
    if (!this._messageBroker.has(name)) {
      this._messageBroker.set(name, []);
    }
    const subject = new BehaviorSubject<Message|undefined>(undefined);
    this._messageBroker.get(name)?.push(subject);
    return subject.asObservable().pipe(tap({
      unsubscribe: () => {
        const index = this._messageBroker.get(name)?.indexOf(subject);
        if (index !== undefined && index !== -1) {
          this._messageBroker.get(name)?.splice(index, 1);
        }
      },
    }));
  }

  sendMessageTo(name: string, message: any) {
    this.sendMessage({name, data: message});
  }

  subscribeTo(name: TopicTypes) {
    return this.subscribeToMessage(name);
  }
}
