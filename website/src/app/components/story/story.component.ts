import {AfterViewInit, Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {animate, AnimationEvent, style, transition, trigger} from '@angular/animations';
import {HistoryInstanceModel, HistoryUser} from "../../models/history-instance.model";
import {CurrentStoryPlayerService} from "../../services/current-story-player.service";


@Component({
  selector: 'app-story',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './story.component.html',
  styleUrl: './story.component.css',
  animations: [
    trigger('fadeIn', [
      transition(':enter', [
        style({opacity: 0}),
        animate('1000ms', style({opacity: 1}))
      ])
    ])
  ]
})
export class StoryComponent {
  players: HistoryUser[] = [];
  htmlPageContent: string = '';
  instance?: HistoryInstanceModel;

  constructor(protected currentStory: CurrentStoryPlayerService) {
    this.currentStory.currentStoryInstance$.subscribe({
      next: (instance) => {
        if (instance) {
          this.instance = instance;
          if (instance.users) {
            this.players = instance.users;
          }
        }
      }
    });
    this.currentStory.currentPageContent$.subscribe({
      next: (content) => {
        if(content && content.length > 0){
          this.htmlPageContent = content;
        }
      }
    });
    this.currentStory.ensureConnected();
  }

}
