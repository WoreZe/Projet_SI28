import {AfterViewInit, Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {animate, AnimationEvent, state, style, transition, trigger} from '@angular/animations';
import {AuthentificationService} from "../../services/authentification.service";
import {AnimationService} from "../animation.service";
import {WebsocketService} from "../../services/websocket.service";
import {Router} from "@angular/router";


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
  animations: [
    trigger('textAnimation', [
      state( 'in',         style({opacity: 0, transform: 'translateY(-100px)'})),
      state( 'out',       style({opacity: 1, transform: 'translateY(0)'})),
      transition('in => out', [
        animate('500ms ease-out')
      ])
    ]),
    trigger('fadeIn', [
      state( 'in',         style({opacity: 0})),
      state( 'out',       style({opacity: 1})),
      transition('in => out', [
        animate('250ms 400ms ease-out')
      ])
    ])
  ]
})

export class LoginComponent implements AfterViewInit {

  title = 'website';
  showText: boolean = false;
  codeConnexion: string | undefined;

  constructor(private animationService: AnimationService, private authService: AuthentificationService, private webSocketServiceService: WebsocketService, private router: Router) {
  }

  ngAfterViewInit(): void {
    setTimeout(() => {
      this.showText = true;
    }, 500);
  }
  isWebSocketLoading: boolean = false;
  isLoginLoading: boolean = false;
  loadingError?: string;
  saveCode(value: string) {
    this.isLoginLoading = true;
    this.codeConnexion = value;
    this.authService.login(value).subscribe({
      next: (result: any) => {
        this.isLoginLoading = false;
        this.isWebSocketLoading = true;
        this.webSocketServiceService.connectToWebSocket().subscribe({
          next: (isReady: boolean) => {
            this.isWebSocketLoading = false;
            if (isReady) {
              this.router.navigate(['/viewer/players']);
            } else {
              console.log('Connexion échouée');
            }
          }
        });
      },
      error: (err: any) => {
        this.showText = false;
        setTimeout(() => {
          this.showText = true;
        },1);
        this.isLoginLoading = false;
        //if 403 => code invalide
        if(err.status === 403){
          this.loadingError = 'Code invalide';
          return;
        }
        this.loadingError = err.message || 'Une erreur est survenue';
      }
    })
  }
}
