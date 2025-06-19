export interface Label {
  id: number;
  name: string;
  color: string;
  projectId: number;
  createdAt: Date;
  updatedAt: Date;
}

export interface LabelRequest {
  name: string;
  color: string;
}
