import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {RouterOutlet, RouterLink, Router} from '@angular/router';
import {AuthentificationService} from "./services/authentification.service";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, CommonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})

export class AppComponent {

  constructor(private authService: AuthentificationService, private router: Router) {
    // this.authService.isAuth$.subscribe((isAuth) => {
    //   if (!isAuth) {
    //     this.router.navigate(['login']);
    //   }
    // });
  }

}
