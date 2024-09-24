import {AfterViewInit, Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {animate, AnimationEvent, state, style, transition, trigger} from '@angular/animations';
import {AuthentificationService} from "../../services/authentification.service";
import {AnimationService} from "../animation.service";
import {WebsocketService} from "../../services/websocket.service";
import {Router, RouterLink} from "@angular/router";
import {CurrentStoryPlayerService} from "../../services/current-story-player.service";
import {HistoryInstanceModel, HistoryUser} from "../../models/history-instance.model";


@Component({
  selector: 'app-waiting-for-player',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './waiting-for-player.component.html',
  styleUrl: './waiting-for-player.component.css',
})

export class WaitingForPlayer {
  joinCode: string = '';
  players: HistoryUser[] = [];
  historyInstance: HistoryInstanceModel | null = null;

  constructor(protected webSocketServiceService: CurrentStoryPlayerService, websocket:WebsocketService,private router: Router) {
    this.webSocketServiceService.currentStoryInstance$.subscribe({
      next: (instance) => {
        if(instance){
          this.joinCode = instance.joinCode;
          if(instance.users){
            this.players = instance.users;
          }
          if(instance.state === "IN_GAME"){
            this.router.navigate(['/viewer/story']);
          }
        }
        this.historyInstance = instance;
      }
    });
    websocket.connectToWebSocket().subscribe({});
  }

  startGame() {
    this.webSocketServiceService.startGame().subscribe({
      next: () => {
        this.router.navigate(['/viewer/story']);
      }
    })
  }
}
