import dayjs from 'dayjs/esm';
import { IEmployee } from 'app/entities/employee/employee.model';

export interface IRoute {
  id: number;
  code?: string | null;
  description?: string | null;
  date?: dayjs.Dayjs | null;
  employee?: Pick<IEmployee, 'id' | 'lastName'> | null;
}

export type NewRoute = Omit<IRoute, 'id'> & { id: null };
