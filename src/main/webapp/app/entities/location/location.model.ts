import { ICustomer } from 'app/entities/customer/customer.model';
import { IRouteStop } from 'app/entities/route-stop/route-stop.model';

export interface ILocation {
  id: number;
  streetAddress?: string | null;
  postalCode?: string | null;
  city?: string | null;
  stateProvince?: string | null;
  customers?: ICustomer[] | null;
  routeStops?: IRouteStop[] | null;
}

export type NewLocation = Omit<ILocation, 'id'> & { id: null };
