import dayjs from 'dayjs/esm';
import { ILocation } from 'app/entities/location/location.model';

export interface ICustomer {
  id: number;
  nr?: number | null;
  lastName?: string | null;
  firstName?: string | null;
  from?: dayjs.Dayjs | null;
  location?: Pick<ILocation, 'id'> | null;
}

export type NewCustomer = Omit<ICustomer, 'id'> & { id: null };
