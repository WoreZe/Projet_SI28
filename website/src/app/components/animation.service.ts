import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AnimationService {
  private textAnimationFinished = new BehaviorSubject<boolean>(false);

  setTextAnimationFinished() {
    this.textAnimationFinished.next(true);
  }

  getTextAnimationFinished() {
    return this.textAnimationFinished.asObservable();
  }
}