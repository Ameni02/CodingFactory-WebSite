export class Project {
  id: number = 0; // Default value (will be ignored by the backend)
  title: string = '';
  field: string = '';
  requiredSkills: string = '';
  descriptionFilePath: string = '';
  numberOfPositions: number = 0;
  startDate: Date = new Date(); // Use Date instead of string
  endDate: Date = new Date(); // Use Date instead of string
  companyName: string = '';
  professionalSupervisor: string = '';
  companyAddress: string = '';
  companyEmail: string = '';
  companyPhone: string = '';
  archived: boolean = false;

  
    // Constructor
    constructor(
      id: number = 0, // Default value for new projects
      title: string = '',
      field: string = '',
      startDate: Date = new Date(),
      endDate: Date = new Date(),
      archived: boolean = false,
      companyAddress: string = '',
      companyEmail: string = '',
      companyName: string = '',
      companyPhone: string = '',
      descriptionFilePath: string = '',
      numberOfPositions: number = 0,
      professionalSupervisor: string = '',
      requiredSkills: string = ''
    ) {
      this.id = id;
      this.title = title;
      this.field = field;
      this.startDate = startDate;
      this.endDate = endDate;
      this.archived = archived;
      this.companyAddress = companyAddress;
      this.companyEmail = companyEmail;
      this.companyName = companyName;
      this.companyPhone = companyPhone;
      this.descriptionFilePath = descriptionFilePath;
      this.numberOfPositions = numberOfPositions;
      this.professionalSupervisor = professionalSupervisor;
      this.requiredSkills = requiredSkills;
    }
  }