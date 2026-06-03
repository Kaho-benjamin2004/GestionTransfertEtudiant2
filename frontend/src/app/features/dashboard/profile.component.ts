import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { UserInfo, ChangePasswordRequest } from '../../core/models';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
  currentUser: UserInfo | null = null;
  changePasswordForm!: FormGroup;
  submitted = false;
  loading = false;
  successMessage = '';
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private formBuilder: FormBuilder
  ) {}

  ngOnInit(): void {
    this.initializeForm();
    this.authService.getCurrentUser$().subscribe((user: UserInfo | null) => {
      this.currentUser = user;
    });
  }

  private initializeForm(): void {
    this.changePasswordForm = this.formBuilder.group(
      {
        currentPassword: ['', [Validators.required, Validators.minLength(6)]],
        newPassword: ['', [Validators.required, Validators.minLength(8)]],
        confirmPassword: ['', Validators.required]
      },
      { validators: this.passwordMatchValidator }
    );
  }

  passwordMatchValidator(group: FormGroup): { [key: string]: any } | null {
    const newPassword = group.get('newPassword');
    const confirmPassword = group.get('confirmPassword');

    if (!newPassword || !confirmPassword) {
      return null;
    }

    return newPassword.value === confirmPassword.value
      ? null
      : { passwordMismatch: true };
  }

  get f() {
    return this.changePasswordForm.controls;
  }

  onSubmit(): void {
    this.submitted = true;
    this.errorMessage = '';
    this.successMessage = '';

    if (this.changePasswordForm.invalid) {
      return;
    }

    this.loading = true;
    const request: ChangePasswordRequest = {
      currentPassword: this.f['currentPassword'].value,
      newPassword: this.f['newPassword'].value,
      confirmPassword: this.f['confirmPassword'].value
    };

    this.authService.changePassword(request).subscribe({
      next: (response: any) => {
        this.loading = false;
        if (response.success) {
          this.successMessage = 'Mot de passe changé avec succès';
          this.changePasswordForm.reset();
          this.submitted = false;
        }
      },
      error: (error: any) => {
        this.loading = false;
        this.errorMessage = error.error?.message || 'Erreur lors du changement de mot de passe';
      }
    });
  }
}
