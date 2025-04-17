/**
 * Configuration for the PfeSpace module
 */
export const PfeSpaceConfig = {
  // API endpoints
  apiUrl: 'http://localhost:8080/pfespace/api/pfe',
  
  // Status options
  projectStatus: {
    PENDING: 'PENDING',
    IN_PROGRESS: 'IN_PROGRESS',
    COMPLETED: 'COMPLETED'
  },
  
  applicationStatus: {
    PENDING: 'PENDING',
    ACCEPTED: 'ACCEPTED',
    REJECTED: 'REJECTED'
  },
  
  deliverableStatus: {
    PENDING: 'PENDING',
    EVALUATED: 'EVALUATED',
    REJECTED: 'REJECTED'
  },
  
  // File upload settings
  fileUpload: {
    maxFileSize: 10485760, // 10MB
    allowedFileTypes: ['application/pdf', 'image/jpeg', 'image/png']
  },
  
  // UI settings
  ui: {
    primaryColor: '#7B6ADA', // As per user preference
    secondaryColor: '#e74c3c',
    accentColor: '#2980b9'
  }
};
