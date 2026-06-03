import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-unauthorized',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="unauthorized-container">
      <div class="error-box">
        <h1>403</h1>
        <h2>Accès Refusé</h2>
        <p>Vous n'avez pas les permissions nécessaires pour accéder à cette page.</p>
        <a href="/dashboard" class="btn btn-primary">Retour au Tableau de Bord</a>
      </div>
    </div>
  `,
  styles: [`
    .unauthorized-container {
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100vh;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }

    .error-box {
      background: white;
      padding: 40px;
      border-radius: 8px;
      text-align: center;
      box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);

      h1 {
        font-size: 48px;
        color: #667eea;
        margin: 0;
      }

      h2 {
        color: #333;
        margin: 10px 0 20px 0;
      }

      p {
        color: #666;
        margin-bottom: 30px;
      }

      .btn {
        display: inline-block;
        padding: 12px 30px;
        background-color: #667eea;
        color: white;
        text-decoration: none;
        border-radius: 4px;
        transition: background-color 0.3s;

        &:hover {
          background-color: #5568d3;
        }
      }
    }
  `]
})
export class UnauthorizedComponent {}
