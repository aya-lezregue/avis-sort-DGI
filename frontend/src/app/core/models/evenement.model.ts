import { Cab } from './cab.model';

export type EvenementType = 'EN_ATTENTE' | 'DISTRIBUE' | 'RETOURNE' | 'ECHEC';

export interface EvenementCab {
  id: number;
  cab: any;
  evenement: EvenementType;
  dateEvenement: string;
}

export interface CabDetailResponse {
  cab: Cab;
  historique: EvenementCab[];
  etatActuel: string;
}
