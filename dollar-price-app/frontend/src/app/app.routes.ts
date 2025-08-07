import { Routes } from '@angular/router';
import { DollarChartComponent } from './components/dollar-chart/dollar-chart.component';

export const routes: Routes = [
  { path: '', component: DollarChartComponent },
  { path: '**', redirectTo: '' }
]; 