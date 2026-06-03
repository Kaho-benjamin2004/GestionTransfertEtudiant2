import { Component } from '@angular/core';

@Component({
  selector: 'app-etudiant',
  standalone: true,
  template: `
    <div class="placeholder">
      <h1>Section Étudiant</h1>
      <p>Cette section sera développée pour les demandes de transfert des étudiants.</p>
    </div>
  `,
  styles: [`
    .placeholder {
      padding: 40px;
      text-align: center;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }

    h1 {
      color: #333;
    }

    p {
      color: #666;
    }
  `]
})
export class EtudiantComponent {}
