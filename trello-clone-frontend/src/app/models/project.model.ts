export interface Project {
  id: number;
  name: string;
  description?: string;
  status: ProjectStatus;
  owner: User;
  members: User[];
  createdAt: Date;
  updatedAt: Date;
}

export enum ProjectStatus {
  ACTIVE = 'ACTIVE',
  COMPLETED = 'COMPLETED',
  ARCHIVED = 'ARCHIVED'
}

export interface ProjectRequest {
  name: string;
  description?: string;
}

import { User } from './user.model';
