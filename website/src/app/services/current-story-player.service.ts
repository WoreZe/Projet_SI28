import {Injectable} from '@angular/core';
import {Message, TopicTypes, WebsocketService} from "./websocket.service";
import {BehaviorSubject, debounceTime, Subject, tap} from "rxjs";
import {HistoryInstanceModel, HistoryStep} from "../models/history-instance.model";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root',
  deps: [WebsocketService]
})
export class CurrentStoryPlayerService {
  isPlaying: boolean = false;
  currentStoryInstance$ = new BehaviorSubject<HistoryInstanceModel | null>(null);
  currentPageContent$: BehaviorSubject<string> = new BehaviorSubject<string>('');
  _currentStep?: HistoryStep;
  requestUpdateFetch: Subject<void> = new Subject<any>();

  constructor(private webSocket: WebsocketService, private httpClient: HttpClient) {
    this.webSocket.subscribeTo(TopicTypes.historyInstance).subscribe({
      next: (message: Message | undefined) => {
        if (message) {
          const inst = new HistoryInstanceModel(message?.data);
          this.currentStoryInstance$.next(inst);
          //step change ?
          if (this._currentStep?.id !== inst.currentStep?.id) {
            this._currentStep = inst.currentStep;
            this.requestUpdateFetch.next();
          }
        }
      }
    });
  }

  ensureConnected() {
    this.webSocket.connectToWebSocket().subscribe();
    this.requestUpdateFetch.pipe(debounceTime(200)).subscribe(() => {
      this.fetchPageContent();
    });
    this.requestUpdateFetch.next();
    this.webSocket.subscribeTo(TopicTypes.dirtyScreen).subscribe({
      next: (message: Message | undefined) => {
        this.requestUpdateFetch.next();
      }
    });
  }

  startGame() {
    return this.httpClient.post<HistoryInstanceModel>(`/instances/${this.currentStoryInstance$.value?.id}/play`, {}).pipe(tap({
      next: (instance) => {
        this.isPlaying = true;
        this.currentStoryInstance$.next(new HistoryInstanceModel(instance));
      }
    }));
  }

  private fetchPageContent() {
    return this.httpClient.get(`/instances/${this.currentStoryInstance$.value?.id}/page`, {
      headers: {
        'Content-Type': 'application/json',
      }
    }).subscribe({
      next: (content: any) => {
        this.currentPageContent$.next(content["page"]);
      }
    });
  }
}
