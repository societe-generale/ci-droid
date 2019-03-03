import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'ci-preview-action',
  templateUrl: './preview-action.component.html',
  styleUrls: ['./preview-action.component.scss']
})
export class PreviewActionComponent implements OnInit {
  selectedAction: string;
  content: string;

  constructor() {}

  ngOnInit() {}
}
