import { Component, Input, OnInit } from '@angular/core';
import SelectedField = shared.types.SelectedField;

@Component({
  selector: 'ci-preview-action',
  templateUrl: './preview-action.component.html',
  styleUrls: ['./preview-action.component.scss']
})
export class PreviewActionComponent implements OnInit {
  @Input() selectedAction: string;
  @Input() fields: SelectedField[];

  constructor() {}

  ngOnInit() {}
}
