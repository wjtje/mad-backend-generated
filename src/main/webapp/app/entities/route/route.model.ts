import dayjs from 'dayjs/esm';
import { IRouteStop } from 'app/entities/route-stop/route-stop.model';
import { IEmployee } from 'app/entities/employee/employee.model';

export interface IRoute {
  id: number;
  code?: string | null;
  description?: string | null;
  date?: dayjs.Dayjs | null;
  routeStops?: IRouteStop[] | null;
  employee?: IEmployee | null;
}

export type NewRoute = Omit<IRoute, 'id'> & { id: null };
