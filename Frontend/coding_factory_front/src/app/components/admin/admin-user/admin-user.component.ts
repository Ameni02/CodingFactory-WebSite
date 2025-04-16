import { Component, OnInit } from '@angular/core';
import { AdminControllerService } from 'src/app/services/services/admin-controller.service';
import { User } from 'src/app/services/models/user';
import {Role} from "../../../services/models/role";
import { HttpErrorResponse } from '@angular/common/http';
import { ConfirmationService, MessageService } from 'primeng/api';

// No changes at the top (imports remain the same)

@Component({
  selector: 'app-admin-users',
  templateUrl: './admin-user.component.html',
  styleUrls: ['./admin-user.component.css'],
  providers: [ConfirmationService, MessageService]
})
export class AdminUsersComponent implements OnInit {
  users: User[] = [];
  filteredUsers: User[] = [];
  errorMsg: string[] = [];
  loading = false;

  // Updated Statistics
  userStats = {
    totalUsers: 0,
    activeUsers: 0,
    lockedUsers: 0,
    admins: 0,
    regularUsers: 0,
    bannedUsers: 0,         // users where enabled === false
    notEnabledUsers: 0      // same as above for your case
  };

  // Filters
  searchTerm = '';
  statusFilter: string | null = null;
  roleFilter: string | null = null;
  banStatusFilter: string | null = null;
  enabledFilter: string | null = null;

  // Pagination
  currentPage = 1;
  itemsPerPage = 10;

  // Role management
  availableRoles: any[] = [
    { name: 'ADMIN', label: 'Admin' },
    { name: 'USER', label: 'User' }
  ];
  selectedUser: User | null = null;
  selectedRoles: any[] = [];
  showRoleDialog = false;

