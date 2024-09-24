import { AfterViewInit, Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { trigger, style, animate, transition, AnimationEvent } from '@angular/animations';
import { AnimationService } from '../animation.service';

@Component({
  selector: 'app-story2',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './story2.component.html',
  styleUrl: './story2.component.css',
  animations: [
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0 }),
            animate('1000ms', style({ opacity: 1 }))
        ])
    ])
  ]
})
export class Story2Component implements AfterViewInit{
  animationDuration = 500; 
  animationDelay = 100; 
  isDarkBackground: boolean = true;  
  textQuestion: string = "Il fait très sombre ici, nous y verrons plus clair si nous allumons nos lampes torches !"

  toggleBackground() {
    this.isDarkBackground = !this.isDarkBackground;
    this.textQuestion = "Voilà qui est mieux, vous découvrez devant vous une immense statue de Bastet la déesse égyptienne ! Elle semble attendre de votre part une interaction, malheureusment je ne pense pas qu'elle parle notre langue..."
  }

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

  getClassByIndex(index: number): string {
    switch (index) {
      case 0:
        return 'bg-red-700 question-' + (index + 1);
      case 1:
        return 'bg-lime-700 question-' + (index + 1);
      case 2:
        return 'bg-blue-700 question-' + (index + 1);
      case 3:
        return 'bg-yellow-400 question-' + (index + 1);
      default:
        return ''; 
    }
  }

  calculateDelay(index: number): string {
    return `${this.animationDelay + index * (this.animationDuration + this.animationDelay)}ms`;
  }



  onAnimationEnd(event: AnimationEvent) {
    this.animationService.setTextAnimationFinished();
  }

}

