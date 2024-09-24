import { AfterViewInit, Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { trigger, style, animate, transition, AnimationEvent } from '@angular/animations';
import { AnimationService } from '../animation.service';

@Component({
  selector: 'app-story3',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './story3.component.html',
  styleUrl: './story3.component.css',
  animations: [
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0 }),
            animate('1000ms', style({ opacity: 1 }))
        ])
    ])
  ]
})
export class Story3Component implements AfterViewInit {

  textQuestion: string = "Vous êtes désormais face à une porte fermée, comment pouvez vous l'ouvrir ?"


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

  onAnimationEnd(event: AnimationEvent) {
    this.animationService.setTextAnimationFinished();
  }

}
