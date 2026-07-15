import csv
import os
import shutil
import logging
import psycopg2
from datetime import datetime

# ─────────────────────────────────────────────
#  CHEMINS
# ─────────────────────────────────────────────
BASE_DIR = os.path.dirname(os.path.abspath(__file__))   # REP/IN
REP_DIR  = os.path.dirname(BASE_DIR)                     # REP
ARCHIVE  = os.path.join(REP_DIR, "ARCHIVE")
LOG_DIR  = os.path.join(REP_DIR, "LOG")

# ─────────────────────────────────────────────
#  CONFIGURATION BASE DE DONNÉES
# ─────────────────────────────────────────────
DB_CONFIG = {
    "host":     "localhost",
    "port":     5432,
    "database": "avis_sort_db",
    "user":     "postgres",
    "password": "postgres"
}

# ─────────────────────────────────────────────
#  LOGGING
# ─────────────────────────────────────────────
log_filename = os.path.join(LOG_DIR, f"traitement_{datetime.now().strftime('%Y%m%d_%H%M%S')}.log")
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s",
    handlers=[
        logging.FileHandler(log_filename, encoding="utf-8"),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger(__name__)

# ─────────────────────────────────────────────
#  COLONNES
# ─────────────────────────────────────────────
COLONNES = [
    "_vide",               # col 0 — vide
    "_job",                # col 1 — $$job$$ ignorée
    "numero_cab",          # col 2
    "bureau_distribution", # col 3
    "nom_destinataire",    # col 4
    "adresse",             # col 5
    "numero_sequentiel",   # col 6
    "identifiant_fiscal",  # col 7
    "date_echeance"        # col 8
]

COLS_IGNOREES = {"_vide", "_job"}
COLONNE_CLE   = "numero_cab"


def traiter_et_injecter(chemin_fichier: str):
    nom_fichier = os.path.basename(chemin_fichier)
    logger.info(f"=== Debut traitement : {nom_fichier} ===")

    lignes_lues     = 0
    lignes_inserees = 0
    lignes_doublons = 0
    lignes_ignorees = 0
    vus             = set()

    conn   = None
    cursor = None

    try:
        # ── Connexion à PostgreSQL ───────────────────────
        conn   = psycopg2.connect(**DB_CONFIG)
        cursor = conn.cursor()
        logger.info("  Connexion PostgreSQL OK")

        # ── Lecture du fichier CSV ───────────────────────
        with open(chemin_fichier, encoding="utf-8", errors="replace") as f:
            reader = csv.reader(f, delimiter="|")
            for ligne in reader:
                lignes_lues += 1

                # Ligne trop courte
                if len(ligne) < len(COLONNES):
                    lignes_ignorees += 1
                    logger.warning(f"  Ligne {lignes_lues} ignoree (colonnes insuffisantes)")
                    continue

                row = dict(zip(COLONNES, ligne))
                cle = row[COLONNE_CLE].strip()

                # Numéro CAB vide
                if not cle:
                    lignes_ignorees += 1
                    logger.warning(f"  Ligne {lignes_lues} ignoree (numero_cab vide)")
                    continue

                # Doublon dans le fichier
                if cle in vus:
                    lignes_doublons += 1
                    logger.warning(f"  Doublon dans fichier — numero_cab={cle} (ligne {lignes_lues})")
                    continue

                vus.add(cle)

                # Doublon en base
                cursor.execute("SELECT id FROM cab WHERE numero_cab = %s", (cle,))
                if cursor.fetchone():
                    lignes_doublons += 1
                    logger.warning(f"  Deja en base — numero_cab={cle}")
                    continue

                # Parser la date
                try:
                    date_echeance = datetime.strptime(
                        row["date_echeance"].strip(), "%d/%m/%Y"
                    ).date()
                except ValueError:
                    date_echeance = None
                    logger.warning(f"  Date invalide pour numero_cab={cle}")

                # Insertion dans PostgreSQL
                cursor.execute("""
                    INSERT INTO cab (
                        numero_cab,
                        bureau_distribution,
                        nom_destinataire,
                        adresse,
                        numero_sequentiel,
                        identifiant_fiscal,
                        date_echeance,
                        date_import,
                        flag_ips,
                        statut
                    ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                """, (
                    cle,
                    row["bureau_distribution"].strip(),
                    row["nom_destinataire"].strip(),
                    row["adresse"].strip(),
                    row["numero_sequentiel"].strip(),
                    row["identifiant_fiscal"].strip(),
                    date_echeance,
                    datetime.now(),
                    "NON_EXISTE",
                    "EN_ATTENTE"
                ))
                lignes_inserees += 1

        # ── Commit ──────────────────────────────────────
        conn.commit()
        logger.info(f"  Commit effectue — {lignes_inserees} lignes inserees en base")

        # ── Archivage du fichier ORIGINAL ────────────────
        chemin_archive = os.path.join(ARCHIVE, nom_fichier)
        shutil.move(chemin_fichier, chemin_archive)
        logger.info(f"  Fichier original archive : {chemin_archive}")

        # ── Résumé ───────────────────────────────────────
        logger.info(f"  Lignes lues      : {lignes_lues}")
        logger.info(f"  Lignes inserees  : {lignes_inserees}")
        logger.info(f"  Doublons ignores : {lignes_doublons}")
        logger.info(f"  Lignes ignorees  : {lignes_ignorees}")
        logger.info(f"=== Traitement termine : {nom_fichier} ===")

    except psycopg2.Error as e:
        logger.error(f"  Erreur PostgreSQL : {e}")
        if conn:
            conn.rollback()
            logger.error("  Rollback effectue — aucune donnee inseree")
    except Exception as e:
        logger.error(f"  Erreur inattendue : {e}")
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()
            logger.info("  Connexion PostgreSQL fermee")


def main():
    fichiers = [
        os.path.join(BASE_DIR, f)
        for f in os.listdir(BASE_DIR)
        if f.endswith(".csv")
    ]

    if not fichiers:
        logger.info("Aucun fichier CSV trouve dans IN/")
        return

    for fichier in fichiers:
        traiter_et_injecter(fichier)


if __name__ == "__main__":
    main()