import { IRouteStop, NewRouteStop } from './route-stop.model';

export const sampleWithRequiredData: IRouteStop = {
  id: 15968,
};

export const sampleWithPartialData: IRouteStop = {
  id: 31684,
  nr: 8129,
};

export const sampleWithFullData: IRouteStop = {
  id: 25210,
  nr: 20007,
};

export const sampleWithNewData: NewRouteStop = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
