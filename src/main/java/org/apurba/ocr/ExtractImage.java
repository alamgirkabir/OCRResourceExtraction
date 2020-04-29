/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apurba.ocr;

/**
 *
 * @author alamgir
 */
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.FilenameUtils;

public class ExtractImage {

    private static final String PDF_DIRECTORY = "/home/alamgir/Desktop/PDF-DIR";
    private static final String OUTPUT_DIR = "/home/alamgir/Desktop/PDF-OUTPUT";

    public static void main(String[] args) throws Exception {

        File f = new File(PDF_DIRECTORY);
        if (f.isDirectory()) {
            try (Stream<Path> walk = Files.walk(Paths.get(PDF_DIRECTORY))) {

                List<String> result = walk.filter(Files::isRegularFile)
                        .map(x -> x.toString()).collect(Collectors.toList());

                result.forEach(fileName -> {
                    File pdfFile = new File(fileName);
                    
                    String imgDirName = FilenameUtils.removeExtension(pdfFile.getName()).replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
                    File imageDir = new File(OUTPUT_DIR + File.separator + imgDirName );
                    if(!imageDir.exists()){
                        imageDir.mkdir();
                    }
                    int pageIndex = 0;
                    try (final PDDocument document = PDDocument.load(pdfFile)) {
                        PDPageTree list = document.getPages();
                        for (PDPage page : list) {
                            PDResources pdResources = page.getResources();
                            pageIndex ++;
                            for (COSName name : pdResources.getXObjectNames()) {
                                PDXObject o = pdResources.getXObject(name);
                                if (o instanceof PDImageXObject) {
                                    PDImageXObject image = (PDImageXObject) o;
                                    String filename = imageDir.getAbsolutePath() + File.separator + pageIndex + ".png";
                                    ImageIO.write(image.getImage(), "png", new File(filename));
                                }
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        System.err.println("Exception while trying to create pdf document - " + e);
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
