import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'ci-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.scss']
})
export class HomePageComponent {
  constructor(private router: Router) {}

  navigateToForm() {
    this.router.navigate(['/form']);
  }
}
