/**
 * Utility functions for the PfeSpace module
 */

/**
 * Format a date string to a human-readable format
 * @param dateString Date string from the API
 * @returns Formatted date string
 */
export function formatDate(dateString: string): string {
  if (!dateString) return 'N/A';
  
  const date = new Date(dateString);
  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  });
}

/**
 * Get a CSS class based on status
 * @param status Status string
 * @returns CSS class name
 */
export function getStatusClass(status: string): string {
  if (!status) return 'status-unknown';
  
  switch (status.toUpperCase()) {
    case 'PENDING':
      return 'status-pending';
    case 'ACCEPTED':
    case 'COMPLETED':
    case 'EVALUATED':
      return 'status-success';
    case 'REJECTED':
      return 'status-danger';
    case 'IN_PROGRESS':
      return 'status-warning';
    default:
      return 'status-unknown';
  }
}

/**
 * Truncate text to a specific length
 * @param text Text to truncate
 * @param maxLength Maximum length
 * @returns Truncated text
 */
export function truncateText(text: string, maxLength: number = 100): string {
  if (!text) return '';
  if (text.length <= maxLength) return text;
  
  return text.substring(0, maxLength) + '...';
}

/**
 * Get file extension from a file path
 * @param filePath File path
 * @returns File extension
 */
export function getFileExtension(filePath: string): string {
  if (!filePath) return '';
  
  const parts = filePath.split('.');
  return parts.length > 1 ? parts[parts.length - 1].toLowerCase() : '';
}

/**
 * Check if a file is a PDF
 * @param filePath File path
 * @returns True if the file is a PDF
 */
export function isPdfFile(filePath: string): boolean {
  return getFileExtension(filePath) === 'pdf';
}

/**
 * Check if a file is an image
 * @param filePath File path
 * @returns True if the file is an image
 */
export function isImageFile(filePath: string): boolean {
  const ext = getFileExtension(filePath);
  return ['jpg', 'jpeg', 'png', 'gif', 'bmp'].includes(ext);
}