  constructor(
    private adminService: AdminControllerService,
    private confirmationService: ConfirmationService,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {
    this.fetchUsers();
  }

  fetchUsers(): void {
    this.loading = true;
    this.errorMsg = [];

    this.adminService.getAllUsers().subscribe({
      next: (data: User[]) => {
        this.users = data;
        this.filteredUsers = [...data];
        this.calculateStats();
        this.loading = false;
      },
      error: (err: HttpErrorResponse) => {
        this.handleError(err);
        this.loading = false;
      }
    });
  }

  calculateStats(): void {
    this.userStats = {
      totalUsers: this.users.length,
      activeUsers: this.users.filter(u => !u.accountLocked).length,
      lockedUsers: this.users.filter(u => u.accountLocked).length,
      admins: this.users.filter(u => this.hasRole(u, 'ADMIN')).length,
      regularUsers: this.users.filter(u => !this.hasRole(u, 'ADMIN')).length,
      bannedUsers: this.users.filter(u => !u.enabled).length,      // enabled === false => banned
      notEnabledUsers: this.users.filter(u => !u.enabled).length   // same logic
    };
  }

  hasRole(user: User, roleName: string): boolean {
    return user.roles?.some(role => role.name === roleName) || false;
  }

  applyFilters(): void {
    this.filteredUsers = this.users.filter(user => {
      const matchesSearch =
        user.firstname?.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        user.lastname?.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        user.email?.toLowerCase().includes(this.searchTerm.toLowerCase());

      const matchesStatus =
        this.statusFilter === null ||
        (this.statusFilter === 'active' && !user.accountLocked) ||
        (this.statusFilter === 'locked' && user.accountLocked);

      const matchesRole =
        this.roleFilter === null ||
        (this.roleFilter === 'ADMIN' && this.hasRole(user, 'ADMIN')) ||
        (this.roleFilter === 'USER' && !this.hasRole(user, 'ADMIN'));

      const matchesBanStatus =
        this.banStatusFilter === null ||
        (this.banStatusFilter === 'notBanned' && user.enabled) ||
        (this.banStatusFilter === 'banned' && !user.enabled);

      const matchesEnabledStatus =
        this.enabledFilter === null ||
        (this.enabledFilter === 'enabled' && user.enabled) ||
        (this.enabledFilter === 'notEnabled' && !user.enabled);

      return matchesSearch && matchesStatus && matchesRole && matchesBanStatus && matchesEnabledStatus;
    });

    this.currentPage = 1;
  }

  resetFilters(): void {
    this.searchTerm = '';
    this.statusFilter = null;
    this.roleFilter = null;
    this.banStatusFilter = null;
    this.enabledFilter = null;
    this.applyFilters();
  }

  lockUser(user: User): void {
    if (!user.id) return;

    this.confirmationService.confirm({
      message: `Are you sure you want to ${user.accountLocked ? 'unlock' : 'lock'} ${user.firstname} ${user.lastname}?`,
      header: 'Confirm Action',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        user.accountLocked = !user.accountLocked;
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: `User ${user.accountLocked ? 'locked' : 'unlocked'} successfully!`
        });
        this.calculateStats();
        this.applyFilters();
      }
    });
  }

  banUser(user: User): void {
    if (!user.id) return;

    this.confirmationService.confirm({
      message: `Are you sure you want to ${user.enabled ? 'ban' : 'unban'} ${user.firstname} ${user.lastname}?`,
      header: 'Confirm Action',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        const action = user.enabled
          ? this.adminService.banUser(user.id!)
          : this.adminService.unbanUser(user.id!);

        action.subscribe({
          next: () => {
            user.enabled = !user.enabled;
            this.messageService.add({
              severity: 'success',
              summary: 'Success',
              detail: `User ${user.enabled ? 'unbanned' : 'banned'} successfully!`
            });
            this.calculateStats();
            this.applyFilters();
          },
          error: (err) => this.handleError(err)
        });
      }
    });
  }

  deleteUser(user: User): void {
    if (!user.id) return;

    this.confirmationService.confirm({
      message: `Are you sure you want to permanently delete ${user.firstname} ${user.lastname}?`,
      header: 'Confirm Deletion',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.adminService.deleteUser(user.id!).subscribe({
          next: () => {
            this.messageService.add({
              severity: 'success',
              summary: 'Success',
              detail: 'User deleted successfully!'
            });
            this.fetchUsers();
          },
          error: (err) => this.handleError(err)
        });
      }
    });
  }

  openRoleDialog(user: User): void {
    this.selectedUser = user;
    this.selectedRoles = this.availableRoles.filter(role =>
      user.roles?.some(userRole => userRole.name === role.name)
    );
    this.showRoleDialog = true;
  }

  saveRoles(): void {
    if (!this.selectedUser?.id) return;

    this.selectedUser.roles = this.selectedRoles.map(role => ({ name: role.name }));
    this.messageService.add({
      severity: 'success',
      summary: 'Success',
      detail: 'User roles updated successfully!'
    });
    this.showRoleDialog = false;
    this.calculateStats();
    this.applyFilters();
  }

  handleError(err: HttpErrorResponse): void {
    let errorMessage = 'An error occurred. Please try again.';
    if (err.status === 403) {
      errorMessage = 'Access denied. You do not have permission.';
    } else if (err.status === 401) {
      errorMessage = 'Unauthorized access. Please log in again.';
    } else if (err.error?.message) {
      errorMessage = err.error.message;
    }

    this.messageService.add({
      severity: 'error',
      summary: 'Error',
      detail: errorMessage,
      life: 5000
    });
  }

  getRoleNames(user: User): string {
    return user.roles?.map(role => role.name).join(', ') || 'USER';
  }

  get paginatedUsers(): User[] {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    return this.filteredUsers.slice(startIndex, startIndex + this.itemsPerPage);
  }

  get totalPages(): number {
    return Math.ceil(this.filteredUsers.length / this.itemsPerPage);
  }

  changePage(page: number): void {
    this.currentPage = page;
  }

  min(a: number, b: number): number {
    return Math.min(a, b);
  }
}
