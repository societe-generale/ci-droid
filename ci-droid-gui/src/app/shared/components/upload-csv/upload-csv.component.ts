import { SelectionModel } from '@angular/cdk/collections';
import { Component } from '@angular/core';
import { MatTableDataSource } from '@angular/material';
import ResourcesToUpdate = shared.types.ResourcesToUpdate;

const SEMICOLON = ';';

export const resourcesToUpdate: ResourcesToUpdate[] = [
  {
    repoFullName: 'ItecFccOsd/financing-platform-lso-syn-type-batch',
    filePathOnRepo: 'Jenkinsfile',
    branchName: 'master'
  },
  { repoFullName: 'ItecFccOsd/financing-platform-ifrs9-analytics', filePathOnRepo: 'Jenkinsfile', branchName: 'master' },
  { repoFullName: 'ItecFccOsd/financing-platform-tcl-analytics', filePathOnRepo: 'Jenkinsfile', branchName: 'master' },
  { repoFullName: 'ItecFccOsd/financing-platform-cache-server', filePathOnRepo: 'Jenkinsfile', branchName: 'master' },
  {
    repoFullName: 'ItecFccOsd/financing-platform-declarative-process-nightly',
    filePathOnRepo: 'Jenkinsfile',
    branchName: 'master'
  },
  {
    repoFullName: 'ItecFccOsd/financing-platform-liquidity-performance',
    filePathOnRepo: 'Jenkinsfile',
    branchName: 'master'
  },
  { repoFullName: 'ItecFccOsd/financing-platform-deal-support', filePathOnRepo: 'Jenkinsfile', branchName: 'master' },
  { repoFullName: 'ItecFccOsd/financing-platform-credapp', filePathOnRepo: 'Jenkinsfile', branchName: 'master' },
  { repoFullName: 'ItecFccOsd/financing-platform-particles', filePathOnRepo: 'Jenkinsfile', branchName: 'master' },
  {
    repoFullName: 'ItecFccOsd/financing-platform-credit-approval-integration',
    filePathOnRepo: 'Jenkinsfile',
    branchName: 'master'
  }
];

@Component({
  selector: 'ci-upload-csv',
  templateUrl: './upload-csv.component.html',
  styleUrls: ['./upload-csv.component.scss']
})
export class UploadCsvComponent {
  displayedColumns: string[] = ['select', 'repoFullName', 'filePathOnRepo', 'branchName'];
  dataSource = new MatTableDataSource<ResourcesToUpdate>(resourcesToUpdate);
  selectedResources = new SelectionModel<ResourcesToUpdate>(true, []);
  fileName;
  resourcesToUpdate: ResourcesToUpdate[] = [];

  constructor() {
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

  onReaderLoad = event => {
    const csvData = event.target.result;
    this.resourcesToUpdate = this.CSV2JSON(csvData);
    this.resourcesToUpdate = this.resourcesToUpdate.filter(obj => obj.repoFullName !== '');
    this.dataSource = new MatTableDataSource<ResourcesToUpdate>(this.resourcesToUpdate);
    this.selectAll();
  };

  handleDragAndDrop(event) {
    if (event.type === 'dragover') {
      document.querySelector('div.upload-csv').classList.add('on-file-over');
    } else if (event.type === 'dragleave' || event.type === 'drop') {
      document.querySelector('div.upload-csv').classList.remove('on-file-over');
    }
    if (event.type === 'drop') {
      this.getFileContent(event.dataTransfer.files[0]);
    }
  }

  getFileContent(file) {
    this.fileName = file.name;
    const reader = new FileReader();
    reader.readAsText(file);
    reader.onload = this.onReaderLoad;
    reader.onerror = () => {
      alert('Unable to read ' + file.name);
    };
  }

  onFileChange(event) {
    this.getFileContent(event.target.files[0]);
  }

  CSV2JSON(csv) {
    const rows = csv.split('\r\n');
    const result = [];
    let headers = rows[0].split(SEMICOLON);
    const position = JSON.stringify(this.displayedColumns.slice(1, 4)) === JSON.stringify(headers) ? 1 : 0;
    if (position === 0) {
      headers = this.displayedColumns.slice(1, 4);
    }
    rows.slice(position, rows.length).forEach(row => {
      const obj = {};
      const currentRow = row.split(SEMICOLON);
      currentRow.forEach((item, index) => {
        obj[headers[index]] = item;
      });
      result.push(obj);
    });
    return result;
  }

  fileReset() {
    this.fileName = '';
    this.resourcesToUpdate = [];
    this.selectedResources.clear();
    this.dataSource = new MatTableDataSource<ResourcesToUpdate>([]);
  }
}
