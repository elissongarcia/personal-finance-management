import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  template: `
    <div class="app-container">
      <header class="app-header">
        <h1>Dollar Price Tracker</h1>
      </header>
      <main class="app-main">
        <router-outlet></router-outlet>
      </main>
    </div>
  `,
  styles: [`
    .app-container {
      min-height: 100vh;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }
    
    .app-header {
      background: rgba(255, 255, 255, 0.1);
      backdrop-filter: blur(10px);
      padding: 1rem;
      text-align: center;
      box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    }
    
    .app-header h1 {
      color: white;
      margin: 0;
      font-size: 2rem;
      font-weight: 300;
    }
    
    .app-main {
      padding: 2rem;
    }
    
    @media (max-width: 768px) {
      .app-header h1 {
        font-size: 1.5rem;
      }
      
      .app-main {
        padding: 1rem;
      }
    }
  `]
})
export class AppComponent {
  title = 'dollar-price-frontend';
} 