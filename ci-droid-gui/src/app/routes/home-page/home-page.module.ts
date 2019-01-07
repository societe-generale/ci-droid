import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { HomePageComponent } from './home-page.component';
import { MatButtonModule } from '@angular/material';

@NgModule({
  imports: [CommonModule, MatButtonModule],
  declarations: [HomePageComponent]
})
export class HomePageModule {}
