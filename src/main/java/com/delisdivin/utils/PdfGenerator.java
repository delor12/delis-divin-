package com.delisdivin.utils;

import com.delisdivin.entity.Bill;
import com.delisdivin.entity.OrderItem;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Component
public class PdfGenerator {

    public byte[] generateInvoicePdf(Bill bill) {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Styling colors (Violet theme)
            Color primaryColor = new Color(106, 13, 173); // #6A0DAD
            Color secondaryColor = new Color(100, 100, 100);
            
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, primaryColor);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, primaryColor);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);
            Font regularFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);

            // Document Header
            Paragraph brand = new Paragraph("DELIS DIVIN", titleFont);
            brand.setAlignment(Element.ALIGN_CENTER);
            document.add(brand);

            Paragraph subtitle = new Paragraph(bill.getRestaurant().getName().toUpperCase(), sectionFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            document.add(subtitle);
            
            document.add(new Paragraph(" ")); // Spacer

            // Restaurant & Bill Meta Info Table
            PdfPTable metaTable = new PdfPTable(2);
            metaTable.setWidthPercentage(100);
            
            // Left Cell: Restaurant Details
            PdfPCell leftCell = new PdfPCell();
            leftCell.setBorder(Rectangle.NO_BORDER);
            leftCell.addElement(new Paragraph("Adresse: " + bill.getRestaurant().getAddress(), regularFont));
            leftCell.addElement(new Paragraph("Téléphone: " + bill.getRestaurant().getPhone(), regularFont));
            leftCell.addElement(new Paragraph("E-mail: " + bill.getRestaurant().getEmail(), regularFont));
            metaTable.addCell(leftCell);

            // Right Cell: Bill metadata
            PdfPCell rightCell = new PdfPCell();
            rightCell.setBorder(Rectangle.NO_BORDER);
            rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            Paragraph billNumPara = new Paragraph("Facture #: " + bill.getBillNumber(), boldFont);
            billNumPara.setAlignment(Element.ALIGN_RIGHT);
            rightCell.addElement(billNumPara);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            Paragraph datePara = new Paragraph("Date: " + bill.getIssuedAt().format(formatter), regularFont);
            datePara.setAlignment(Element.ALIGN_RIGHT);
            rightCell.addElement(datePara);
            
            String clientInfo = "Client: " + (bill.getOrder().getClientName() != null ? bill.getOrder().getClientName() : "Client Direct");
            Paragraph clientPara = new Paragraph(clientInfo, regularFont);
            clientPara.setAlignment(Element.ALIGN_RIGHT);
            rightCell.addElement(clientPara);

            metaTable.addCell(rightCell);
            document.add(metaTable);

            document.add(new Paragraph(" ")); // Spacer
            document.add(new Paragraph("DÉTAIL DE LA COMMANDE", sectionFont));
            document.add(new Paragraph(" ")); // Spacer

            // Order items table
            PdfPTable itemsTable = new PdfPTable(4);
            itemsTable.setWidthPercentage(100);
            itemsTable.setWidths(new float[]{4f, 2f, 2f, 2f});

            // Headers
            PdfPCell h1 = new PdfPCell(new Phrase("Article", headerFont));
            h1.setBackgroundColor(primaryColor);
            h1.setPadding(8);
            itemsTable.addCell(h1);

            PdfPCell h2 = new PdfPCell(new Phrase("Prix Unitaire", headerFont));
            h2.setBackgroundColor(primaryColor);
            h2.setPadding(8);
            itemsTable.addCell(h2);

            PdfPCell h3 = new PdfPCell(new Phrase("Quantité", headerFont));
            h3.setBackgroundColor(primaryColor);
            h3.setPadding(8);
            itemsTable.addCell(h3);

            PdfPCell h4 = new PdfPCell(new Phrase("Total", headerFont));
            h4.setBackgroundColor(primaryColor);
            h4.setPadding(8);
            itemsTable.addCell(h4);

            // Populate rows
            for (OrderItem item : bill.getOrder().getOrderItems()) {
                PdfPCell cellItem = new PdfPCell(new Phrase(item.getProduct().getName(), regularFont));
                cellItem.setPadding(6);
                itemsTable.addCell(cellItem);

                PdfPCell cellPrice = new PdfPCell(new Phrase(String.format("%,.0f FCFA", item.getPrice()), regularFont));
                cellPrice.setPadding(6);
                itemsTable.addCell(cellPrice);

                PdfPCell cellQty = new PdfPCell(new Phrase(String.valueOf(item.getQuantity()), regularFont));
                cellQty.setPadding(6);
                itemsTable.addCell(cellQty);

                double totalItem = item.getPrice() * item.getQuantity();
                PdfPCell cellTotal = new PdfPCell(new Phrase(String.format("%,.0f FCFA", totalItem), regularFont));
                cellTotal.setPadding(6);
                itemsTable.addCell(cellTotal);
            }

            document.add(itemsTable);
            document.add(new Paragraph(" ")); // Spacer

            // Bill Summary block
            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(40);
            summaryTable.setHorizontalAlignment(Element.ALIGN_RIGHT);

            summaryTable.addCell(new PdfPCell(new Phrase("Sous-Total:", regularFont)));
            summaryTable.addCell(new PdfPCell(new Phrase(String.format("%,.0f FCFA", bill.getSubTotal()), regularFont)));

            summaryTable.addCell(new PdfPCell(new Phrase("TVA (18%):", regularFont)));
            summaryTable.addCell(new PdfPCell(new Phrase(String.format("%,.0f FCFA", bill.getTaxAmount()), regularFont)));

            PdfPCell finalTotalHeader = new PdfPCell(new Phrase("Total Général:", boldFont));
            finalTotalHeader.setBackgroundColor(new Color(240, 240, 240));
            summaryTable.addCell(finalTotalHeader);

            PdfPCell finalTotalVal = new PdfPCell(new Phrase(String.format("%,.0f FCFA", bill.getTotalAmount()), boldFont));
            finalTotalVal.setBackgroundColor(new Color(240, 240, 240));
            summaryTable.addCell(finalTotalVal);

            document.add(summaryTable);

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            
            Paragraph footer = new Paragraph("Merci de votre visite et à bientôt chez " + bill.getRestaurant().getName() + " !", regularFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }
}
