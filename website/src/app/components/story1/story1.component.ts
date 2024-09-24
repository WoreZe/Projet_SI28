import { AfterViewInit, Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { trigger, style, animate, transition, stagger, query, state, AnimationEvent } from '@angular/animations';
import { AnimationService } from '../animation.service';

@Component({
  selector: 'app-story1',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './story1.component.html',
  styleUrl: './story1.component.css',
  animations: [
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0 }),
            animate('1000ms', style({ opacity: 1 }))
        ])
    ])
  ]
})
export class Story1Component implements AfterViewInit {
  animationDuration = 1000;
  animationDelay = 100;
  textQuestion: string ="Vous voila plongés dans le temple de Maitre Edmond. Une odeur de papier enveloppe la pièce. Vous observez la pièce et voyez devant vous le fameux papy russe ! Vous devez découvrir le nom d'Edmond tel qu'il était prononcé à l'époque grâce à son énigme. Pour cela, découvrez par quelle lettre est remplacée la lettre K et appliquez ce décalage au nom de Maitre Edmond."
  cheminImage: string = 'assets/images/egyptain_temple_interior.jpg'

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

