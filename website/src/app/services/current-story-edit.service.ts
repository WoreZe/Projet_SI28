import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {BehaviorSubject, filter, map, tap} from "rxjs";
import {History, HistoryStepFull} from "../models/history-instance.model";

@Injectable({
  providedIn: 'root'
})
export class CurrentStoryEditService {
  rawCurrentStory$ = new BehaviorSubject<History | undefined>(undefined);
  currentStory$ = this.rawCurrentStory$.pipe(filter((story) => story !== null));
  steps$ :BehaviorSubject<HistoryStepFull[]> = new BehaviorSubject<HistoryStepFull []>([]);

  constructor(private http: HttpClient) {
  }

  getHistory(parsedStoryId: number) {
    return this.http.get<History>(`/history/${parsedStoryId}`).pipe(map((item: any) => new History(item)), tap((history: History) => {
      this.rawCurrentStory$.next(history);
      this.getSteps(history).subscribe((steps) => {
        if(steps.length > 0){
          steps = steps.map((step) => {
            if(step.id === history.entrypoint.id){
              step.isEntryPoint = true;
            }
            return step;
          });
          steps.sort((a, b) => a.isEntryPoint ? -1 : b.isEntryPoint ? 1 : 0);
        }
        this.steps$.next(steps);
      });
    }));
  }

  getHistories() {
    return this.http.get<History[]>('/history').pipe(map((items: any[]) => items.map((item) => new History(item))));
  }

  getSteps(history: History) {
    return this.http.get<HistoryStepFull[]>(`/history/${history.id}/steps`).pipe(map((items: any[]) => items.map((item) => new HistoryStepFull(item))));
  }

  saveStepScript(currentStep: HistoryStepFull) {
    return this.http.put(`/history/${this.rawCurrentStory$.value?.id}/steps/${currentStep.id}/script`, {script: currentStep.scriptController}).pipe(tap((data) => {
     this.steps$.next(this.steps$.value.map((step) => step.id === currentStep.id ? new HistoryStepFull(data) : step));
    }));
  }

  runStep(historyStepFull: HistoryStepFull) {
    return this.http.post(`/history/${this.rawCurrentStory$.value?.id}/steps/${historyStepFull.id}/run`, {});
  }

  saveStepPage(historyStepFull: HistoryStepFull) {
    return this.http.put(`/history/${this.rawCurrentStory$.value?.id}/steps/${historyStepFull.id}/template`, {template: historyStepFull.page.content});
  }

  setEntryPoint(historyStepFull: HistoryStepFull) {
    return this.http.post(`/history/${this.rawCurrentStory$.value?.id}/entrypoint`, {
      stepId: historyStepFull.id
    });
  }
}
