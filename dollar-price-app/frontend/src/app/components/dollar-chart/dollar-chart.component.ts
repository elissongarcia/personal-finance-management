import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { NgChartsModule } from 'ng2-charts';
import { ChartConfiguration, ChartOptions } from 'chart.js';
import { ApiService, DollarPrice } from '../../services/api.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-dollar-chart',
  standalone: true,
  imports: [CommonModule, HttpClientModule, NgChartsModule],
  template: `
    <div class="chart-container">
      <div class="chart-card">
        <div class="chart-header">
          <h2>USD to CAD Exchange Rate</h2>
          <p class="subtitle">Last 7 days of exchange rates</p>
        </div>
        
        <div class="loading-container" *ngIf="loading">
          <div class="loading-spinner"></div>
          <p>Loading data...</p>
        </div>
        
        <div class="error-container" *ngIf="error">
          <p class="error-message">{{ error }}</p>
          <button class="retry-button" (click)="loadData()">Retry</button>
        </div>
        
        <div class="chart-wrapper" *ngIf="!loading && !error">
          <canvas baseChart
            [data]="lineChartData"
            [options]="lineChartOptions"
            [type]="'line'">
          </canvas>
        </div>
        
        <div class="stats-container" *ngIf="!loading && !error && prices.length > 0">
          <div class="stat-item">
            <span class="stat-label">Current Rate:</span>
            <span class="stat-value">{{ getCurrentRate() | number:'1.4-4' }} CAD</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">7-Day Average:</span>
            <span class="stat-value">{{ getAverageRate() | number:'1.4-4' }} CAD</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">Last Updated:</span>
            <span class="stat-value">{{ getLastUpdated() | date:'medium' }}</span>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .chart-container {
      max-width: 1200px;
      margin: 0 auto;
    }
    
    .chart-card {
      background: rgba(255, 255, 255, 0.95);
      border-radius: 15px;
      padding: 2rem;
      box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
      backdrop-filter: blur(10px);
    }
    
    .chart-header {
      text-align: center;
      margin-bottom: 2rem;
    }
    
    .chart-header h2 {
      color: #333;
      margin: 0 0 0.5rem 0;
      font-size: 1.8rem;
      font-weight: 600;
    }
    
    .subtitle {
      color: #666;
      margin: 0;
      font-size: 1rem;
    }
    
    .chart-wrapper {
      position: relative;
      height: 400px;
      margin-bottom: 2rem;
    }
    
    .loading-container {
      text-align: center;
      padding: 3rem;
    }
    
    .loading-spinner {
      width: 50px;
      height: 50px;
      border: 4px solid #f3f3f3;
      border-top: 4px solid #667eea;
      border-radius: 50%;
      animation: spin 1s linear infinite;
      margin: 0 auto 1rem;
    }
    
    @keyframes spin {
      0% { transform: rotate(0deg); }
      100% { transform: rotate(360deg); }
    }
    
    .error-container {
      text-align: center;
      padding: 2rem;
    }
    
    .error-message {
      color: #e74c3c;
      margin-bottom: 1rem;
    }
    
    .retry-button {
      background: #667eea;
      color: white;
      border: none;
      padding: 0.75rem 1.5rem;
      border-radius: 5px;
      cursor: pointer;
      font-size: 1rem;
      transition: background-color 0.3s;
    }
    
    .retry-button:hover {
      background: #5a6fd8;
    }
    
    .stats-container {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 1rem;
      margin-top: 2rem;
      padding-top: 2rem;
      border-top: 1px solid #eee;
    }
    
    .stat-item {
      text-align: center;
      padding: 1rem;
      background: rgba(102, 126, 234, 0.1);
      border-radius: 8px;
    }
    
    .stat-label {
      display: block;
      color: #666;
      font-size: 0.9rem;
      margin-bottom: 0.5rem;
    }
    
    .stat-value {
      display: block;
      color: #333;
      font-size: 1.2rem;
      font-weight: 600;
    }
    
    /* Mobile Responsive */
    @media (max-width: 768px) {
      .chart-card {
        padding: 1rem;
        margin: 0 1rem;
      }
      
      .chart-header h2 {
        font-size: 1.5rem;
      }
      
      .chart-wrapper {
        height: 300px;
      }
      
      .stats-container {
        grid-template-columns: 1fr;
        gap: 0.5rem;
      }
      
      .stat-item {
        padding: 0.75rem;
      }
      
      .stat-value {
        font-size: 1rem;
      }
    }
    
    @media (max-width: 480px) {
      .chart-wrapper {
        height: 250px;
      }
      
      .chart-header h2 {
        font-size: 1.3rem;
      }
      
      .subtitle {
        font-size: 0.9rem;
      }
    }
  `]
})
export class DollarChartComponent implements OnInit, OnDestroy {
  prices: DollarPrice[] = [];
  loading = true;
  error: string | null = null;
  private subscription: Subscription = new Subscription();

  public lineChartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: [
      {
        data: [],
        label: 'USD to CAD',
        fill: true,
        tension: 0.4,
        borderColor: '#667eea',
        backgroundColor: 'rgba(102, 126, 234, 0.1)',
        pointBackgroundColor: '#667eea',
        pointBorderColor: '#fff',
        pointHoverBackgroundColor: '#fff',
        pointHoverBorderColor: '#667eea'
      }
    ]
  };

  public lineChartOptions: ChartOptions<'line'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: false
      },
      tooltip: {
        mode: 'index',
        intersect: false,
        backgroundColor: 'rgba(0, 0, 0, 0.8)',
        titleColor: '#fff',
        bodyColor: '#fff',
        borderColor: '#667eea',
        borderWidth: 1
      }
    },
    scales: {
      x: {
        display: true,
        title: {
          display: true,
          text: 'Date'
        },
        grid: {
          display: false
        }
      },
      y: {
        display: true,
        title: {
          display: true,
          text: 'CAD Rate'
        },
        grid: {
          color: 'rgba(0, 0, 0, 0.1)'
        }
      }
    },
    interaction: {
      mode: 'nearest',
      axis: 'x',
      intersect: false
    }
  };

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.loadData();
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  loadData(): void {
    this.loading = true;
    this.error = null;

    this.subscription.add(
      this.apiService.getDollarPrices().subscribe({
        next: (data) => {
          this.prices = data;
          this.updateChartData();
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Failed to load data. Please try again.';
          this.loading = false;
          console.error('Error loading data:', err);
        }
      })
    );
  }

  private updateChartData(): void {
    if (this.prices.length === 0) return;

    // Sort prices by timestamp (oldest first)
    const sortedPrices = [...this.prices].sort((a, b) => 
      new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime()
    );

    this.lineChartData.labels = sortedPrices.map(price => 
      new Date(price.timestamp).toLocaleDateString()
    );

    this.lineChartData.datasets[0].data = sortedPrices.map(price => price.price);
  }

  getCurrentRate(): number {
    if (this.prices.length === 0) return 0;
    return this.prices[0].price;
  }

  getAverageRate(): number {
    if (this.prices.length === 0) return 0;
    const sum = this.prices.reduce((acc, price) => acc + price.price, 0);
    return sum / this.prices.length;
  }

  getLastUpdated(): string {
    if (this.prices.length === 0) return '';
    return this.prices[0].timestamp;
  }
} 