import { Component, Input, OnInit, NgZone } from '@angular/core';
import { animate, keyframes, state, style, transition, trigger } from '@angular/animations';
import { StatusState } from './status.enum';

const keyFrames = [style({ strokeDashoffset: 1000, offset: 0 }), style({ strokeDashoffset: 0, offset: 1 })];

@Component({
  selector: 'ci-status',
  templateUrl: './status.component.html',
  styleUrls: ['./status.component.scss'],
  animations: [
    trigger('tick', [
      state(
        StatusState.INITIAL,
        style({
          strokeDasharray: 1000,
          strokeDashoffset: -100
        })
      ),
      transition(
        `${StatusState.INITIAL} => ${StatusState.FINAL}`,
        animate(
          '2.5s .35s ease-in-out',
          keyframes([style({ strokeDashoffset: -100, offset: 0 }), style({ strokeDashoffset: 900, offset: 1 })])
        )
      )
    ]),
    trigger('line', [
      state(
        StatusState.INITIAL,
        style({
          strokeDasharray: 1000,
          strokeDashoffset: 1000
        })
      ),
      transition(`${StatusState.INITIAL} => ${StatusState.FINAL}`, animate('2.5s .35s ease-in-out', keyframes(keyFrames)))
    ]),
    trigger('circle', [
      state(
        StatusState.INITIAL,
        style({
          strokeDasharray: 1000,
          strokeDashoffset: 0
        })
      ),
      transition(`${StatusState.INITIAL} => ${StatusState.FINAL}`, animate('0.9s ease-in-out', keyframes(keyFrames)))
    ])
  ]
})
export class StatusComponent implements OnInit {
  @Input() success;
  state: string;

  constructor(public zone: NgZone) {}

  ngOnInit() {
    this.triggerAnimation();
  }

  triggerAnimation() {
    this.state = StatusState.INITIAL;
  }

  reset() {
    this.zone.run(() => {
      this.state = StatusState.FINAL;
    });
  }
}
