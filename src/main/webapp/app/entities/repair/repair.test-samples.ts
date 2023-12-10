import dayjs from 'dayjs/esm';

import { IRepair, NewRepair } from './repair.model';

export const sampleWithRequiredData: IRepair = {
  id: 13372,
};

export const sampleWithPartialData: IRepair = {
  id: 6236,
  repairStatus: 'CANCELLED',
};

export const sampleWithFullData: IRepair = {
  id: 30053,
  description: 'unimpressively',
  repairStatus: 'DOING',
  dateCompleted: dayjs('2023-12-06'),
};

export const sampleWithNewData: NewRepair = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
