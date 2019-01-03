import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';
import { LoggerModule, NgxLoggerLevel } from 'ngx-logger';
import { RoutesModule } from './routes/routes.module';
import { HomePageModule } from './routes/home-page/home-page.module';

@NgModule({
  declarations: [AppComponent],
  imports: [BrowserModule, RoutesModule, HomePageModule, LoggerModule.forRoot({ level: NgxLoggerLevel.INFO })],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {}
