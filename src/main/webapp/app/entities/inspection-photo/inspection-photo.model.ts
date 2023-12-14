import { IInspection } from 'app/entities/inspection/inspection.model';

export interface IInspectionPhoto {
  id: number;
  photo?: string | null;
  photoContentType?: string | null;
  inspection?: Pick<IInspection, 'id'> | null;
}

export type NewInspectionPhoto = Omit<IInspectionPhoto, 'id'> & { id: null };
