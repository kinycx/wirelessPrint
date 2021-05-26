package com.example.wirelessprint;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.content.Intent;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Set;
import java.util.UUID;
import android.os.Handler;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfWriter;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.concurrent.RunnableFuture;
import java.util.logging.LogRecord;

public class MainActivity extends AppCompatActivity {

    Button mCreatePDF;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCreatePDF = (Button) findViewById(R.id.createPDF);

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mCreatePDF.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                createPDF(Common.getAppPath(MainActivity.this) + "testPDF.pdf");
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();

    }

    private void createPDF(String path) {

        if (new File(path).exists())
            new File(path).delete();

        try {
            Document document = new Document();

            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();
            document.setPageSize(PageSize.A6);
            document.addCreationDate();
            document.addAuthor("BroUser");
            document.addCreator("Niky");

            BaseColor colorAccent = new BaseColor(0, 153, 204, 255);
            float fontSize = 20.0f;
            float valueFontSize = 26.0f;

            BaseFont fontName = BaseFont.createFont("assets/fonts/Times.otf","UTF-8" , BaseFont.EMBEDDED);

            Font titleFont = new Font(fontName, 36.0f, Font.NORMAL, BaseColor.BLACK);
            addNewItem(document, "Order Detail", Element.ALIGN_CENTER, titleFont);

            Font orderNumberFont = new Font(fontName, fontSize, Font.NORMAL, colorAccent);
            addNewItem(document, "Order No: ", Element.ALIGN_LEFT, orderNumberFont);

            document.close();

            Toast.makeText(this, "success", Toast.LENGTH_SHORT);

            printPDF();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }

    }

    private void printPDF() {

        PrintManager printManager = (PrintManager)getSystemService(Context.PRINT_SERVICE);
        try{
            PrintDocumentAdapter printDocumentAdapter = new MyPrintDocumentAdapter(MainActivity.this, Common.getAppPath(MainActivity.this) + "testPDF.pdf");
            printManager.print("Document", printDocumentAdapter, new PrintAttributes.Builder().build());
        }catch (Exception ex) {
            Log.e("BroUser", "" + ex.getMessage());
        }

    }

    private void addNewItem(Document document, String text, int alignCenter, Font font) throws DocumentException {

        Chunk chunk = new Chunk(text, font);
        Paragraph paragraph = new Paragraph(chunk);
        paragraph.setAlignment(alignCenter);
        document.add(paragraph);

    }
}





