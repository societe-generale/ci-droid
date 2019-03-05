import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'ci-preview-github-interaction',
  templateUrl: './preview-github-interaction.component.html',
  styleUrls: ['./preview-github-interaction.component.scss']
})
export class PreviewGithubInteractionComponent implements OnInit {
  @Input() githubInteraction: string;
  @Input() title: string;
  @Input() branchName: string;
  @Input() commitMessage: string;
  constructor() {}

  ngOnInit() {}
}
