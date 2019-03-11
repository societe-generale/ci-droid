import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StatusComponent } from './status.component';

@NgModule({
  declarations: [StatusComponent],
  imports: [CommonModule],
  exports: [StatusComponent]
})
export class StatusModule { }
