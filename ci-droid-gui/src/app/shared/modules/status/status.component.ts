import { Component, Input, OnInit, NgZone } from '@angular/core';
import { animate, keyframes, state, style, transition, trigger } from '@angular/animations';

const keyFrames = [
  style({strokeDashoffset: 1000, offset: 0}),
  style({strokeDashoffset: 0, offset: 1})
];

@Component({
  selector: 'ci-status',
  templateUrl: './status.component.html',
  styleUrls: ['./status.component.scss'],
  animations: [
    trigger('tick', [
      state('initial', style({
        strokeDasharray: 1000,
        strokeDashoffset: -100
      })),
      transition('initial => final', animate('2.5s .35s ease-in-out',
        keyframes([
          style({strokeDashoffset: -100, offset: 0}),
          style({strokeDashoffset: 900, offset: 1})
        ])))
    ]),
    trigger('line', [
      state('initial', style({
        strokeDasharray: 1000,
        strokeDashoffset: 1000
      })),
      transition('initial => final', animate('2.5s .35s ease-in-out',
        keyframes(keyFrames)))
    ]),
    trigger('circle', [
      state('initial', style({
        strokeDasharray: 1000,
        strokeDashoffset: 0
      })),
      transition('initial => final', animate('0.9s ease-in-out',
        keyframes(keyFrames)))
    ])
  ]
})
export class StatusComponent implements OnInit {

  @Input() success;
  state: string;

  constructor(public zone: NgZone) {
  }

  ngOnInit() {
    this.triggerAnimation();
  }

  triggerAnimation() {
    this.state = 'initial';
  }

  reset() {
    this.zone.run(() => {
      this.state = 'final';
    });
  }
}
