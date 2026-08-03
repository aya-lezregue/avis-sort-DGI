export interface Cab {
  id: number;
  numeroCab: string;
  bureauDistribution: string;
  nomDestinataire: string;
  adresse: string;
  numeroSequentiel: string;
  identifiantFiscal: string;
  dateEcheance: string;
  dateImport: string;
  dateLivraison: string | null;
  flagIps: 'EXISTE' | 'NON_EXISTE' | 'EXCEPTION';
  statut: string;
}

export interface CsvImportResult {
  nomFichier: string;
  lignesLues: number;
  lignesInserees: number;
  lignesDoublons: number;
  lignesIgnorees: number;
}