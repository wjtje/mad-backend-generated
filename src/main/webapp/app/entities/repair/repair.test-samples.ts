import dayjs from 'dayjs/esm';

import { IRepair, NewRepair } from './repair.model';

export const sampleWithRequiredData: IRepair = {
  id: 2586,
};

export const sampleWithPartialData: IRepair = {
  id: 30077,
  dateCompleted: dayjs('2023-12-06'),
};

export const sampleWithFullData: IRepair = {
  id: 30747,
  description: 'noisily',
  repairStatus: 'PLANNED',
  dateCompleted: dayjs('2023-12-07'),
};

export const sampleWithNewData: NewRepair = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
