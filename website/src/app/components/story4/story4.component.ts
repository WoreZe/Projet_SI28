import { AfterViewInit, Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { trigger, style, animate, transition, AnimationEvent } from '@angular/animations';
import { AnimationService } from '../animation.service';

@Component({
  selector: 'app-story4',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './story4.component.html',
  styleUrl: './story4.component.css',
  animations: [
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0 }),
            animate('1000ms', style({ opacity: 1 }))
        ])
    ])
  ]
})

export class Story4Component implements AfterViewInit{
  textQuestion1: string = "Ce farceur d'Edmond a décidé de vous téléporter dans le monde des nuages, cet endroit est magnifique !!! Prenez garde les nuages sont imprévisibles...";
  textQuestion2: string = "C'est une très bonne idée d'explorer ce monde, malheureusment joueurrandom1 et joueurrandom2 manquent d'attention et tombent, VITE IL FAUT PRENDRE UNE DECISION !!!!";
  textQuestion3: string = "S'approcher du bord permet peut être de mieux comprendre la situation, mais c'est aussi le meilleur moyen de se mettre en danger, joueurrandom1 et joueurrandom2 manquent d'attention et tombent, VITE IL FAUT PRENDRE UNE DECISION !!!!";
  textAnswer1_1: string = "Partir explorer le monde des nuages";
  textAnswer1_2: string = "S'approcher du bord";
  textAnswer2_1: string = "Les sauver au risque de tomber vous aussi et de tout perdre";
  textAnswer2_2: string = "Finir l'aventure sans eux";

  isFirstQuestion = true;
  isExplore = false;
  isBorder = false;

  constructor(private animationService: AnimationService) {}

  ngAfterViewInit(): void {
    this.animationService.getTextAnimationFinished().subscribe((finished) => {
      if (finished) {
        const answer4Container = document.getElementById('answer4Container');
        const answer2Container = document.getElementById('answer2Container');
        if (answer4Container !== null) {
          answer4Container.classList.add('animate-fade-in');
        }
        if (answer2Container !== null) {
          answer2Container.classList.add('animate-fade-in');
        }
      }
    });
  }

  setExplore(){
    this.isFirstQuestion = false;
    this.isExplore = true;
    this.isBorder = false;
  }

  setBorder(){
    this.isFirstQuestion = false;
    this.isExplore = false;
    this.isBorder = true;
  }

  onAnimationEnd(event: AnimationEvent) {
    this.animationService.setTextAnimationFinished();
  }
}
