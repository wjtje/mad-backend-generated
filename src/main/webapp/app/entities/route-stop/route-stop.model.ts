import { IRoute } from 'app/entities/route/route.model';
import { ILocation } from 'app/entities/location/location.model';

export interface IRouteStop {
  id: number;
  nr?: number | null;
  route?: IRoute | null;
  location?: ILocation | null;
}

export type NewRouteStop = Omit<IRouteStop, 'id'> & { id: null };
