import { PreviewActionComponent } from './preview-action.component';
import { MatCardModule } from '@angular/material';
import { Component } from '@angular/core';
import { initContext, TestContext } from '../../../../testing/test.context';

@Component({
  template: `
    <ci-preview-action [selectedAction]="selectedAction" [fields]="fields"></ci-preview-action>
  `
})
class TesteePreviewActionComponent {
  selectedAction = 'Content to overwrite/create';
  fields = [
    {
      value: 'staticContent',
      label: 'content to overwrite/create'
    }
  ];
}

describe('PreviewActionComponent', () => {
  // PreviewActionComponent - testedComponent
  // TesteePreviewAction - hostComponent
  type Context = TestContext<PreviewActionComponent, TesteePreviewActionComponent>;

  const moduleMetaData = {
    imports: [MatCardModule]
  };

  initContext(PreviewActionComponent, TesteePreviewActionComponent, moduleMetaData);

  it('should create', function(this: Context) {
    this.detectChanges();
    expect(this.hostComponent).toBeTruthy();
    expect(this.testedComponent).toBeTruthy();
  });

  it('should have an input for the selected action and the content of the action to be performed ', function(this: Context) {
    this.detectChanges();
    expect(this.testedComponent.selectedAction).toBe('Content to overwrite/create');
    expect(this.testedComponent.fields).toEqual([
      {
        value: 'staticContent',
        label: 'content to overwrite/create'
      }
    ]);
    // change the host
    this.hostComponent.selectedAction = 'simple replace in the file';
    this.hostComponent.fields = [
      {
        value: 'initialValue',
        label: 'old value, to replace'
      },
      {
        value: 'newValue',
        label: 'new value'
      }
    ];
    this.detectChanges();
    expect(this.testedComponent.selectedAction).toBe('simple replace in the file');
    expect(this.testedComponent.fields).toEqual([
      {
        value: 'initialValue',
        label: 'old value, to replace'
      },
      {
        value: 'newValue',
        label: 'new value'
      }
    ]);
  });
});
