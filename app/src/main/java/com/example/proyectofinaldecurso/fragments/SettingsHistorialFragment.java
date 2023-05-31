package com.example.proyectofinaldecurso.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.proyectofinaldecurso.R;
import com.example.proyectofinaldecurso.data.DatabaseHelper;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileOutputStream;

public class SettingsHistorialFragment extends Fragment {

    private DatabaseHelper db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inicializa tu DatabaseHelper
        db = new DatabaseHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings_historial, container, false);

        // Obtén una referencia al botón en tu layout
        ImageButton deleteButton = view.findViewById(R.id.imageButtonDeleteBDSettings);
        Button generatePdfButton = view.findViewById(R.id.buttonPdf);

        // Agrega un OnClickListener al botón
        deleteButton.setOnClickListener(v -> {
            // Lógica para eliminar los datos de las tablas
            deleteDataDialog();
        });
        generatePdfButton.setOnClickListener(v -> {

            try {
                generarPdf();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

        });

        return view;
    }

    private void generarPdf() throws ParseException {
        // db.exportarCSV(getActivity());
        db.exportToCSV();
        Toast.makeText(getActivity(), "Se ha creado el csv", Toast.LENGTH_SHORT).show();
        String prueba2 = "/storage/emulated/0/Download/csvaux/historyCashfy.csv";
        // String prueba1 = "/storage/emulated/0/SQLiteBackup/Backup.csv";
        convertCsvToPdf(prueba2, "/storage/emulated/0/Download/pdfaux/pdfCashfy.pdf");
    }

    public void convertCsvToPdf(String csvFile, String pdfFile) {
        try {
            // Crear un nuevo documento
            Document document = new Document();

            // Crear un PdfWriter
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

            // Crear un evento de pie de página para mostrar el número de página y la fecha
            FooterPageEvent event = new FooterPageEvent();
            writer.setPageEvent(event);

            // Abrir el documento
            document.open();

            // Obtén el contexto de la actividad
            Context context = getActivity();

            // Obtiene el ID de recursos de la imagen
            int imageResourceId = R.drawable.logomemoria; // Reemplaza "nombre_de_la_imagen" con el nombre real de la imagen en res/drawable

            // Crea una instancia de la clase Image utilizando el ID de recursos
            Drawable drawable = ContextCompat.getDrawable(context, imageResourceId);
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image headerImage = Image.getInstance(stream.toByteArray());
            headerImage.scaleToFit(150, 150); // Ajusta el tamaño de la imagen según tus necesidades

            // Agrega el encabezado al documento
            document.add(headerImage);

            // Leer el archivo CSV
            BufferedReader br = new BufferedReader(new FileReader(csvFile));
            String line;
            PdfPTable table = null;

            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                // Dividir la línea en campos
                String[] fields = line.split(",");

                // Si es la primera línea, inicializamos la tabla
                if (lineNumber == 0) {
                    table = new PdfPTable(fields.length);
                }

                // Añadir los campos a la tabla
                for (String field : fields) {
                    table.addCell(field);
                }

                lineNumber++;
            }

            // Añadir la tabla al documento
            if (table != null) {
                document.add(table);
            }

            // Cerrar el BufferedReader y el Document
            br.close();
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteDataDialog() {
        // Aquí puedes agregar la lógica para eliminar los datos de las tablas utilizando tu DatabaseHelper
        // Por ejemplo, puedes llamar a los métodos de eliminación en tu DatabaseHelper según tus necesidades
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Confirmar eliminación");
        builder.setMessage("¿Estás seguro de que quieres eliminar este registro?");

        builder.setPositiveButton("Sí", (dialog, which) -> {
            db.deleteAllRecords();
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            // Si el usuario hace clic en "No", simplemente cierra el diálogo
            dialog.dismiss();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private class FooterPageEvent extends PdfPageEventHelper {
        private Font pageNumberFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);
        private Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            int pageNumber = writer.getPageNumber();
            Phrase pageNumberPhrase = new Phrase("Página " + pageNumber, pageNumberFont);

            float x = (document.right() + document.left()) / 2;
            float y = document.bottomMargin() - 10;
            ColumnText.showTextAligned(writer.getDirectContent(),
                    Element.ALIGN_CENTER, pageNumberPhrase, x, y, 0);

            // Obtén la fecha actual
            Date currentDate = new Date();
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String formattedDate = dateFormat.format(currentDate);

            // Crea una instancia de la clase Phrase con la fecha
            Phrase datePhrase = new Phrase("Fecha: " + formattedDate, dateFont);

            // Añade la frase al encabezado
            float xDate = document.right() - 50;
            ColumnText.showTextAligned(writer.getDirectContent(),
                    Element.ALIGN_RIGHT, datePhrase, xDate, y, 0);
        }
    }
}
