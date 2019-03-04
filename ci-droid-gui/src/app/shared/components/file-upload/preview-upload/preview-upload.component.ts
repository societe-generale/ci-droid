import { SelectionModel } from '@angular/cdk/collections';
import { Component, Input, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material';
import ResourcesToUpdate = shared.types.ResourcesToUpdate;

@Component({
  selector: 'ci-preview-upload',
  templateUrl: './preview-upload.component.html',
  styleUrls: ['./preview-upload.component.scss']
})
export class PreviewUploadComponent implements OnInit {
  @Input() resourcesToUpdate: ResourcesToUpdate[] = [];

  constructor() {}

  displayedColumns: string[] = ['select', 'repoFullName', 'filePathOnRepo', 'branchName'];
  dataSource = new MatTableDataSource<ResourcesToUpdate>(this.resourcesToUpdate);
  selectedResources = new SelectionModel<ResourcesToUpdate>(true, []);

  ngOnInit() {
    this.dataSource.data = this.resourcesToUpdate;
    this.selectAll();
  }

  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {
    const numSelected = this.selectedResources.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selectedResources. */
  selectAll() {
    this.isAllSelected() ? this.selectedResources.clear() : this.dataSource.data.forEach(row => this.selectedResources.select(row));
  }
}
