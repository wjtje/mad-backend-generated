import dayjs from 'dayjs/esm';
import { IInspectionPhoto } from 'app/entities/inspection-photo/inspection-photo.model';
import { IRepair } from 'app/entities/repair/repair.model';
import { ICar } from 'app/entities/car/car.model';
import { IEmployee } from 'app/entities/employee/employee.model';
import { IRental } from 'app/entities/rental/rental.model';

export interface IInspection {
  id: number;
  code?: string | null;
  odometer?: number | null;
  result?: string | null;
  photo?: string | null;
  photoContentType?: string | null;
  completed?: dayjs.Dayjs | null;
  photos?: IInspectionPhoto[] | null;
  repairs?: IRepair[] | null;
  car?: ICar | null;
  employee?: IEmployee | null;
  rental?: IRental | null;
}

export type NewInspection = Omit<IInspection, 'id'> & { id: null };
