export type CurrentSession = {
  userId: number | null;
  preferredUsername: string | null;
  fullName: string | null;
  email: string | null;
  roleCode: string;
  scopeType: string;
  permissionLevel: string;
  capabilities: string[];
  boardId: number | null;
  schoolId: number | null;
};

export type Role = {
  id: number;
  code: string;
  displayNameNl: string;
  descriptionNl: string;
  scopeType: string;
  permissionLevel: string;
  systemRole: boolean;
  active: boolean;
  capabilityCodes: string[];
};

export type Capability = {
  id: number;
  code: string;
  displayNameNl: string;
  descriptionNl: string;
  active: boolean;
};

export type Assignment = {
  id: number;
  userId: number;
  roleCode: string;
  scopeType: string;
  boardId: number | null;
  schoolId: number | null;
  permissionLevel: string;
  active: boolean;
  validFrom: string | null;
  validTo: string | null;
};

export type ScoreConfiguration = {
  id: number;
  code: string;
  numericValue: number;
  displayLabelNl: string;
  descriptionNl: string;
  sortOrder: number;
  active: boolean;
};

export type InspectionDomain = {
  id: number;
  code: string;
  displayNameNl: string;
  descriptionNl: string;
  sortOrder: number;
  active: boolean;
};

export type InspectionTopic = {
  id: number;
  domainId: number;
  code: string;
  displayNameNl: string;
  descriptionNl: string;
  sortOrder: number;
  active: boolean;
};

export type DocumentTypeDefinition = {
  id: number;
  code: string;
  displayNameNl: string;
  descriptionNl: string;
  active: boolean;
  requiredForOnboarding: boolean;
};

export type AdminSnapshot = {
  session: CurrentSession;
  roles: Role[];
  capabilities: Capability[];
  assignments: Assignment[];
  inspectionDomains: InspectionDomain[];
  inspectionTopics: InspectionTopic[];
  documentTypes: DocumentTypeDefinition[];
  scoreConfigurations: ScoreConfiguration[];
};
