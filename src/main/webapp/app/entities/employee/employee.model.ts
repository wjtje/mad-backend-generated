import dayjs from 'dayjs/esm';
import { IInspection } from 'app/entities/inspection/inspection.model';
import { IRepair } from 'app/entities/repair/repair.model';
import { IRoute } from 'app/entities/route/route.model';

export interface IEmployee {
  id: number;
  nr?: number | null;
  lastName?: string | null;
  firstName?: string | null;
  from?: dayjs.Dayjs | null;
  inspections?: IInspection[] | null;
  repairs?: IRepair[] | null;
  routes?: IRoute[] | null;
}

export type NewEmployee = Omit<IEmployee, 'id'> & { id: null };
