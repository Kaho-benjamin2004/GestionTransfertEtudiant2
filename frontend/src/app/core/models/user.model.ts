export interface User {
  id: string;
  email: string;
  nom: string;
  prenom: string;
  roles: string[];
  dateCreation?: Date;
  derniereConnexion?: Date;
  actif: boolean;
}

export interface Etudiant extends User {
  dateNaissance?: Date;
  numeroMatricule: string;
  universite?: string;
  faculte?: string;
  departement?: string;
  specialite?: string;
}

export interface Agent extends User {
  universite?: string;
  designation?: string;
}

export interface Admin extends User {
  permissions?: string[];
}
