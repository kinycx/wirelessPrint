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
            return;
        }

        // Compute the expected number of printed pages
        int pages = computePageCount(newAttributes);

        if (pages > 0) {
            // Return print information to print framework
            PrintDocumentInfo info = new PrintDocumentInfo
                    .Builder("print_output.pdf")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(pages)
                    .build();
            // Content layout reflow is complete
            callback.onLayoutFinished(info, true);
        } else {
            // Otherwise report an error to the print framework
            callback.onLayoutFailed("Page count calculation failed.");
        }

    }

    @Override
    public void onWrite(final PageRange[] pageRanges,
                        final ParcelFileDescriptor destination,
                        final CancellationSignal cancellationSignal,
                        final WriteResultCallback callback) {

        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(new File("somefile.pdf"));
            output = new FileOutputStream(destination.getFileDescriptor());
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }

            if(cancellationSignal.isCanceled())
                callback.onWriteCancelled();
            else{
                callback.onWriteFinished(new PageRange[]{
                        PageRange.ALL_PAGES
                });
            }

        } catch (Exception e) {
            callback.onWriteFailed(e.getMessage());
            Log.e("Bro", e.getMessage());
            e.printStackTrace();

        } finally {
            try {
                input.close();
                output.close();
            } catch (IOException e) {
                Log.e("Bro", e.getMessage());
                e.printStackTrace();
            }
        }

    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onFinish() {
        super.onFinish();
    }

    private int computePageCount(PrintAttributes printAttributes) {
        int itemsPerPage = 4; // default item count for portrait mode
        // default item count for portrait mode

        PrintAttributes.MediaSize pageSize = printAttributes.getMediaSize();
        if (!pageSize.isPortrait()) {
            // Six items per page in landscape orientation
            itemsPerPage = 6;
        }

        // Determine number of print items
        int printItemCount = 1;

        return (int) Math.ceil(printItemCount / itemsPerPage);
    }
}
