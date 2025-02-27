export class Project {
    id: number;
    title: string;
    field: string;
    startDate: Date;
    endDate: Date;
    archived: boolean;
    companyAddress: string;
    companyEmail: string;
    companyName: string;
    companyPhone: string;
    descriptionFilePath: string;
    numberOfPositions: number;
    professionalSupervisor: string;
    requiredSkills: string;
  
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