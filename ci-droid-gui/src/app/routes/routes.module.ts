import { ModuleWithProviders } from '@angular/core';
import { RouterModule } from '@angular/router';
import { appRoutes } from './routes';

export const RoutesModule: ModuleWithProviders = RouterModule.forRoot(appRoutes);
