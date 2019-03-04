import { Component } from '@angular/core';
import ResourcesToUpdate = shared.types.ResourcesToUpdate;

const SEMICOLON = ';';

@Component({
  selector: 'ci-upload-csv',
  templateUrl: './upload-csv.component.html',
  styleUrls: ['./upload-csv.component.scss']
})
export class UploadCsvComponent {
  fileName;
  resourcesToUpdate: ResourcesToUpdate[] = [];

  constructor() {}

  onReaderLoad = event => {
    const csvData = event.target.result;
    this.resourcesToUpdate = this.CSV2JSON(csvData);
    this.resourcesToUpdate = this.resourcesToUpdate.filter(obj => obj.repoFullName !== '');
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
    const displayedColumns = ['select', 'repoFullName', 'filePathOnRepo', 'branchName'];
    const rows = csv.split('\r\n');
    const result = [];
    let headers = rows[0].split(SEMICOLON);
    const position = JSON.stringify(displayedColumns.slice(1, 4)) === JSON.stringify(headers) ? 1 : 0;
    if (position === 0) {
      headers = displayedColumns.slice(1, 4);
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

  resetFile() {
    this.fileName = '';
    this.resourcesToUpdate = [];
  }
}
