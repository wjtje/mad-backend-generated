import dayjs from 'dayjs/esm';
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
  car?: Pick<ICar, 'id' | 'licensePlate'> | null;
  employee?: Pick<IEmployee, 'id' | 'lastName'> | null;
  rental?: Pick<IRental, 'id'> | null;
}

export type NewInspection = Omit<IInspection, 'id'> & { id: null };
