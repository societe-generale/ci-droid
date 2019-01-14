declare namespace shared.types {
  interface Field {
    name: string;
    label: string;
    fieldType: string;
  }

  interface Action {
    expectedFields: Field[];
    actionClassToSend: string;
    label: string;
  }
}
