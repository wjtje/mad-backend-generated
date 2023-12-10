import { IRouteStop, NewRouteStop } from './route-stop.model';

export const sampleWithRequiredData: IRouteStop = {
  id: 4473,
};

export const sampleWithPartialData: IRouteStop = {
  id: 23157,
};

export const sampleWithFullData: IRouteStop = {
  id: 4041,
  nr: 13343,
};

export const sampleWithNewData: NewRouteStop = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
