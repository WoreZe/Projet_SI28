import { AfterViewInit, Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { trigger, style, animate, transition, AnimationEvent } from '@angular/animations';
import { AnimationService } from '../animation.service';

@Component({
  selector: 'app-story5',
  standalone: true,
  imports: [],
  templateUrl: './story5.component.html',
  styleUrl: './story5.component.css',
  animations: [
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0 }),
            animate('1000ms', style({ opacity: 1 }))
        ])
    ])
  ]
})
export class Story5Component implements AfterViewInit {

  textQuestion: string = "L'esprit de maitre Edmond se dresse devant vous, à vous de prononcer sa devise afin de lever le sort qu'il avait jeté sur vous !!"

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
