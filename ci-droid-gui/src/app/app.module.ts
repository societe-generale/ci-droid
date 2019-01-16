import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';
import { LoggerModule, NgxLoggerLevel } from 'ngx-logger';
import { FormModule } from './routes/form/form.module';
import { RoutesModule } from './routes/routes.module';
import { HomePageModule } from './routes/home-page/home-page.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    RoutesModule,
    HomePageModule,
    FormModule,
    BrowserAnimationsModule,
    LoggerModule.forRoot({ level: NgxLoggerLevel.INFO })
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {}
