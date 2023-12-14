import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IRoute, NewRoute } from '../route.model';

export type PartialUpdateRoute = Partial<IRoute> & Pick<IRoute, 'id'>;

type RestOf<T extends IRoute | NewRoute> = Omit<T, 'date'> & {
  date?: string | null;
};

export type RestRoute = RestOf<IRoute>;

export type NewRestRoute = RestOf<NewRoute>;

export type PartialUpdateRestRoute = RestOf<PartialUpdateRoute>;

export type EntityResponseType = HttpResponse<IRoute>;
export type EntityArrayResponseType = HttpResponse<IRoute[]>;

@Injectable({ providedIn: 'root' })
export class RouteService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/routes');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(route: NewRoute): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(route);
    return this.http.post<RestRoute>(this.resourceUrl, copy, { observe: 'response' }).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(route: IRoute): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(route);
    return this.http
      .put<RestRoute>(`${this.resourceUrl}/${this.getRouteIdentifier(route)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(route: PartialUpdateRoute): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(route);
    return this.http
      .patch<RestRoute>(`${this.resourceUrl}/${this.getRouteIdentifier(route)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestRoute>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestRoute[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getRouteIdentifier(route: Pick<IRoute, 'id'>): number {
    return route.id;
  }

  compareRoute(o1: Pick<IRoute, 'id'> | null, o2: Pick<IRoute, 'id'> | null): boolean {
    return o1 && o2 ? this.getRouteIdentifier(o1) === this.getRouteIdentifier(o2) : o1 === o2;
  }

  addRouteToCollectionIfMissing<Type extends Pick<IRoute, 'id'>>(
    routeCollection: Type[],
    ...routesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const routes: Type[] = routesToCheck.filter(isPresent);
    if (routes.length > 0) {
      const routeCollectionIdentifiers = routeCollection.map(routeItem => this.getRouteIdentifier(routeItem)!);
      const routesToAdd = routes.filter(routeItem => {
        const routeIdentifier = this.getRouteIdentifier(routeItem);
        if (routeCollectionIdentifiers.includes(routeIdentifier)) {
          return false;
        }
        routeCollectionIdentifiers.push(routeIdentifier);
        return true;
      });
      return [...routesToAdd, ...routeCollection];
    }
    return routeCollection;
  }

  protected convertDateFromClient<T extends IRoute | NewRoute | PartialUpdateRoute>(route: T): RestOf<T> {
    return {
      ...route,
      date: route.date?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restRoute: RestRoute): IRoute {
    return {
      ...restRoute,
      date: restRoute.date ? dayjs(restRoute.date) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestRoute>): HttpResponse<IRoute> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestRoute[]>): HttpResponse<IRoute[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
