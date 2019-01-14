import { Routes } from '@angular/router';
import { HomePageComponent } from './home-page/home-page.component';
import { FormComponent } from './form/form.component';

export const appRoutes: Routes = [{ path: '', component: HomePageComponent }, { path: 'form', component: FormComponent }];
