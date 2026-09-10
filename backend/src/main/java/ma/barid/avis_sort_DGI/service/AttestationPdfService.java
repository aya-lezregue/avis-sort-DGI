package ma.barid.avis_sort_DGI.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import ma.barid.avis_sort_DGI.entity.Cab;
import ma.barid.avis_sort_DGI.entity.EvenementCab;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AttestationPdfService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final Color BLEU_BARID = new Color(0, 51, 153);
    private static final Color BLEU_CLAIR = new Color(244, 247, 252);
    private static final Color GRIS_BORDURE = new Color(225, 229, 235);

    public byte[] genererAttestation(Cab cab, List<EvenementCab> historique) throws Exception {

        Document document = new Document(PageSize.A4, 45, 45, 40, 50);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        document.open();

        Font titleFont = new Font(Font.HELVETICA, 15, Font.BOLD, BLEU_BARID);
        Font subTitleFont = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.GRAY);
        Font docTitleFont = new Font(Font.HELVETICA, 17, Font.BOLD, Color.BLACK);
        Font labelFont = new Font(Font.HELVETICA, 9.5f, Font.BOLD, new Color(90, 100, 115));
        Font valueFont = new Font(Font.HELVETICA, 11, Font.NORMAL, Color.BLACK);
        Font statusFont = new Font(Font.HELVETICA, 15, Font.BOLD, Color.WHITE);
        Font histHeaderFont = new Font(Font.HELVETICA, 9.5f, Font.BOLD, BLEU_BARID);
        Font footerFont = new Font(Font.HELVETICA, 8, Font.ITALIC, Color.GRAY);

        // ===== EN-TETE AVEC LOGOS =====
        PdfPTable headerTable = new PdfPTable(3);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{1, 2, 1});

        // Logo Poste (gauche)
        PdfPCell logoPosteCell = new PdfPCell();
        logoPosteCell.setBorder(Rectangle.NO_BORDER);
        logoPosteCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        logoPosteCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        try {
            Image logoPoste = chargerImage("/static/images/logo-poste.png");
            logoPoste.scaleToFit(75, 55);
            logoPosteCell.addElement(logoPoste);
        } catch (Exception e) {
            logoPosteCell.addElement(new Phrase(""));
        }
        headerTable.addCell(logoPosteCell);

        // Titre central
        PdfPCell titleCell = new PdfPCell();
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        Paragraph titreP = new Paragraph("BARID AL MAGHRIB", titleFont);
        titreP.setAlignment(Element.ALIGN_CENTER);
        Paragraph sousTitreP = new Paragraph("Direction Generale des Impots — Avis de Sort", subTitleFont);
        sousTitreP.setAlignment(Element.ALIGN_CENTER);
        titleCell.addElement(titreP);
        titleCell.addElement(sousTitreP);
        headerTable.addCell(titleCell);

        // Logo DGI (droite)
        PdfPCell logoDgiCell = new PdfPCell();
        logoDgiCell.setBorder(Rectangle.NO_BORDER);
        logoDgiCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        logoDgiCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        try {
            Image logoDgi = chargerImage("/static/images/logo-dgi.png");
            logoDgi.scaleToFit(75, 55);
            logoDgiCell.addElement(logoDgi);
        } catch (Exception e) {
            logoDgiCell.addElement(new Phrase(""));
        }
        headerTable.addCell(logoDgiCell);

        document.add(headerTable);

        // Ligne de separation coloree sous l'en-tete
        PdfPTable ligneSep = new PdfPTable(1);
        ligneSep.setWidthPercentage(100);
        PdfPCell sepCell = new PdfPCell();
        sepCell.setFixedHeight(3f);
        sepCell.setBackgroundColor(BLEU_BARID);
        sepCell.setBorder(Rectangle.NO_BORDER);
        ligneSep.addCell(sepCell);
        ligneSep.setSpacingBefore(8);
        ligneSep.setSpacingAfter(20);
        document.add(ligneSep);

        // Titre du document
        Paragraph docTitle = new Paragraph("ATTESTATION DE SORT", docTitleFont);
        docTitle.setAlignment(Element.ALIGN_CENTER);
        docTitle.setSpacingAfter(4);
        document.add(docTitle);

        Paragraph numCabSub = new Paragraph("N° " + cab.getNumeroCab(), subTitleFont);
        numCabSub.setAlignment(Element.ALIGN_CENTER);
        numCabSub.setSpacingAfter(22);
        document.add(numCabSub);

        // ===== TABLEAU D'INFORMATIONS =====
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 2});

        addRow(table, "Numero CAB", cab.getNumeroCab(), labelFont, valueFont, true);
        addRow(table, "Destinataire", cab.getNomDestinataire(), labelFont, valueFont, false);
        addRow(table, "Bureau de distribution", cab.getBureauDistribution(), labelFont, valueFont, true);
        addRow(table, "Adresse", cab.getAdresse(), labelFont, valueFont, false);
        addRow(table, "Numero sequentiel", cab.getNumeroSequentiel(), labelFont, valueFont, true);
        addRow(table, "Identifiant fiscal", cab.getIdentifiantFiscal(), labelFont, valueFont, false);
        addRow(table, "Date d'echeance",
                cab.getDateEcheance() != null ? cab.getDateEcheance().format(DATE_FMT) : "-",
                labelFont, valueFont, true);
        if (cab.getDateLivraison() != null) {
            addRow(table, "Date de livraison", cab.getDateLivraison().format(DATETIME_FMT), labelFont, valueFont, false);
        }

        document.add(table);
        document.add(new Paragraph(" "));

        // ===== CADRE ETAT DU SORT =====
        PdfPTable statusTable = new PdfPTable(1);
        statusTable.setWidthPercentage(100);
        PdfPCell statusCell = new PdfPCell(new Phrase("ETAT DU SORT : " + traduireStatut(cab.getStatut()), statusFont));
        statusCell.setBackgroundColor(getColorForStatut(cab.getStatut()));
        statusCell.setPadding(16);
        statusCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        statusCell.setBorder(Rectangle.NO_BORDER);
        statusTable.addCell(statusCell);
        statusTable.setSpacingBefore(6);
        statusTable.setSpacingAfter(28);
        document.add(statusTable);

        // ===== HISTORIQUE =====
        if (historique != null && !historique.isEmpty()) {
            Paragraph histTitle = new Paragraph("Historique des evenements", new Font(Font.HELVETICA, 12, Font.BOLD, BLEU_BARID));
            histTitle.setSpacingAfter(10);
            document.add(histTitle);

            PdfPTable histTable = new PdfPTable(2);
            histTable.setWidthPercentage(100);
            histTable.setWidths(new float[]{1, 1});

            PdfPCell h1 = new PdfPCell(new Phrase("Evenement", histHeaderFont));
            PdfPCell h2 = new PdfPCell(new Phrase("Date", histHeaderFont));
            h1.setBackgroundColor(BLEU_CLAIR);
            h2.setBackgroundColor(BLEU_CLAIR);
            h1.setPadding(8);
            h2.setPadding(8);
            h1.setBorderColor(GRIS_BORDURE);
            h2.setBorderColor(GRIS_BORDURE);
            histTable.addCell(h1);
            histTable.addCell(h2);

            boolean alt = false;
            for (EvenementCab evt : historique) {
                Color bg = alt ? new Color(250, 251, 253) : Color.WHITE;
                PdfPCell c1 = new PdfPCell(new Phrase(traduireStatut(evt.getEvenement().name()), valueFont));
                PdfPCell c2 = new PdfPCell(new Phrase(evt.getDateEvenement().format(DATETIME_FMT), valueFont));
                c1.setPadding(8);
                c2.setPadding(8);
                c1.setBackgroundColor(bg);
                c2.setBackgroundColor(bg);
                c1.setBorderColor(GRIS_BORDURE);
                c2.setBorderColor(GRIS_BORDURE);
                histTable.addCell(c1);
                histTable.addCell(c2);
                alt = !alt;
            }
            document.add(histTable);
        }

        // ===== PIED DE PAGE =====
        document.add(new Paragraph(" "));
        PdfPTable footerSep = new PdfPTable(1);
        footerSep.setWidthPercentage(100);
        PdfPCell footerSepCell = new PdfPCell();
        footerSepCell.setFixedHeight(1f);
        footerSepCell.setBackgroundColor(GRIS_BORDURE);
        footerSepCell.setBorder(Rectangle.NO_BORDER);
        footerSep.addCell(footerSepCell);
        footerSep.setSpacingBefore(20);
        footerSep.setSpacingAfter(10);
        document.add(footerSep);

        Paragraph footer = new Paragraph(
                "Document genere automatiquement le " + LocalDateTime.now().format(DATETIME_FMT) +
                        " — Plateforme Avis de Sort DGI — Barid Al Maghrib",
                footerFont
        );
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();
        return baos.toByteArray();
    }

    private Image chargerImage(String cheminClasspath) throws Exception {
        InputStream is = getClass().getResourceAsStream(cheminClasspath);
        if (is == null) {
            throw new IOException("Logo introuvable : " + cheminClasspath);
        }
        byte[] bytes = is.readAllBytes();
        return Image.getInstance(bytes);
    }

    private void addRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont, boolean alt) {
        Color bg = alt ? BLEU_CLAIR : Color.WHITE;

        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(9);
        labelCell.setBackgroundColor(bg);

        PdfPCell valueCell = new PdfPCell(new Phrase(value != null ? value : "-", valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(9);
        valueCell.setBackgroundColor(bg);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private String traduireStatut(String statut) {
        return switch (statut) {
            case "DISTRIBUE" -> "DISTRIBUE";
            case "RETOURNE" -> "RETOURNE";
            case "ECHEC" -> "ECHEC";
            case "EN_ATTENTE" -> "EN ATTENTE";
            default -> statut;
        };
    }

    private Color getColorForStatut(String statut) {
        return switch (statut) {
            case "DISTRIBUE" -> new Color(25, 135, 84);
            case "RETOURNE" -> new Color(230, 162, 0);
            case "ECHEC" -> new Color(220, 53, 69);
            default -> new Color(108, 117, 125);
        };
    }
}