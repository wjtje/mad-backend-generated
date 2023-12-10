import { IRoute } from 'app/entities/route/route.model';
import { ILocation } from 'app/entities/location/location.model';

export interface IRouteStop {
  id: number;
  nr?: number | null;
  route?: Pick<IRoute, 'id'> | null;
  location?: Pick<ILocation, 'id'> | null;
}

export type NewRouteStop = Omit<IRouteStop, 'id'> & { id: null };
