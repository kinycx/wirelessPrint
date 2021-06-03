package com.example.wirelessprint;

import android.graphics.pdf.PdfDocument;
import android.print.PrintDocumentAdapter;

import android.content.Context;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class MyPrintDocumentAdapter extends PrintDocumentAdapter {

    Context applicationContext;
    String path;

    public MyPrintDocumentAdapter(Context applicationContext, String path) {
        this.applicationContext = applicationContext;
        this.path = path;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes,
                         PrintAttributes newAttributes,
                         CancellationSignal cancellationSignal,
                         LayoutResultCallback callback,
                         Bundle metadata) {
        // Create a new PdfDocument with the requested page attributes
        //pdfDocument = new PrintedPdfDocument(applicationContext, newAttributes);

        // Respond to cancellation request
        if (cancellationSignal.isCanceled() ) {
            callback.onLayoutCancelled();
        }else {
            PrintDocumentInfo.Builder builder = new PrintDocumentInfo.Builder("file_name");
            builder.setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                    .build();
            // Otherwise report an error to the print framework
            callback.onLayoutFailed("calcolo delle pagine a puttane");
        }

    }

    @Override
    public void onWrite(final PageRange[] pageRanges,
                        final ParcelFileDescriptor destination,
                        final CancellationSignal cancellationSignal,
                        final WriteResultCallback callback) {
        // Iterate over each page of the document,
        // check if it's in the output range.
        InputStream  in = null;
        OutputStream out = null;
        try{
            File file = new File(path);
            in = new FileInputStream(file);
            out = new FileOutputStream(destination.getFileDescriptor());

            byte[] buff = new byte[16384];
            int size;
            while ((size = in.read(buff))>= 0 && !cancellationSignal.isCanceled()){
                out.write(buff,0,size);
            }
            if (cancellationSignal.isCanceled())
                callback.onWriteCancelled();
            else {
                callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
            }




        } catch (Exception e) {
            callback.onWriteFailed(e.getMessage());
            Log.e("Bro", e.getMessage());
            e.printStackTrace();
        }
        finally {
            try{
                in.close();
                out.close();
            }catch (IOException ex){
                Log.e("Bro", "" + ex.getMessage());
            }
        }

        callback.onWriteFinished(pageRanges);
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onFinish() {
        super.onFinish();
    }


}
